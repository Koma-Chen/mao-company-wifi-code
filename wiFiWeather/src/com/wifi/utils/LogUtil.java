package com.wifi.utils;

import android.util.Log;

public class LogUtil {
	private static final boolean isLog  = true;
	
	public  static final void LOG(String s){
		if(isLog){
			Log.e("日志输出",""+s);
		}
	}
}
