package com.wwr.clock;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.mpw.constant.Constant;
import com.wwr.clock.temp.OutdoorTempView;

public class TempActivity extends Activity {
    private IndoorTempView IndoorView;
    private OutdoorTempView OutoorView;
    boolean isStart = false;
    private int mId;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp_view);

        mId = getIntent().getExtras().getInt(IndoorTempView.KEY_ID, 0);

        LinearLayout ll = (LinearLayout) findViewById(R.id.ll_temp_view);
        if (mId == IndoorTempView.ID_FROM_INDOOR) {
            IndoorView = new IndoorTempView(this, mId);
        } else if (mId == IndoorTempView.ID_FROM_OUTDOOR) {
            OutoorView = new OutdoorTempView(this, mId);
        }
        isStart = true;
        Constant.isDestory = false;
        ll.setBackgroundColor(Color.WHITE);
        if (mId == IndoorTempView.ID_FROM_INDOOR) {
            ll.addView(IndoorView);
        } else if (mId == IndoorTempView.ID_FROM_OUTDOOR) {
            ll.addView(OutoorView);
        }
    }
    public void finish(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constant.isDestory = true;
        Log.e("生命周期", "onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Constant.isDestory = false;
        Log.e("生命周期", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Constant.isDestory = true;
        if (mId == IndoorTempView.ID_FROM_INDOOR) {
            IndoorView.stopThread();
        } else if (mId == IndoorTempView.ID_FROM_OUTDOOR) {
            OutoorView.stopThread();
        }

        Log.e("生命周期", "onstop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mId == IndoorTempView.ID_FROM_INDOOR) {
            IndoorView.toStart();
        } else if (mId == IndoorTempView.ID_FROM_OUTDOOR) {
            OutoorView.toStart();
        }

        Log.e("生命周期", "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("生命周期", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("生命周期", "onPause");
    }


}
