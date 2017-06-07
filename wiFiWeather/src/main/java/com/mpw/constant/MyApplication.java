package com.mpw.constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.wifi.utils.City;
import com.wwr.clock.R;

import android.app.Application;
import android.util.Log;


public class MyApplication extends Application{
	public static List<City> CN_LIST;
	public static List<City> FORE_LIST;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e("程序启动了","程序启动了");
		
		PushAgent mPushAgent = PushAgent.getInstance(this);
		//注册推送服务，每次调用register方法都会回调该接口
		mPushAgent.register(new IUmengRegisterCallback() {

		    @Override
		    public void onSuccess(String deviceToken) {
		        //注册成功会返回device token
		    }

		    @Override
		    public void onFailure(String s, String s1) {

		    }
		});
		
		
		new Thread(){
			public void run() {
				CN_LIST = getCityList(1);
				FORE_LIST = getCityList(0);
			};
		}.start();
		
		
		//放在程序入口
		//初始化讯飞语音
		SpeechUtility.createUtility(this, SpeechConstant.APPID +"=58759745");
		
		
		
	}
	
	

	/**
	 * 在程序开始的时候就读取本地数据 
	 * 
	 */
	
	private ArrayList<City> getCityList(int flag) {
		JSONArray chineseCities = null;
		ArrayList<City> list = new ArrayList<City>();
		try {
			if(flag==0){//国外
				 chineseCities = new JSONArray(getResources().getString(R.string.Citys));
			}else{//国内
				chineseCities = new JSONArray(getResources().getString(R.string.citys));
			}
			
			for (int i = 0; i < chineseCities.length(); i++) {
				JSONObject jsonObject = chineseCities.getJSONObject(i);
				City city = new City(jsonObject.getString("name").replace("_", " "), jsonObject.getString("pinyin"), jsonObject.getString("zip"));
				list.add(city);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Collections.sort(list, comparator);
		return list;
	}
	
	/**
	 * a-z 排序  根据拼音排序
	 */
	Comparator<City> comparator = new Comparator<City>() {
		@Override
		public int compare(City lhs, City rhs) {
			return lhs.getPinyi().trim().compareTo(rhs.getPinyi().trim());
		}
	};
	
}
