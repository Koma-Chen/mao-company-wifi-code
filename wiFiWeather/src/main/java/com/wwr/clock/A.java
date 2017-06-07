package com.wwr.clock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import com.mpw.constant.MyApplication;
import com.mrwujay.cascade.service.FirstEvent;
import com.mrwujay.cascade.service.SecordEvent;
import com.mrwujay.cascade.service.ThreeEvent;
import com.umeng.message.PushAgent;
import com.wifi.utils.BaiduLocation;
import com.wifi.utils.City;
import com.wifi.utils.LocationResultBean;
import com.wwr.locationselect.MyLetterListView;
import com.wwr.locationselect.MyLetterListView.OnTouchingLetterChangedListener;
import de.greenrobot.event.EventBus;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class A extends Activity {
	private ListAdapter adapter;
	private ListView personList;
	private TextView overlay;
	private MyLetterListView letterListView;
	private HashMap<String, Integer> alphaIndexer;
	private String[] sections;
	private Handler handler;
	private String cityname, cityid;
	private OverlayThread overlayThread;
	public static ArrayList<City> allCity_lists;
	private ArrayList<City> ShowCity_lists;
	private ArrayList<City> city_lists;
	private String lngCityName = "";
//	private JSONArray chineseCities;
	private EditText sh;
	private LinearLayout lng_city_lay;
	private ProgressDialog progress;
	private static final int SHOWDIALOG = 2;
	private static final int DISMISSDIALOG = 3;
//	private int locateProcess = 1;
//	private ImageView imageView;
	public static final int LOCATION = 4;
	MainActivity maActivity;
	Thread mythread;
	Handler handler0;
	private TextView tv_city1, tv_city2, tv_city3;  //显示定位的三个view
	private String cn = "";	//定位城市返回的数据国家
	private Context appContext;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 initOverlay();
		setContentView(R.layout.activity_selectcity);
		
		appContext  = getApplicationContext();
		
		
		PushAgent.getInstance(this).onAppStart();
		personList = (ListView) findViewById(R.id.list_view);
		
		/**
		 * 添加定位的显示
		 */
		View headerView = LayoutInflater.from(this).inflate(R.layout.city_list_header_view, null);

		tv_city1 = (TextView) headerView.findViewById(R.id.tv_city1);
		tv_city2 = (TextView) headerView.findViewById(R.id.tv_city2);
		tv_city3 = (TextView) headerView.findViewById(R.id.tv_city3);

		
		
		tv_city1.setBackgroundResource(R.drawable.text_view_border);
		tv_city2.setBackgroundResource(R.drawable.text_view_border);
		tv_city3.setBackgroundResource(R.drawable.text_view_border);
		
		tv_city1.setText(getString(R.string.local_later));
		tv_city2.setVisibility(View.GONE);
		tv_city3.setVisibility(View.GONE);

		

		personList.addHeaderView(headerView, null, false);
		
		
		allCity_lists = new ArrayList<City>();

		letterListView = (MyLetterListView) findViewById(R.id.MyLetterListView01);
		lng_city_lay = (LinearLayout) findViewById(R.id.lng_city_lay);

		lng_city_lay.setVisibility(View.GONE);

		sh = (EditText) findViewById(R.id.sh);
		letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
		handler = new Handler();
		overlayThread = new OverlayThread();
		personList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				arg2 = arg2 -1 ;
				
				String cn = "01";
				cityname = ShowCity_lists.get(arg2).getName();
				cityid = ShowCity_lists.get(arg2).getzip().toString();
				EventBus.getDefault().post(new FirstEvent(cityname));
				EventBus.getDefault().post(new SecordEvent(cityid));
				EventBus.getDefault().post(new ThreeEvent(cn));
				finish();
			}
		});
		lng_city_lay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("lngCityName", lngCityName);
				setResult(99, intent);
				finish();
			}
		});

//		handler2.sendEmptyMessage(SHOWDIALOG);
//		Thread thread = new Thread() {
//			@Override
//			public void run() {
//				hotCityInit();
//				handler2.sendEmptyMessage(DISMISSDIALOG);
//				
//				
//				// 本地数据加载完成之后 开始定位   定位完全实现封装   在handler2里面处理结果即可
//				
//				// LocationResultBean lrb = new Location().getCity();
//
////				Message msg = new Message();
////				msg.obj = new Location().getCity();
////				msg.what = LOCATION;
////				handler2.sendMessage(msg);
//				
////				tv_city2.setVisibility(View.VISIBLE);
//
//				super.run();
//			}
//		};
//		thread.start();
		hotCityInit();
		handler2.sendEmptyMessage(DISMISSDIALOG);
		new BaiduLocation(appContext, handler2);
