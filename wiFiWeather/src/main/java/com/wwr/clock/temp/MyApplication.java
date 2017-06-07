package com.wwr.clock.temp;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2017/6/7.
 */

public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext(){
        return mContext;
    }
}
