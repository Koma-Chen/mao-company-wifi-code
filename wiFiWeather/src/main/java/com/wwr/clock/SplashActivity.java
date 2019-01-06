package com.wwr.clock;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static anetwork.channel.http.NetworkSdkSetting.context;

public class SplashActivity extends Activity {
    private ProgressDialog progressDialog;
    // 设备的版本号
    private int versionCode;
    // 解析xml文件获得的对象
    private UpdataInfo ui;
    private boolean isUpdata = false;
    private UpdataInfo ui2;

    private static final String PATH =
            Environment.getExternalStorageDirectory() + "/Crash/log/";


    private SharedPreferences mSharedPreferences;


    // 当arg1为0的时候则是睡眠完了直接跳转至主界面 当arg1为1的时候则表示解析xml文件成功
    Handler handler = new Handler() {


        public void handleMessage(android.os.Message msg) {
            this.obtainMessage();
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
                                    File path = Environment.getDataDirectory();
                                    StatFs stat = new StatFs(path.getPath());
                                    long blockSize = stat.getBlockSize();
                                    long availableBlocks = stat.getAvailableBlocks();
                                    if (availableBlocks * blockSize < 60000000) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                                SplashActivity.this); // 先得到构造器
                                        builder.setMessage("Unable to download the apk, please " +
                                                "retry after delete some items"); // 设置内容
                                        builder.setPositiveButton("Management application", new
                                                DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface
                                                                                dialogInterface,
                                                                        int i) {
                                                        Intent intent = new Intent();
                                                        intent.setAction("android.intent.action" +
                                                                ".MAIN");
                                                        intent.setClassName("com.android" +
                                                                ".settings", "com" +
                                                                ".android.settings" +
                                                                ".ManageApplications");
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });

                                        builder.setNegativeButton("Cancle", new DialogInterface
                                                .OnClickListener() {


                                            @Override
                                            public void onClick(DialogInterface dialogInterface,
                                                                int i) {
                                                finish();
                                            }
                                        }).create().show();
                                    } else {
                                        dialog.dismiss();
                                        if (Build.VERSION.SDK_INT >= 23) {
                                            //判断当前系统的SDK版本是否大于23
                                            //如果当前申请的权限没有授权
                                            if (!(checkSelfPermission(Manifest.permission
                                                    .WRITE_EXTERNAL_STORAGE) == PackageManager
                                                    .PERMISSION_GRANTED)) {
                                                //第一次请求权限的时候返回false,
                                                // 第二次shouldShowRequestPermissionRationale返回true
                                                //如果用户选择了“不再提醒”永远返回false。
                                                if (shouldShowRequestPermissionRationale(android
                                                        .Manifest.permission.RECORD_AUDIO)) {
                                                    Toast.makeText(SplashActivity.this, "请授予权限",
                                                            Toast
                                                                    .LENGTH_LONG).show();
                                                }
                                                //请求权限
                                                requestPermissions(new String[]{Manifest
                                                                .permission.WRITE_EXTERNAL_STORAGE},
                                                        593596);
                                            } else {//已经授权了就走这条分支
                                                Toast.makeText(SplashActivity.this, getString(R
                                                                .string
                                                                .alert_button_being_update),
                                                        Toast.LENGTH_SHORT).show();
                                                initDialog();

                                                MyTast mt = new MyTast(ui2.getUrl());
                                                mt.execute();
                                            }
                                        } else {//已经授权了就走这条分支

                                            Toast.makeText(SplashActivity.this, getString(R.string
                                                            .alert_button_being_update),
                                                     Toast.LENGTH_SHORT).show();
                                            initDialog();
                                            MyTast mt = new MyTast(ui2.getUrl());
                                            mt.execute();
                                        }
                                    }
                                }
                            });
                    // 不更新则退出
                    builder.setNegativeButton(getString(R.string.exit),
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
        SharedPreferences temp = getSharedPreferences("language",MODE_PRIVATE);

        setLanguage(temp.getString("language",(Locale.getDefault().getLanguage().toString())));
        setContentView(R.layout.splash_layout);




        //每次启动APP会根据条件判断是否上传错误日志
        mSharedPreferences = getSharedPreferences("clock", MODE_PRIVATE);

//        Log.d("koma", "isCrash:" + mSharedPreferences.getBoolean("isCrash", false));
//        if (mSharedPreferences.getBoolean("isCrash", false)) {
//            uploadFile();
//            mSharedPreferences.edit().putBoolean("isCrash", false).commit();
//        }


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
    }

    public void checkVersion() {

        // 先从网络获取服务器上apk的版本号
        URL url = null;
        try {
            url = new URL(Constant.XmlPath);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(2000);    //设置三秒超时
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
//
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

    public void initDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.drawable.ic_logo28);
        progressDialog.setTitle(getString(R.string.alert_title_down_loading));
        progressDialog.setMessage(getString(R.string.alert_message_wiat));
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
                    progressDialog.setProgress(progressDialog.getProgress() + len);

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


            Intent intent = new Intent(Intent.ACTION_VIEW);
            //判断是否是AndroidN以及更高的版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID +
                        ".fileProvider", file1);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file1), "application/vnd.android" +
                        ".package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            startActivity(intent);
            //下载完成之后自动安装
//            Intent intent = new Intent();
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setAction(android.content.Intent.ACTION_VIEW);
//            intent.setDataAndType(Uri.fromFile(file1),
//                    "application/vnd.android.package-archive");
//            startActivity(intent);
//            progressDialog.dismiss();
            finish();

        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Toast.makeText(SplashActivity.this, getString(R.string
                        .alert_button_being_update),
                Toast.LENGTH_SHORT).show();
        initDialog();

        MyTast mt = new MyTast(ui2.getUrl());
        mt.execute();
    }


    public void uploadFile() {
        final File f = new File(PATH);
        // 判断路径是否存在
        if (!f.exists()) {
            return;
        }
        final List<File> files = Arrays.asList(f.listFiles());
        if (files.size() == 0) {
            return;
        }
        OkGo.<String>post("http://120.25.207.192:8080/apps/android/WiFiWeather/8001/servlet" +
                "/UploadHandleServlet")
                .tag(this)
                .isSpliceUrl(true)
                .addFileParams("file", files)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.d("koma", "body:" + response.body());
                        Log.d("koma", "上传成功");
                        // 删除成功后，删除本地 crash 文件
                        for (File file : files) {
                            boolean result = file.delete();
                            Log.d("koma", "文件删除");
                        }
//                        JSONObject jsonObject;
//                        try {
//                            jsonObject = new JSONObject(response.body());
//                            int retCode = jsonObject.optInt("ret");
//                            if (retCode == 0) {
//
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }

                    @Override
                    public void uploadProgress(Progress progress) {
                        Log.d("koma","progress:"+progress.toString());
                    }

                    @Override
                    public void onError(Response<String> response) {
                        Log.d("koma", "上传失败" + response.body());
                    }
                });
    }

    /**
     * 设置当前语言
     *
     * @param language
     */
    private void setLanguage(String language) {
        //获取当前资源对象
        Resources resources = getResources();
        //获取设置对象
        Configuration configuration = resources.getConfiguration();
        //获取屏幕参数
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        //设置本地语言
        configuration.locale = new Locale(language);
        resources.updateConfiguration(configuration, displayMetrics);
        //发送结束所有activity的广播
    }



}
