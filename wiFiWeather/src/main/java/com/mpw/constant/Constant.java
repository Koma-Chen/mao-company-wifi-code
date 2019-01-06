package com.mpw.constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Constant {
    /**
     * 定义全局变量
     */
    public final static String[] sex = {"3 Hour", "6 Hour", "12 Hour", "24 Hour"};
    public static final List<InfoBean> mlistIndoor = new ArrayList<InfoBean>();
    public static final List<InfoBean> mlistOutdoor = new ArrayList<InfoBean>();
    public static boolean haveOutMoudle = true;

    public final static String ImgPath = "https://app.moscase8.com/apps/8001D/Android%20WiFi%20Weather.png";
    public final static String XmlPath = "https://app.moscase8.com/apps/8001D/Wi-FiWeather.xml";
    public static boolean isDestory = false;

    public static void test() {
        for (int i = 0; i < 10; i++) {
            Random r = new Random();
            InfoBean ib = new InfoBean();
            ib.setTempC(r.nextDouble() * 100);
            ib.setTempF(r.nextDouble() * 100);

            ib.setHumidity((int) (r.nextDouble() * 100));

            ib.setTime("now");
            mlistIndoor.add(ib);
        }
    }
}
