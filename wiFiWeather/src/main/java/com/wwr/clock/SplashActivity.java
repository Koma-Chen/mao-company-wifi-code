package com.wwr.clock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.mpw.constant.Constant;
import com.umeng.message.PushAgent;
import com.wifi.utils.ParseXml;
import com.wifi.utils.UpdataInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends Activity {
	private ProgressDialog progressDialog;
	// 设备的版本号
	private int versionCode;
	// 解析xml文件获得的对象
	private UpdataInfo ui;
	private boolean isUpdata = false;

	// 当arg1为0的时候则是睡眠完了直接跳转至主界面 当arg1为1的时候则表示解析xml文件成功
	Handler handler = new Handler() {
		private UpdataInfo ui2;
		public void handleMessage(android.os.Message msg) {
			if (msg.arg1 == 1) {
				ui2 = (UpdataInfo) msg.obj;
				if (isUpdata) {
					// 创建更新提示
					AlertDialog.Builder builder = new AlertDialog.Builder(
							SplashActivity.this); // 先得到构造器
					builder.setTitle(getString(R.string.alert_title_new_version)); // 设置标题
					builder.setMessage(getString(R.string.alert_message_wether_update)); // 设置内容
					builder.setIcon(R.drawable.ic_logo28);// 设置图标，图片id即可
					builder.setPositiveButton(getString(R.string.alert_button_update_now),
							new DialogInterface.OnClickListener() { // 设置确定按钮
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									Toast.makeText(SplashActivity.this, getString(R.string.alert_button_being_update),
											Toast.LENGTH_SHORT).show();
									initDialog();
									
									MyTast mt = new MyTast(ui2.getUrl());
									mt.execute();
								}
							});
					// 不更新则退出
					builder.setNegativeButton("Exit",
							new DialogInterface.OnClickListener() { // 设置取消按钮
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							});
					builder.setCancelable(false);
					// 参数都设置完成了，创建并显示出来
					builder.create().show();
				}
			} else if (msg.arg1 == 0) {
				Intent intent = new Intent();
				intent.setClass(SplashActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		}
	};
	private int maxSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);
		PushAgent.getInstance(this).onAppStart();
		// 获取设备版本号
		try {
			versionCode = this.getPackageManager().getPackageInfo(
					getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}

		Thread thread = new Thread() {
			@Override
			public void run() {
				// 检查设备版本
				checkVersion();
				if (!isUpdata) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Message msg = new Message();
					msg.arg1 = 0;
					handler.sendMessage(msg);

				}
				super.run();
			}
		};
		thread.start();
		//开一个线程下载图片
		new Thread(){
			public void run() {
				downImg();
			};
		}.start();;
	}

	public void checkVersion() {
		
		// 先从网络获取服务器上apk的版本号
		URL url = null;
		try {	
			url = new URL(Constant.XmlPath);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(2000);	//设置三秒超时
			conn.setReadTimeout(2000);
			conn.connect();
			// 获取流
			InputStream is = conn.getInputStream();
			
			
//			System.out.println("sssssssssssss"+conn.getHeaderField(0));
//			long start = System.currentTimeMillis();
//			while (is.available() == 0) {
//				
////				System.out.println("进来了    11111111111111111111");
//				
//				if ((System.currentTimeMillis() - start) > 5 * 1000) {
//					isUpdata = false;
//					return;
//				}
//			}
			
			
			
			ui = new ParseXml().parse(is);
			
			
			
			
			if (Integer.parseInt(ui.getVersion()) > versionCode) {
				isUpdata = true;
			} else {
				isUpdata = false;
			}
		

			// 使用 message传递消息给主线程
			Message message = new Message();
			message.arg1 = 1;
			message.obj = ui;
			handler.sendMessage(message);

		} catch (MalformedURLException e) {
			return;
		} catch (IOException e) {
			return;
		}

	}
	
	public void initDialog(){
		progressDialog=new ProgressDialog(this); 
        progressDialog.setIcon(R.drawable.ic_logo28);  
        progressDialog.setTitle("Downloading...");  
        progressDialog.setMessage("Please wait a minute!");  
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//设置进度条对话框//样式（水平，旋转）  
        //显示  
        progressDialog.setCancelable(false);
        progressDialog.show();  
        //必须设置到show之后   
	}
	//下载文件
	public File downApk(String path) {
		File file = null;
		try {
			URL url = new URL(path);
			// 打开连接
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			InputStream is = conn.getInputStream();
			maxSize = conn.getContentLength();
			progressDialog.setMax(maxSize);
			// 判定SD卡是否能用 能用则下载文件
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				File sdcard = Environment.getExternalStorageDirectory();

				file = new File(sdcard, "update.apk");
				// 获得输出流
				FileOutputStream fos = new FileOutputStream(file);

				// 将文件读取至本地
				int len = 0;
				byte[] buffer = new byte[512];
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
					progressDialog.setProgress(progressDialog.getProgress()+len);
					
				}
				// 清除缓存
				fos.flush();
				fos.close();
				is.close();
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;

	}

	//异步下载更新的文件
	public class MyTast extends AsyncTask {
		String path;

		public MyTast(String path) {
			this.path = path;
		}

		@Override
		protected Object doInBackground(Object... arg0) {
			return downApk(path);
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			File file1 = null;
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				File sdcard = Environment.getExternalStorageDirectory();
				file1 = new File(sdcard, "update.apk");
			}
			//下载完成之后自动安装
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file1),
					"application/vnd.android.package-archive");
			startActivity(intent);
			progressDialog.dismiss();
			finish();

		}
	}
	
	//下载图片
	public static void downImg() {
		File file ;
		try {
			// 判定SD卡是否能用 能用则下载文件
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				File sdcard = Environment.getExternalStorageDirectory();

				file = new File(sdcard, "share.png");
				//判断文件是否已经存在  如果已经存在的话  则直接退出这个方法
				if(file.exists()){
					
					Log.e("LOG", "文件存在");
					return;		
				}
				Log.e("LOG", "文件不存在");
				URL url = new URL(Constant.ImgPath);
				// 打开连接
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(2000);
				InputStream is = conn.getInputStream();
				
				// 获得输出流
				FileOutputStream fos = new FileOutputStream(file);

				// 将文件读取至本地
				int len = 0;
				byte[] buffer = new byte[1024];
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				// 清除缓存
				fos.flush();
				fos.close();
				is.close();

			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	

	}
	
	

}