//		LocationResultBean lrb = new LocationResultBean();
//		lrb.setResltCode(1);
//		lrb.setCoutry("no");
//		List<City> list = new ArrayList<City>();
//		list.add((MyApplication.FORE_LIST.get(1)));
//		lrb.setLocationCitys(list);
//		Message msg = new Message();
//		msg.obj = lrb;
//		msg.what = LOCATION;
//		handler2.sendMessage(msg);
	}

	public void hotCityInit() {
		allCity_lists = (ArrayList<City>) MyApplication.CN_LIST;
		city_lists = allCity_lists;
		ShowCity_lists = city_lists;
	}


	public class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		final int VIEW_TYPE = 3;

		public ListAdapter(Context context) {
			this.inflater = LayoutInflater.from(context);
			alphaIndexer = new HashMap<String, Integer>();
			sections = new String[ShowCity_lists.size()];
			for (int i = 0; i < ShowCity_lists.size(); i++) {
				String currentStr = getAlpha(ShowCity_lists.get(i).getPinyi());
				String previewStr = (i - 1) >= 0 ? getAlpha(ShowCity_lists.get(i - 1).getPinyi()) : " ";
				if (!previewStr.equals(currentStr)) {
					String name = getAlpha(ShowCity_lists.get(i).getPinyi());
					alphaIndexer.put(name, i);
					sections[i] = name;
				}
			}
		}

		@Override
		public int getCount() {
			return ShowCity_lists.size();
		}

		@Override
		public Object getItem(int position) {
			return ShowCity_lists.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			int type = 2;
			if (position == 0 && sh.getText().length() == 0) {
				type = 0;
			}
			return type;
		}

		@Override
		public int getViewTypeCount() {
			return VIEW_TYPE;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
//			int viewType = getItemViewType(position);
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.list_item, null);
				holder = new ViewHolder();
				holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.name.setText(ShowCity_lists.get(position).getName());
			String currentStr = getAlpha(ShowCity_lists.get(position).getPinyi());// 閿熸枻鎷烽敓鏂ゆ嫹鎷奸敓鏂ゆ嫹
			String previewStr = (position - 1) >= 0 ? getAlpha(ShowCity_lists.get(position - 1).getPinyi()) : " ";// 閿熸枻鎷蜂竴閿熸枻鎷锋嫾閿熸枻鎷�
			if (!previewStr.equals(currentStr)) {// 閿熸枻鎷蜂竴閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷风ず
				holder.alpha.setVisibility(View.VISIBLE);
				if (currentStr.equals("#")) {
					currentStr = "姝ｅ湪鍔犺浇 ";
				}
				holder.alpha.setText(currentStr);
			} else {
				holder.alpha.setVisibility(View.GONE);
			}
			return convertView;
		}

		private class ViewHolder {
			TextView alpha;
			TextView name;
		}
	}

	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) inflater.inflate(R.layout.overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);
		WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}

	private class LetterListViewListener implements OnTouchingLetterChangedListener {
		@Override
		public void onTouchingLetterChanged(final String s) {
			if (alphaIndexer.get(s) != null) {
				int position = alphaIndexer.get(s);
				personList.setSelection(position);
				overlay.setText(sections[position]);
				overlay.setVisibility(View.VISIBLE);
				handler.removeCallbacks(overlayThread);
				handler.postDelayed(overlayThread, 1000);
			}
		}

	}

	private class OverlayThread implements Runnable {
		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
		}
	}

	private String getAlpha(String str) {

		if (str.equals("-")) {
			return "&";
		}
		if (str.trim().length() == 0) {
			return "#";
		}
		char c = str.trim().substring(0, 1).charAt(0);
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase();
		} else {
			return "#";
		}
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
//		mLocationClient.stopLocation();	//停止定位
//		mLocationClient.onDestroy();	//销毁
	}

	Handler handler2 = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SHOWDIALOG:
//				progress = AppUtil.showProgress(A.this, "Loading medium");
				break;
			case DISMISSDIALOG:
