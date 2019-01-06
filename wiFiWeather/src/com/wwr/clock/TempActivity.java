package com.wwr.clock;

import com.mpw.constant.Constant;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class TempActivity extends Activity {

	private TempView view;
	boolean isStart = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.temp_view);
		LinearLayout ll = (LinearLayout) findViewById(R.id.ll_temp_view);
		view = new TempView(this);
		isStart = true;
		Constant.isDestory = false;
		ll.setBackgroundColor(Color.WHITE);
		ll.addView(view);
	}
	
	public void finish(View view){
		finish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Constant.isDestory = true;
		Log.e("生命周期","onDestroy");
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Constant.isDestory = false;
		Log.e("生命周期","onStart");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Constant.isDestory = true;
		view.stopThread();
		Log.e("生命周期","onstop");
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		view.toStart();
		Log.e("生命周期","onRestart");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.e("生命周期","onResume");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.e("生命周期","onPause");
	}
	
	
	

}
