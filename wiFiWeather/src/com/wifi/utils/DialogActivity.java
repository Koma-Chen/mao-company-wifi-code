package com.wifi.utils;

import com.wwr.clock.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DialogActivity extends Dialog {
	private TextView textView;
	public DialogActivity(Context context) {
		super(context, R.style.MyDialogStyle);
	}
	public DialogActivity(Context context, int theme) {
		super(context, theme);
	}
	protected void onCreate(Bundle saveInstanceState) {
    super.onCreate(saveInstanceState);
    setContentView(R.layout.dialog);
    textView=(TextView)findViewById(R.id.tv);
    LinearLayout layout=(LinearLayout)this.findViewById(R.id.LinearLayout);
    layout.getBackground().setAlpha(120);
	}
}
