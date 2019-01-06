package com.wifi.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.mpw.constant.MyApplication;
import com.wwr.clock.A;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.mpw.constant.MyApplication.isEnterPosition;

public class BaiduLocation {
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private Context context;
    private Context mContext;
    private Handler handler;

    public static final int SUCCESS_JUST_ONE = 1; // 成功遍歷到城市 且本地匹配
    public static final int SUCCESS_NO = 0; // 成功 沒有遍歷到城市,雅虎有数据
    public static final int FAILD = -1; // 定位失敗
    public static final int YAHOO_FAILD = -2; // 成功，沒有遍歷到城市 雅虎无数据
    private LocationService locationService;


    Message msg = new Message();
    List<City> LoactionCitys = null;


    public BaiduLocation(Context context, Handler handler, Context mcontext) {
        this.context = context;
        this.handler = handler;
        this.mContext = mcontext;
        locationService = new LocationService(context);

        locationService.registerListener(mListener);

        locationService.setLocationOption(locationService.getDefaultLocationClientOption());

        locationService.start();
    }

    /**
     * 设置定位的参数  并且开始定位
     */
    private void initLocation() {
        /**
         * 初始化
         */
        mLocationClient = new LocationClient(context); // 声明LocationClient类
        mLocationClient.registerLocationListener(myListener); // 注册监听函数

        LocationClientOption option = new LocationClientOption();


//		option.setLocationMode(LocationMode.Battery_Saving);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//															// 当前是低功耗 网络定位
//		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
//		option.setScanSpan(0);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
//		option.setOpenGps(false);// 可选，默认false,设置是否使用gps
//		option.setLocationNotify(false);// 可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
//		option.setIsNeedLocationDescribe(false);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation
// .getLocationDescribe里得到，结果类似于“在北京天安门附近”
//		option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//		option.setIgnoreKillProcess(true);//
// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
//		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        option = new LocationClientOption();
        option.setLocationMode(LocationMode.Battery_Saving);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
        option.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
        option.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation
        // .getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setIsNeedAltitude(false);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用


        mLocationClient.setLocOption(option);

        mLocationClient.start();
    }

    /**
     * 定位监听  也即是定位的结果分析
     *
     * @author lxj
     */
    public class MyLocationListener implements BDLocationListener {
        Message msg = new Message();
        List<City> LoactionCitys = null;

