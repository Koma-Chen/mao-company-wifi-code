package com.wwr.clock;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.SimpleFormatter;

import com.mrwujay.cascade.service.Event;
import com.mrwujay.cascade.service.FiveEvent;
import com.mrwujay.cascade.service.LevelEvent;
import com.mrwujay.cascade.service.SexEvent;
import com.mrwujay.cascade.service.ZeorEvent;
import com.wifi.utils.ToggleStatus;

import de.greenrobot.event.EventBus;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SelectTimeActivity extends Activity {
	Button btntime;
	private String hour, ampm;
	private String Alarm, Snooze, time, alarm, snooze, clocktime, alarmtime,time24, time12;
	private ToggleButton Alarmbtn1,Alarmbtn2, Snoozebtn;
	private Calendar c = Calendar.getInstance();
	private ImageView imback;
	int mResultCode;
	Boolean bool;
	ToggleStatus status = new ToggleStatus();
	TimePickerDialog timePicker;

	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	String selecttime, result;
	Intent mResultData;
	private boolean alarm1,alarm2,isSnooze;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		

		/**
		 * 从本地文件中获取闹钟的设置
		 * 默认的数据都是关闭的
		 */
		preferences = getSharedPreferences("togglebuttonstatus",
				Context.MODE_PRIVATE);
		alarm1 = preferences.getBoolean("alarm1",false);		
		alarm2 = preferences.getBoolean("alarm2",false);
		isSnooze = preferences.getBoolean("isSnooze",false);
		editor = preferences.edit();
		
		
		
		btntime = (Button) findViewById(R.id.timeselector);
		imback = (ImageView) findViewById(R.id.imgage);
		Alarmbtn1 = (ToggleButton) findViewById(R.id.AlarmBtn1);
		Alarmbtn2 = (ToggleButton) findViewById(R.id.AlarmBtn2);
		Snoozebtn = (ToggleButton) findViewById(R.id.SnoozeBtn);
		
		initTime();
		
		
		Alarmbtn1.setChecked(alarm1);
		Alarmbtn2.setChecked(alarm2);
		Snoozebtn.setChecked(isSnooze);
		
		if(!alarm1&&!alarm2){
			Snoozebtn.setClickable(false);
		}
		
		//
		setListener();
		this.setTheme(R.style.ActionBar_title_style);
		imback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				updata();
			}
		});
		//设置选择时间的点击按钮
		btntime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//跳转至选择时间的点击按钮
				startActivityForResult(new Intent(SelectTimeActivity.this, ClockTimeActivity.class), 1);
			}
		});
	}
	
	//更新MainActivity里面的数据
	public void updata(){
		time = result;
		if (time != null) {
			EventBus.getDefault().post(new FiveEvent(time));
		}
		alarm1 = Alarmbtn1.isChecked();
		alarm2 = Alarmbtn2.isChecked();
		isSnooze = Snoozebtn.isChecked();
		if(alarm1||alarm2){
			Alarm = "ON";
		}else{
			Alarm = "OFF";
		}
		Snooze = isSnooze?"ON":"OFF";
		editor.putBoolean("alarm1",alarm1);		
		editor.putBoolean("alarm2",alarm2);
		editor.putBoolean("isSnooze",isSnooze);
		editor.commit();	
		
		EventBus.getDefault().post(new Event(ampm));
		EventBus.getDefault().post(new SexEvent(clocktime));
		EventBus.getDefault().post(new LevelEvent(Alarm));
		EventBus.getDefault().post(new ZeorEvent(Snooze));
		finish();
	}
	

	// 接收其他Activity的信息
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//会出现空指针一场 
		result = data.getExtras().getString("time");
		
		//
		ampm = data.getExtras().getString("ampm");
		clocktime = data.getExtras().getString("alltime");
		
		if(ampm!=null){
			btntime.setText(ampm+" "+result);
		}else{
			btntime.setText(result);
		}
		
//		Toast.makeText(SelectTimeActivity.this,""+ampm, Toast.LENGTH_LONG).show();
		// Toast.makeText(getApplicationContext(), ampm, 3000).show();
	}



	public void  initTime(){
		int h;
		int m;
		String ampm = null;
		
		ContentResolver cv = this.getContentResolver();
		String strTimeFormat = android.provider.Settings.System.getString(cv,
				android.provider.Settings.System.TIME_12_24);

		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		String data = formatter.format(new Date());
		h = Integer.parseInt(data.split(":")[0]);
		m = Integer.parseInt(data.split(":")[1]);

		Log.e("获得的时间为   h"+h,"		m	"+m);
		SharedPreferences spAlarm = getSharedPreferences("alarm",Activity.MODE_PRIVATE);
		Editor edit = spAlarm.edit();
		edit.putString("alarm",(String.format("%02d", h) + ":"+ String.format("%02d", m)));
		edit.commit();
		
		//如果是12小时制  则判断时间
		if ("12".equals(strTimeFormat) ) {
			if (h> 12) {
				ampm = "PM";
				h=h-12;
			}else if(h==12){
				ampm = "PM";
				
			}
			else if (h < 12) {
				ampm = "AM";
				if (h == 0) {
					h = 12;
				}
			}
			
		}
		
		if(ampm!=null){
			btntime.setText(ampm+" "+String.format("%02d", h) + ":"+ String.format("%02d", m));
		}else{
			btntime.setText(String.format("%02d", h) + ":"+ String.format("%02d", m));
		}
		
		
	}
	

	private void setListener() {
		//闹钟开关的点击按钮
		Alarmbtn1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean check) {
				status.setOne(check); 
				//闹钟1打开  闹钟2关闭     贪睡按钮能点击
				if (check) {
					Alarmbtn2.setChecked(false);
					Snoozebtn.setClickable(true);
				} else {
					//闹钟2打 闹钟1关闭      贪睡按钮能点击开 
					if(Alarmbtn2.isChecked()){
						Snoozebtn.setClickable(true);
					}else{
						//闹钟都关闭的时候    强制贪睡关闭  并且贪睡不能点击
						Snoozebtn.setChecked(false);
						Snoozebtn.setClickable(false);
					}
					
				}
			}
		});
		
		
		
		Alarmbtn2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean check) {
				status.setOne(check);
				if (check) {
					Alarmbtn1.setChecked(false);
					Snoozebtn.setClickable(true);
				} else {
					if(Alarmbtn1.isChecked()){
						Snoozebtn.setClickable(true);
					}else{
						Snoozebtn.setChecked(false);
						Snoozebtn.setClickable(false);
					}
					
				}
			}
		});
		//贪睡的点击事件
		Snoozebtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean check) {

			}
		});

	}


	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {	
			//返回主界面之前更新数据
			updata();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
