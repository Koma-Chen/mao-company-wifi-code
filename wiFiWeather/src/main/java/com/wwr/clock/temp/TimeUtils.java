package com.wwr.clock.temp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 获取当前时间
 */
public class TimeUtils {

    public static String getNowTime() {
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("HH:mm");
        String time = format.format(date);
        return time;
    }
}
