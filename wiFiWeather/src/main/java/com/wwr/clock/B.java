package com.wwr.clock;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import com.mpw.constant.MyApplication;
import com.mrwujay.cascade.service.FirstEvent;
import com.mrwujay.cascade.service.SecordEvent;
import com.mrwujay.cascade.service.ThreeEvent;
import com.wifi.utils.BaiduLocation;
import com.wifi.utils.City;
import com.wifi.utils.LocationResultBean;
import com.wwr.clock.R;
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
import android.os.Message;
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

public class B extends Activity {
	private ListAdapter adapter;
	private ListView personList;
	private TextView overlay;
	private MyLetterListView letterListView;
	private HashMap<String, Integer> alphaIndexer;
	private String[] sections;
	private Handler handler;
	private String cityname;
	private OverlayThread overlayThread;
	public  ArrayList<City> allCity_lists;
	private ArrayList<City> ShowCity_lists;
	private ArrayList<City> city_lists;
	private String lngCityName = "";
	private EditText sh;
	private LinearLayout lng_city_lay;
	private ProgressDialog progress;
	private static final int SHOWDIALOG = 2;
	private static final int DISMISSDIALOG = 3;
	private static final int LOCATION = 4;
	MainActivity maActivity;
	Thread mythread;
	Handler handler0;
	private TextView tv_city1, tv_city2, tv_city3;
	private String cn = "";	//定位城市返回的数据国家

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initOverlay();
		setContentView(R.layout.activity_searchcity);
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
		sh = (EditText) findViewById(R.id.sh);
		letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
		handler = new Handler();
		overlayThread = new OverlayThread();
		personList.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				arg2 = arg2 - 1;
				String cn = "00";
				
				// 将_替换成" "
				cityname = ShowCity_lists.get(arg2).getName().replace("_", " ");
				// 传递城市编码以及城市拼音到主界面
				EventBus.getDefault().post(new FirstEvent(cityname));
				EventBus.getDefault().post(new ThreeEvent(cn));

				SharedPreferences sp_chooce = getSharedPreferences("Citys", Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = sp_chooce.edit();
				editor.putString("toService", ShowCity_lists.get(arg2).getPinyi().replace("_", " "));
				editor.commit();

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

		/**
		 * 在获取城市列表之后
		 */
//		handler2.sendEmptyMessage(SHOWDIALOG);
	//		Thread thread = new Thread() {
	//			@Override
	//			public void run() {
	//				// 先家在本地数据
	//				initCity();
	//				handler2.sendEmptyMessage(DISMISSDIALOG);
	//
	//				// 本地数据加载完成之后 开始定位
	//				// LocationResultBean lrb = new Location().getCity();
	//
	////				Message msg = new Message();
	////				msg.obj = new Location().getCity();
	////				msg.what = LOCATION;
	////				handler2.sendMessage(msg);
	//
	//				super.run();
	//			}
	//		};
	//		thread.start();
		initCity();
		handler2.sendEmptyMessage(DISMISSDIALOG);
		new BaiduLocation(getApplicationContext(), handler2);
		
		// 本地数据加载完成之后 开始定位
//						LocationResultBean lrb = new LocationResultBean();
//						lrb.setResltCode(1);
//						lrb.setCoutry("no");
//						List<City> list = new ArrayList<City>();
//						list.add((MyApplication.FORE_LIST.get(1)));
//						lrb.setLocationCitys(list);
//						Message msg = new Message();
//						msg.obj = lrb;
//						msg.what = LOCATION;
//						handler2.sendMessage(msg);
		
		
	}

	/**
	 * 获取所有城市
	 */
	public void initCity() {
		allCity_lists = (ArrayList<City>) MyApplication.FORE_LIST;
		city_lists = allCity_lists;
		ShowCity_lists = city_lists;
	}

//	private ArrayList<City> getCityList() {
//		ArrayList<City> list = new ArrayList<City>();
//		try {
//			chineseCities = new JSONArray(getResources().getString(R.string.Citys));
//			for (int i = 0; i < chineseCities.length(); i++) {
//				JSONObject jsonObject = chineseCities.getJSONObject(i);
//				City city = new City(jsonObject.getString("name").replace("_", " "), jsonObject.getString("pinyin"), jsonObject.getString("zip"));
//				list.add(city);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		Collections.sort(list, comparator);
//		return list;
//	}

	/**
	 * a-z
	 */
	Comparator<City> comparator = new Comparator<City>() {
		@Override
		public int compare(City lhs, City rhs) {
			return lhs.getPinyi().trim().compareTo(rhs.getPinyi().trim());
		}
	};

