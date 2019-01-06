package com.mpw.constant;

import android.app.Application;
import android.util.Log;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.wifi.utils.City;
import com.wifi.utils.CrashHandler;
import com.wifi.utils.LocationResultBean;
import com.wwr.clock.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MyApplication extends Application {
    public static List<City> CN_LIST;
    public static List<City> FORE_LIST;
    public static boolean isEnterPosition;

    public static String city;

    public static String data;

    public static String json;

    public static String xieyi;

    public static String key = "QCHM8P2V8IgqMD7bjJKgxn+GoIZAfP/fzZ6Iy4/iuS5Pf";

    public static String jiemikey =
            "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJ96nymNhlFOHuQDXqwbM44UCR67Q7z" +
					"+HR6IJnxeuuMFHL6AXgVFyMRg0B9zta4yJZbLV" +
					"/N31ZnKwJXjs81HbS05SRboCCLSkTTGJBfNcF9nKarW3gj" +
					"+2EayiWKZHk399asjELAyZFtNjVUUDKmKsgAvrfehvhEEfN43SGL" +
					"//0YVAgMBAAECgYAhOTVxPaeOxpyO2djeN" +
					"+h8n9Xqjg6lj7bXRnvmCwIaC1WapjlwFcKmjpitBLkDqjTEfmY7NWrNWDzYvRAXItsua8UcyvVuMxSIirqzSu0uqxuWvWds+AFkSiqg6c2FE1gvoW54NYJZybwKnHzTQD4Mq8DBer069HUrxRsuGqGSYQJBAPd4zcmaSa80/PGwwF7qKuFHDdtYqitmRs46wtayHoZjC9CLSOFIKn9vLWr4J1yzWR3vMSWjaU6FvDOioU/l0ekCQQCk+Yt5s783JZ+z1sZZAjDXq31ejst+2vC7IfV/Sa/V044/czQg5GcHpbul3ZfkNL/S2SJVgJtqlg62/x4JlStNAkAynUaZoTDI4PGLDTeLMiGrCblPz4aeccCxlVscRjnwCnn0IUi6quPmRHrpke+bCiOD99P5er6jDL89YFnV2Y9hAkBorQnJc7p9FDtL0ZfSvCgff+kxSStnVyXtprNtS2TtKKmWKtPFlwakfI0exgZtPucDstAtLkfujj3R8PevHt31AkEAq3SallEvkcsGrFh6TLAQEjTaMdvN9PREz3Thl1IAn9q46tAFdI8Hr/8u/XzIpFIJzfxy40usjItU1tX65Bg+HA==";

    public static LocationResultBean sLrb;

    public static boolean isFinish;

    public static String server = "120.25.207.192";
    public static int port = 8818;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("程序启动了", "程序启动了");

        CrashHandler.getInstance().init(this);

        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token


            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });


        new Thread() {
            public void run() {
                CN_LIST = getCityList(1);
                FORE_LIST = getCityList(0);
            }

            ;
        }.start();


        //放在程序入口
        //初始化讯飞语音
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=599ba5d6");


    }


    /**
     * 在程序开始的时候就读取本地数据
     */

    private ArrayList<City> getCityList(int flag) {
        Log.d("koma", "正在读取城市");
        JSONArray chineseCities = null;
        JSONArray chineseCities1 = null;
        JSONArray chineseCities2 = null;
        JSONArray chineseCities3 = null;
        JSONArray chineseCities4 = null;
        JSONArray chineseCities5 = null;
        ArrayList<City> list = new ArrayList<City>();

        try {
            if (flag == 0) {//国外
                chineseCities = new JSONArray(getResources().getString(R.string.CityOne));
                chineseCities1 = new JSONArray(getResources().getString(R.string.CityTwo));
                chineseCities2 = new JSONArray(getResources().getString(R.string.CityThree));

                for (int i = 0; i < chineseCities.length(); i++) {
                    if (chineseCities.getJSONObject(i) == null || i == 599)
                        continue;
                    JSONObject jsonObject = chineseCities.getJSONObject(i);

                    City city = new City(jsonObject.getString("name"), jsonObject.getString("pinyin")
                            , jsonObject.getString("zip"));
                    list.add(city);
                }
                for (int i = 0; i < chineseCities1.length(); i++) {
                    if (chineseCities.getJSONObject(i) == null || i ==587)
                        continue;
                    JSONObject jsonObject = chineseCities1.getJSONObject(i);

                    City city = new City(jsonObject.getString("name"), jsonObject.getString("pinyin")
                            , jsonObject.getString("zip"));
                    list.add(city);
                }
                for (int i = 0; i < chineseCities2.length(); i++) {
                    if (chineseCities.getJSONObject(i) == null || i == 114)
                        continue;
                    JSONObject jsonObject = chineseCities2.getJSONObject(i);

                    City city = new City(jsonObject.getString("name"), jsonObject.getString("pinyin")
                            , jsonObject.getString("zip"));
                    list.add(city);
                }
            } else {//国内
                chineseCities = new JSONArray(getResources().getString(R.string.cityOne));
                chineseCities1 = new JSONArray(getResources().getString(R.string.cityTwo));
                chineseCities2 = new JSONArray(getResources().getString(R.string.cityThree));
                chineseCities3 = new JSONArray(getResources().getString(R.string.cityFour));
                chineseCities4 = new JSONArray(getResources().getString(R.string.cityFive));
                chineseCities5 = new JSONArray(getResources().getString(R.string.citySix));
                for (int i = 0; i < chineseCities.length(); i++) {
                    if (i == 447)
                        continue;
                    JSONObject jsonObject = chineseCities.getJSONObject(i);

                    City city = new City(jsonObject.getString("name"), jsonObject.getString("pinyin")
                            , jsonObject.getString("zip"));
                    list.add(city);
                }
                for (int i = 0; i < chineseCities1.length(); i++) {
                    if (i == 449)
                        continue;
                    JSONObject jsonObject = chineseCities1.getJSONObject(i);

                    City city = new City(jsonObject.getString("name"), jsonObject.getString("pinyin")
                            , jsonObject.getString("zip"));
                    list.add(city);
                }
                for (int i = 0; i < chineseCities2.length(); i++) {
                    if (i == 450)
                        continue;
                    JSONObject jsonObject = chineseCities2.getJSONObject(i);

                    City city = new City(jsonObject.getString("name"), jsonObject.getString("pinyin")
                            , jsonObject.getString("zip"));
                    list.add(city);
                }for (int i = 0; i < chineseCities3.length(); i++) {
                    if (i == 450)
                        continue;
                    JSONObject jsonObject = chineseCities3.getJSONObject(i);

                    City city = new City(jsonObject.getString("name"), jsonObject.getString("pinyin")
                            , jsonObject.getString("zip"));
                    list.add(city);
                }
                for (int i = 0; i < chineseCities4.length(); i++) {
                    if (i == 450)
                        continue;
                    JSONObject jsonObject = chineseCities4.getJSONObject(i);

                    City city = new City(jsonObject.getString("name"), jsonObject.getString("pinyin")
                            , jsonObject.getString("zip"));
                    list.add(city);
                }
                for (int i = 0; i < chineseCities5.length(); i++) {
                    if (i == 318)
                        continue;
                    JSONObject jsonObject = chineseCities5.getJSONObject(i);

                    City city = new City(jsonObject.getString("name"), jsonObject.getString("pinyin")
                            , jsonObject.getString("zip"));
                    list.add(city);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.sort(list, comparator);
        return list;
    }

    /**
     * a-z 排序  根据拼音排序
     */
    Comparator<City> comparator = new Comparator<City>() {
        @Override
        public int compare(City lhs, City rhs) {
            return lhs.getPinyi().trim().compareTo(rhs.getPinyi().trim());
        }
    };


}
