package com.wwr.clock.temp;

import android.os.Handler;

/**
 * Created by Administrator on 2017/6/6.
 */

public interface GetTempFromServer {

    void getTemp(String content,int id, Handler handler);
}