	public int Mycompare(City lhs, City rhs) {
		String str1 = lhs.getPinyi();
		String str2 = rhs.getPinyi();
		String a = str1.substring(0, 1);
		String b = str2.substring(0, 1);
		int flag = a.compareTo(b);
		if (flag == 0) {
			if (str1.length() > 1 && str2.length() > 1) {
				lhs.setPinyi(str1.substring(1));
				rhs.setPinyi(str2.substring(1));
				int c = Mycompare(lhs, rhs);
				return c;
			} else {
				if (str1.length() == 1) {
					return 1;
				} else {
					return -1;
				}
			}
		} else {
			return flag;
		}

	}

	// private void setAdapter(List<City> list) {
	// adapter = new ListAdapter(this, list);
	// personList.setAdapter(adapter);
	// }

	public class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		final int VIEW_TYPE = 3;

		public ListAdapter(Context context) {
			this.inflater = LayoutInflater.from(context);
			alphaIndexer = new HashMap<String, Integer>();
			sections = new String[ShowCity_lists.size()];
			for (int i = 0; i < ShowCity_lists.size(); i++) {
				String currentStr = getAlpha(ShowCity_lists.get(i).getPinyi());
				String previewStr = (i - 1) >= 0 ? getAlpha(ShowCity_lists.get(i - 1).getName()) : " ";
				if (!previewStr.equals(currentStr)) {
					String name = getAlpha(ShowCity_lists.get(i).getName());
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
			// TODO Auto-generated method stub
			int type = 2;

			if (position == 0 && sh.getText().length() == 0) {// 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷风姸鎬侀敓鏂ゆ嫹
				type = 0;
			}
			return type;
		}

		@Override
		public int getViewTypeCount() {// 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷疯閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷疯閿熸枻鎷烽敓鍙鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓閰碉綇鎷烽敓鏉拌揪鎷峰皬涓洪敓鏂ゆ嫹閿熼叺纰夋嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓閾版唻鎷�
			return VIEW_TYPE;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
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
			String currentStr = getAlpha(ShowCity_lists.get(position).getPinyi());
			String previewStr = (position - 1) >= 0 ? getAlpha(ShowCity_lists.get(position - 1).getPinyi()) : " ";
			if (!previewStr.equals(currentStr)) {
				holder.alpha.setVisibility(View.VISIBLE);
				if (currentStr.equals("#")) {
					currentStr = "閿熸枻鎷烽敓鑴氱鎷烽敓鏂ゆ嫹";
				}
				holder.alpha.setText(currentStr);
			} else {
				holder.alpha.setVisibility(View.GONE);
			}
			// }
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
				handler.postDelayed(overlayThread, 1500);
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
		if (str == null) {
			return "#";
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
		// TODO Auto-generated method stub
		super.onDestroy();
	}

//	private class MyLocationListenner implements BDLocationListener {
//		@Override
//		public void onReceiveLocation(BDLocation location) {
//
//			if (location == null)
//				return;
//			StringBuffer sb = new StringBuffer(256);
//			if (location.getLocType() == BDLocation.TypeGpsLocation) {
//				// sb.append(location.getAddrStr());
//				sb.append(location.getCity());
//			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
//				sb.append(location.getCity());
//			}
//			if (sb.toString() != null && sb.toString().length() > 0) {
//				lngCityName = sb.toString();
//				lng_city.setText(lngCityName);
//			}
//
//		}
//
//		public void onReceivePoi(BDLocation poiLocation) {
//
//		}
//	}

	
	
	
	Handler handler2 = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SHOWDIALOG:
//				progress = AppUtil.showProgress(B.this, "Loading medium");
				break;
			case DISMISSDIALOG:
//				if (progress != null) {
//					progress.dismiss();
//				}

				/**
				 * 本地数据加载完毕
//				 */
//				tv_city1.setText("正在定位");/*
//				tv_city2.setVisibility(View.GONE);
//				tv_city3.setVisibility(View.GONE);*/

				personList.setAdapter(null);
				adapter = new ListAdapter(B.this);

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

								if (j == 0) {
									sb.append(Character.toUpperCase(c));
								} else {
									sb.append(Character.toLowerCase(c));
								}
							}
							str = sb.toString();

							ArrayList<City> changecity = new ArrayList<City>();
							for (int i = 0; i < city_lists.size(); i++) {
								if (city_lists.get(i).name.indexOf(str) != -1) {
									changecity.add(city_lists.get(i));
								}
							}
							ShowCity_lists = changecity;
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
			AlertDialog.Builder builder = new AlertDialog.Builder(B.this); // 先得到构造器
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