//				if (progress != null) {
//					
//					progress.dismiss();
//					
//				}
				personList.setAdapter(null);
				adapter = new ListAdapter(A.this);

				personList.setAdapter(adapter);

				sh.addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {

					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
						if (s.length() > 0) {
							String str = s.toString();
							StringBuffer sb = new StringBuffer();
							for (int j = 0; j < str.length(); j++) {
								char c = str.charAt(j);
								sb.append(Character.toLowerCase(c));
							}
							str = sb.toString();
							ArrayList<City> changecity = new ArrayList<City>();
							for (int i = 0; i < city_lists.size(); i++) {
								if (city_lists.get(i).getPinyi().indexOf(str) != -1) {
									changecity.add(city_lists.get(i));
								} else if (city_lists.get(i).name.indexOf(str) != -1) {
									changecity.add(city_lists.get(i));
								}
								ShowCity_lists = changecity;
							}
						} else {
							ShowCity_lists = allCity_lists;
						}
						adapter.notifyDataSetChanged();
					}
				});
				break;
			case LOCATION:
				LocationResultBean lrb = (LocationResultBean)msg.obj;
				int resultCode = lrb.getResltCode();
				if(resultCode==-1){		//定位失败
					tv_city1.setText(getString(R.string.location_faild));
					
				}else if(resultCode==0){////定位成功定位成功，本地无匹配到城市，雅虎有返回	点击能返回
					if("CN".equals(lrb.getCoutry())){
						cn="01";
						
					}else{
						cn="00";
					}
					List<City> citys = lrb.getLocationCitys();
					tv_city1.setText(citys.get(0).getName());
					tv_city1.setTag(citys.get(0));
					tv_city1.setOnClickListener(new locationClickListenger());
					
					tv_city2.setVisibility(View.GONE);
					tv_city3.setVisibility(View.GONE);
				}else if(resultCode==-2){//定位成功定位成功，本地无匹配到城市，雅虎无返回	点击弹窗
					List<City> citys = lrb.getLocationCitys();
					tv_city1.setText(citys.get(0).getName());
					tv_city1.setOnClickListener(new NoMatchClickListenger());
					
					tv_city2.setVisibility(View.GONE);
					tv_city3.setVisibility(View.GONE);
				}else{
					if("CN".equals(lrb.getCoutry())){
						cn="01";
					}else{
						cn="00";
					}
					
					List<City> citys = lrb.getLocationCitys();
					if(citys.size()==1){	//定位成功切匹配一个城市	点击能返回
						tv_city1.setText(citys.get(0).getName());
						tv_city1.setTag(citys.get(0));
						tv_city1.setOnClickListener(new locationClickListenger());
						
						tv_city2.setVisibility(View.GONE);
						tv_city3.setVisibility(View.GONE);
						
					}else if(citys.size()==2){//定位成功切匹配2个城市	点击能返回

						tv_city1.setText(citys.get(0).getName());
						tv_city1.setTag(citys.get(0));
						tv_city1.setOnClickListener(new locationClickListenger());
						tv_city2.setText(citys.get(1).getName());
						tv_city1.setTag(citys.get(1));
						tv_city2.setOnClickListener(new locationClickListenger());
						tv_city3.setVisibility(View.GONE);
						tv_city2.setVisibility(View.VISIBLE);
					}else if(citys.size()==3){//定位成功切匹配3个城市	点击能返回

						tv_city1.setText(citys.get(0).getName());
						tv_city1.setTag(citys.get(0));
						tv_city1.setOnClickListener(new locationClickListenger());
						
						tv_city2.setText(citys.get(1).getName());
						tv_city1.setTag(citys.get(1));
						tv_city1.setOnClickListener(new locationClickListenger());
						
						tv_city3.setText(citys.get(2).getName());
						tv_city1.setTag(citys.get(2));
						tv_city1.setOnClickListener(new locationClickListenger());
						
						tv_city2.setVisibility(View.VISIBLE);
						tv_city3.setVisibility(View.VISIBLE);
					}
				}
				break;
			default:
				break;
			}
		};
	};
	
	
	
	/**
	 * 定位成功的城市的点击事件
	 * @author lxj
	 *
	 */
	class locationClickListenger implements OnClickListener{
		@Override
		public void onClick(View v) {
			City city = (City) v.getTag();
//			
////			String cn = "00";
//			// 将_替换成" "
//			cityname = city.getName().replace("_", " ");
//			// 传递城市编码以及城市拼音到主界面
//			EventBus.getDefault().post(new FirstEvent(cityname));
//			EventBus.getDefault().post(new ThreeEvent(cn));
//
//			SharedPreferences sp_chooce = getSharedPreferences("Citys", Activity.MODE_PRIVATE);
//			SharedPreferences.Editor editor = sp_chooce.edit();
//			editor.putString("toService", city.getPinyi().replace("_", " "));
//			editor.commit();
//
//			finish();
			
			
			if("00".equals(cn)){
				// 将_替换成" "
				cityname = city.getName().replace("_", " ");
				// 传递城市编码以及城市拼音到主界面
				EventBus.getDefault().post(new FirstEvent(cityname));
				EventBus.getDefault().post(new ThreeEvent(cn));

				SharedPreferences sp_chooce = getSharedPreferences("Citys", Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = sp_chooce.edit();
				editor.putString("toService", city.getPinyi().replace("_", " "));
				editor.commit();

				finish();
			}else{
				cityname = city.getName();
				String cityid = city.getzip().toString();
				EventBus.getDefault().post(new FirstEvent(cityname));
				EventBus.getDefault().post(new SecordEvent(cityid));
				EventBus.getDefault().post(new ThreeEvent(cn));
				finish();
			}
		}
		
	}
	
	/**
	 * 定位成功但无匹配城市的点击事件
	 * @author lxj
	 *
	 */
	class NoMatchClickListenger implements OnClickListener{
		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(A.this); // 先得到构造器
			builder.setMessage(getString(R.string.no_match_city)); // 设置内容
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // 设置确定按钮
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.create().show();
		}
		
	}

	
}