        /**
         * 先判断定位成功 失败 -1
         * <p>
         * 成功 定位到的城市都转换成小写字母无空格的字符串
         * 国内: 遍历国内城市 无匹配 返回 -2 有匹配 返回 1
         * 国外： 遍历国外城市 无匹配 访问雅虎： 有返回数据 0 无返回数据 -2
         * 有匹配 返回 1 如果没有
         */
        @Override
        public void onReceiveLocation(BDLocation location) {

            LoactionCitys = new ArrayList<City>();
            msg = new Message();
            msg.what = A.LOCATION;
            LocationResultBean lrb = new LocationResultBean();
            lrb.setResltCode(-1);        //默认是失败   所以所有失败的情况不需要设定


//			if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
//			} else

            Log.d("koma===country",11111+"");
            if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                if (location.getCity() != null) {//定位成功但是没有城市   直接不管
                    final String city = location.getCity();
                    String afterCity = "";
                    Log.d("koma===country",location.getCountry());
                    if ("中国".equals(location.getCountry())) {    //定位结果是国内
                        lrb.setCoutry("CN");
                        if ("市".equals(city.charAt(city.length() - 1))) {
                            afterCity = city.substring(0, city.length() - 1);
                        } else {
                            afterCity = city;
                        }


                        for (City c : MyApplication.CN_LIST) {
                            if ((c.getName().indexOf(afterCity) != -1 || afterCity.indexOf(c
                                    .getName()) != -1)) {
                                LoactionCitys.add(c);
                            }
                        }

                        if (LoactionCitys.size() == 0) {        //无匹配
                            lrb.setResltCode(-2);
                            LoactionCitys.add(new City(city, city, city));
                            lrb.setLocationCitys(LoactionCitys);
                            new Thread() {
                                public void run() {
                                    new SendCity2Service(context).send(city);
                                }

                                ;
                            }.start();
                        } else {        //有匹配
                            lrb.setResltCode(1);
                            lrb.setLocationCitys(LoactionCitys);

                        }


                    } else {    //定位结果是国外
                        //全部转换成小写字母  并且将空格去掉
                        afterCity = toLower(city.replace(" ", ""));
                        lrb.setCoutry("NO");
                        // 国外
                        for (City c : MyApplication.FORE_LIST) {
                            if ((afterCity.indexOf(toLower(c.getPinyi().replace(" ", ""))) != -1)
                                    || (toLower(c.getPinyi().replace(" ", "")).indexOf(afterCity)
                                    != -1)) {
                                LoactionCitys.add(c);
                            }
                        }
                        if (LoactionCitys.isEmpty()) { // 定位到城市且本地库没有数据
                            // 查询雅虎天气是否有数据返回
                            String yahoo = "https://query.yahooapis" +
                                    ".com/v1/public/yql?q=select%20*%20from%20weather" +
                                    ".forecast%20where%20woeid%20in%20" +
                                    "(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22"
                                    + city + "%22)&format=json&env=store%3A%2F%2Fdatatables" +
                                    ".org%2Falltableswithkeys";
                            try {
                                URL url1 = new URL(yahoo);
                                HttpURLConnection conn1 = (HttpURLConnection) url1.openConnection();
                                InputStream is1 = conn1.getInputStream();
                                BufferedReader br1 = new BufferedReader(new InputStreamReader(is1));
                                StringBuffer sb1 = new StringBuffer();
                                String str1;
                                while ((str1 = br1.readLine()) != null) {
                                    sb1.append(str1);
                                }
                                System.out.println(sb1.toString());
                                if ("{".equals(String.valueOf(sb1.toString().charAt(0)))) {
                                    JSONObject object1 = new org.json.JSONObject(sb1.toString()
                                            .trim());
                                    JSONObject data = object1.getJSONObject("query");
                                    int count = data.getInt("count");
                                    if (count == 0) { // 雅虎查询不到该城市
                                        LoactionCitys.add(new City(city, city, city));
                                        lrb.setLocationCitys(LoactionCitys);
                                        lrb.setResltCode(-2);

                                    } else {// 雅虎能查询到该城市 返回0
                                        /**
                                         * 获取雅虎返回的国家
                                         */
                                        JSONObject results = data.getJSONObject("results"); // 结果
                                        JSONObject channel = results.getJSONObject("channel"); //
                                        // 具体数据
                                        String title = channel.getString("title"); // 具体数据
                                        String yahooCountry = title.substring(title.length() - 2);
//										System.out.println("定位到的国家	" + country);
                                        System.out.println("雅虎返回的国家" + yahooCountry);

//										if (country.equals(yahooCountry)) { // 定位到的国家和雅虎返回的国家相对比
//
//										}
//										lrb.setCoutry(country);
                                        LoactionCitys.add(new City(city, city, city));
                                        lrb.setLocationCitys(LoactionCitys);
                                        lrb.setResltCode(0);
                                    }
                                } else {
                                    LoactionCitys.add(new City(city, city, city));
                                    lrb.setLocationCitys(LoactionCitys);
                                    lrb.setResltCode(-2);
                                }
                                new Thread() {
                                    public void run() {
                                        new SendCity2Service(context).send(city);
                                    }

                                }.start();
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else { // 定位到城市且本地库有数据
//							lrb.setCoutry(country);
                            lrb.setLocationCitys(LoactionCitys);
                            lrb.setResltCode(1);
                        }
                    }

                }


//				// 运营商信息
//			} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
//				sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                //sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                //sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                //sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }


//			Message msg = new Message();
//			msg.obj = new Location().getCity();
//			msg.what = LOCATION;

            msg.obj = lrb;
            handler.sendMessage(msg);
            mLocationClient.stop();

        }

        @Override
        public void onConnectHotSpotMessage(String arg0, int arg1) {
            // TODO Auto-generated method stub

        }
    }


    /*****
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (!isEnterPosition) {
                isEnterPosition = true;
                LoactionCitys = new ArrayList<City>();
                msg.what = A.LOCATION;
                LocationResultBean lrb = new LocationResultBean();
//			lrb.setResltCode(-1);		//默认是失败   所以所有失败的情况不需要设定
                // TODO Auto-generated method stub
                if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                    LogUtil.LOG("进来了  有定位结果");

                    Log.d("koma===country",location.getCity());
                    // 网络定位结果
                    if (location.getCity() != null) {//定位成功但是没有城市   直接不管

                        LogUtil.LOG("进来了  有城市");


                        final String city = location.getCity();
                        String afterCity = "";
                        if ("中国".equals(location.getCountry())) {    //定位结果是国内
                            lrb.setCoutry("CN");
                            if ("市".equals(city.charAt(city.length() - 1))) {
                                afterCity = city.substring(0, city.length() - 1);
                            } else {
                                afterCity = city;
                            }


                            for (City c : MyApplication.CN_LIST) {
                                if ((c.getName().indexOf(afterCity) != -1 || afterCity.indexOf(c
                                        .getName()) != -1)) {
                                    LoactionCitys.add(c);
                                }
                            }

                            if (LoactionCitys.size() == 0) {        //无匹配
                                lrb.setResltCode(-2);
                                LoactionCitys.add(new City(city, city, city));
                                lrb.setLocationCitys(LoactionCitys);
                                new Thread() {
                                    public void run() {
                                        new SendCity2Service(context).send(city);
                                    }

                                    ;
                                }.start();
                            } else {        //有匹配
                                lrb.setResltCode(1);
                                lrb.setLocationCitys(LoactionCitys);

                            }


                        } else {    //定位结果是国外
                            //全部转换成小写字母  并且将空格去掉
                            afterCity = toLower(city.replace(" ", ""));
                            lrb.setCoutry("NO");
                            // 国外
                            for (City c : MyApplication.FORE_LIST) {
                                if ((afterCity.indexOf(toLower(c.getPinyi().replace(" ", ""))) !=
                                        -1) || (toLower(c.getPinyi().replace(" ", "")).indexOf
                                        (afterCity) != -1)) {
                                    LoactionCitys.add(c);
                                }
                            }
                            if (LoactionCitys.isEmpty()) { // 定位到城市且本地库没有数据
                                // 查询雅虎天气是否有数据返回
                                String yahoo = "https://query.yahooapis" +
                                        ".com/v1/public/yql?q=select%20*%20from%20weather" +
                                        ".forecast%20where%20woeid%20in%20" +
                                        "(select%20woeid%20from%20geo.places(1)" +
                                        "%20where%20text%3D%22"
                                        + city + "%22)&format=json&env=store%3A%2F%2Fdatatables" +
                                        ".org%2Falltableswithkeys";
                                try {
                                    URL url1 = new URL(yahoo);
                                    HttpURLConnection conn1 = (HttpURLConnection)
                                            url1.openConnection();
                                    InputStream is1 = conn1.getInputStream();
                                    BufferedReader br1 = new BufferedReader(new InputStreamReader
                                            (is1));
                                    StringBuffer sb1 = new StringBuffer();
                                    String str1;
                                    while ((str1 = br1.readLine()) != null) {
                                        sb1.append(str1);
                                    }
                                    System.out.println(sb1.toString());
                                    if ("{".equals(String.valueOf(sb1.toString().charAt(0)))) {
                                        JSONObject object1 = new org.json.JSONObject(sb1.toString
                                                ().trim());
                                        JSONObject data = object1.getJSONObject("query");
                                        int count = data.getInt("count");
                                        if (count == 0) { // 雅虎查询不到该城市
                                            LoactionCitys.add(new City(city, city, city));
                                            lrb.setLocationCitys(LoactionCitys);
                                            lrb.setResltCode(-2);

                                        } else {// 雅虎能查询到该城市 返回0
                                            /**
                                             * 获取雅虎返回的国家
                                             */
                                            JSONObject results = data.getJSONObject("results");
                                            // 结果
                                            JSONObject channel = results.getJSONObject("channel")
                                                    ; // 具体数据
                                            String title = channel.getString("title"); // 具体数据
                                            String yahooCountry = title.substring(title.length()
                                                    - 2);
//										System.out.println("定位到的国家	" + country);
                                            System.out.println("雅虎返回的国家" + yahooCountry);

//										if (country.equals(yahooCountry)) { // 定位到的国家和雅虎返回的国家相对比
//
//										}
//										lrb.setCoutry(country);
                                            LoactionCitys.add(new City(city, city, city));
                                            lrb.setLocationCitys(LoactionCitys);
                                            lrb.setResltCode(0);
                                        }
                                    } else {
                                        LoactionCitys.add(new City(city, city, city));
                                        lrb.setLocationCitys(LoactionCitys);
                                        lrb.setResltCode(-2);
                                    }
                                    new Thread() {
                                        public void run() {
                                            new SendCity2Service(context).send(city);
                                        }

                                        ;
                                    }.start();
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } else { // 定位到城市且本地库有数据
//							lrb.setCoutry(country);
                                lrb.setLocationCitys(LoactionCitys);
                                lrb.setResltCode(1);
                            }
                        }

                    } else if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
//					sb.append("离线定位成功，离线定位结果也是有效的");
                    } else if (location.getLocType() == BDLocation.TypeServerError) {
//					sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                    } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
//					sb.append("网络不同导致定位失败，请检查网络是否通畅");
                    } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
//					sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                    }


                    msg.obj = lrb;
                    MyApplication.sLrb = lrb;
                    handler.sendMessage(msg);
                    locationService.stop();
                }
            }
        }

        public void onConnectHotSpotMessage(String s, int i) {
        }
    };


    /**
     * 将字符串中所有字母转换成小写字母
     *
     * @param str 转换前
     * @return 转换后
     */
    public String toLower(String str) {

        StringBuffer sb = new StringBuffer();
        for (int j = 0; j < str.length(); j++) {
            char c = str.charAt(j);
            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

}
