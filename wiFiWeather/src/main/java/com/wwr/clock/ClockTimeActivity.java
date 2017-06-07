package com.wwr.clock;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

public class ClockTimeActivity extends Activity {
	private String strTimeFormat;
	private TimePicker timePick1;
	private Button buttone1;
	private int mResultCode = 1;
	private String ampm;
	private int hour;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settime);
		
		timePick1 = (TimePicker) findViewById(R.id.timePic1);  //一整个选择时间的按钮
		
		buttone1 = (Button) findViewById(R.id.buttone1);//设置时间按钮
		
		OnChangeListener buc = new OnChangeListener();
		//设置TimePicker空间不弹出软键盘
		timePick1.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
		buttone1.setOnClickListener(buc);	//set time 按钮  获取数据并且回传
		//又是获得系统的时间制式
		ContentResolver cv = this.getContentResolver();
		strTimeFormat = android.provider.Settings.System.getString(cv,
				android.provider.Settings.System.TIME_12_24);
		if (("24").equals(strTimeFormat)) {
			//设置24小时制式
			timePick1.setIs24HourView(true);
		} else {
			timePick1.setIs24HourView(false);
		}
		
		findViewById(R.id.back).setOnClickListener(buc);
		
		//一个
//		timePick1.setOnTimeChangedListener(new TimeListener());
		
	}

	class OnChangeListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			settime();
		}
	}
	
	//设置时间的具体方法
	public void settime(){
		// TODO Auto-generated method stub
					//获得时间和分钟
					int h = timePick1.getCurrentHour();
					int m = timePick1.getCurrentMinute();

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
					
					String alltime = String.format("%02d", h) + ":"+ String.format("%02d", m);

					String time = String.format("%02d", h) + ":"+ String.format("%02d", m);		
					//回传给选择时间和闹钟界面的数据
					Intent intent = new Intent();
					intent.putExtra("time", time);//没取到数据？
					intent.putExtra("ampm", ampm);
					intent.putExtra("alltime", alltime);
					setResult(RESULT_OK, intent);
					finish();
	}
	
//	@Override
//	protected void onResume() {
//	
//		super.onResume();
//	}
//	
	
	@Override
	protected void onRestart() {
		ContentResolver cv = this.getContentResolver();
		strTimeFormat = android.provider.Settings.System.getString(cv,
				android.provider.Settings.System.TIME_12_24);
		if (("24").equals(strTimeFormat)) {
			// 设置24小时制式
			timePick1.setIs24HourView(true);
		} else {
			timePick1.setIs24HourView(false);
		}
		
		super.onRestart();
	}
	
	//将返回键禁用
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {	
			//返回主界面之前更新数据
			settime();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	class TimeListener implements OnTimeChangedListener {
		public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {	
				
		}
	}

}