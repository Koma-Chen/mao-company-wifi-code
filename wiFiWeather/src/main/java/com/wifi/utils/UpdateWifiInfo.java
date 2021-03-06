package com.wifi.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mpw.constant.InfoBean;
import com.mpw.constant.MyApplication;
import com.wwr.clock.temp.SqliteOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateWifiInfo implements Runnable {
    Handler handler;
    BufferedReader br = null;
    OutputStream os = null;
    private Socket s = null;
    String content;
    private InputStream is = null;
    private InputStream is1 = null;
    private String cn;    //00国外 01 国内
    private String city;
    private long startTime;
    private boolean yahoo_date = true;
    private Context mContext;
    private boolean isFirst = true;
    private SqliteOpenHelper mSqliteOpenHelper;

    /**
     * @param content  传输的内容
     * @param handler
     * @param cn       ·国内还是国外
     * @param cityName 城市
     */
    public UpdateWifiInfo(Context context, String content, Handler handler, String cn,
                          String cityName, SqliteOpenHelper sqliteOpenHelper) {

        this.handler = handler;
        this.content = content;
        this.city = cityName;
        this.cn = cn;
        mContext = context;
        mSqliteOpenHelper = sqliteOpenHelper;

    }

    @Override
    public void run() {
        try {
            System.out.println("准备打开链接");
            // 向服务器端发送一条消息
            content = content.replace("**", "##");

            startTime = System.currentTimeMillis();
            /**
             * 如果是国外城市 先请求雅虎api先请求数据 再发送给服务器
             */
            if ("00".equals(cn)) {

                System.out.println("国外城市");
                final String yahoo = "https://query.yahooapis" +
                        ".com/v1/public/yql?q=select%20*%20from%20weather" +
                        ".forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)" +
                        "%20where%20text%3D%22"
                        + URLEncoder.encode(city)
                        + "%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
                System.out.println(yahoo);

//                String urlDeCode = URLDecoder.decode(urlEnCode,"UTF-8");
//                String deStr = KmService.deCryptKey(enStr);


                URL url1;
                url1 = new URL(yahoo);
                HttpURLConnection conn1 = (HttpURLConnection) url1
                        .openConnection();
                conn1.setConnectTimeout(2000); // 设置三秒超时
                conn1.setReadTimeout(2000);
                conn1.connect();

                is1 = conn1.getInputStream();
                BufferedReader br1 = new BufferedReader(new InputStreamReader(is1));

                StringBuffer sb1 = new StringBuffer();
                String str1;
                while ((str1 = br1.readLine()) != null) {
                    sb1.append(str1);
                }


                System.out.println(sb1.toString());
                if ("{".equals(String.valueOf(sb1.toString().charAt(0)))) { // 返回的是JSon数据
                    System.out.println("是json数据");

                    JSONObject json = new JSONObject(sb1.toString());
                    if (json.getString("query") == null && json.getString("query").length()>200) { // query为空

                        System.out.println("query为空");
                        yahoo_date = false;
                        long l = 5000 - (System.currentTimeMillis() - startTime);
                        if (l > 0) {
                            Thread.sleep(l);
                        }
                        handler.sendEmptyMessage(-2);
                        return;

                    } else {
                        System.out.println("query不为空");
                        int count = json.getJSONObject("query").getInt("count");
                        if (count == 0) {// 返回为空的时候
                            System.out.println("count为0");
                            yahoo_date = false;
                            long l = 5000 - (System.currentTimeMillis() - startTime);
                            if (l > 0) {
                                Thread.sleep(l);
                            }
                            handler.sendEmptyMessage(-2);
                            return;

                        } else {// 有具体的返回结果
//							System.out.println("count不为0");
                            content += "" + sb1.toString() + "##";
//                            if (MyApplication.time >= 4)
//                                content += "" + "HTML##" + MyApplication.json + "##";
                            System.out.println("数据正常       出来了" + content);
                        }
                    }
                } else {// 返回的不是json数据

                    yahoo_date = false;
                    long l = 5000 - (System.currentTimeMillis() - startTime);
                    if (l > 0) {
                        Thread.sleep(l);
                    }
                    handler.sendEmptyMessage(-2);
                    return;

                }

            }

//			System.out.println("刚改-----	"+content);

            s = new Socket(MyApplication.server, MyApplication.port);
            // s = new Socket("192.168.0.110", 8818);
            System.out.println("打开socket");

            if (!s.isConnected()) {
                System.out.println("没打开");
            }
            os = s.getOutputStream();

            is = s.getInputStream();
            Log.e("111", "11111	--" + content);

            TestRSA.main(mContext, content);
            Log.d("koma===RSA", MyApplication.data);
            os.write(MyApplication.data.getBytes());

            os.flush();
            s.shutdownOutput();
            // 解析输入流
            // BufferedReader br = new BufferedReader(new
            // InputStreamReader(is));
            // String line;
            // StringBuilder sb = new StringBuilder();
            System.out.println("获得了is等待解析	" + (br == null)
                    + "		isInputShutdown:	" + (s.isInputShutdown())
                    + "	isConnected:	" + s.isConnected()
                    + "		isOutputShutdown:	" + s.isOutputShutdown());
            // while ((line = br.readLine()) != null) {
            // sb.append(line);
            // }

            byte[] buffer = new byte[8];
            int readBytes = 0;
            StringBuilder sb = new StringBuilder();

            // Thread.sleep(2000); //等两秒 如果没有
            Log.d("koma===available", is.available()+"");
            while (is.available() == 0) {
                if ((System.currentTimeMillis() - startTime) > 5 * 1000) {
                    handler.sendEmptyMessage(-2);
                    return;
                }
            }

            System.out.println("有没有数据-----------" + is.available());
            while ((readBytes = is.read(buffer)) > 0) {
                sb.append(new String(buffer, 0, readBytes));
            }

            System.out.println("从服务器获得的数据是++++++++++++++++++" + sb.toString());
            String backInfo = sb.toString();
            //从服务器发过来的加密的数据
            if (backInfo.startsWith("##")){
                byte[] decode = Base64Utils.decode(sb.toString().substring(2,sb.toString().length()-2));
                byte[] tempBytes = RSAUtils.decryptByPrivateKey(decode, MyApplication
                        .jiemikey);

                backInfo = new String(tempBytes,"UTF-8");
                System.out.println("从服务器获得的解密后的数据是++++++++++++++++++" + backInfo);

                if (backInfo.contains("Error")){
                    String[] temp = backInfo.split("-");
                    Message message = new Message();
                    Log.d("koma===dialog111",temp[1]);
                    message.what = -4;
                    message.obj = temp[1];
                    handler.sendMessage(message);
                    return;
                }
            }



            // 获得了数据 对数据进行处理

            Message msg = new Message();
            if (backInfo.isEmpty()) {
                handler.sendEmptyMessage(-1);
//                mContext.sendBroadcast(new Intent("com.chenhang.b"));
            } else {
                msg.what = 1;
                Log.e("返回的信息是", backInfo);
                String[] strs = backInfo.split("\\*");
                if (strs.length >= 3) {
                    if (strs[2].length() == 8) {
                        strs[2] = strs[2].substring(0, 4);
                    } else {
                        strs[2] = strs[2].substring(0, 2);
                    }
                    InfoBean ib = new InfoBean();
                    ib.setTime(getNowTime());
                    ib.setTime(getNowTime());
//                  这里主要是判断设备上有没有室外温度的模块，如果有，在数据库中更新它的值为0，
//                  haveOut默认值我给的是1，结合主界面的点击事件；--by chenhang
                    Log.d("koma", strs[3] + "");
                    SQLiteDatabase db = mSqliteOpenHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    if (strs[3].equals("null") || strs[3].equals("--.-")) {
                        values.put("haveOut", 1);
                        Log.d("koma", "+1");
                        db.update("out", values, null, null);
                    } else if (!strs[3].equals("null")) {
                        values.put("haveOut", 0);
                        Log.d("koma", "+0");
                        db.update("out", values, null, null);
                    }
                    db.close();
                }
                handler.sendMessage(msg);
            }
        } catch (IOException e) {
            System.out.println("异常    IOException" + e.toString());

        } catch (JSONException e) {
            System.out.println("异常    JSONException");

        } catch (InterruptedException e) {
            System.out.println("异常    InterruptedException");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {

                if (is1 != null) {
                    is1.close();
                }
                if (is != null) {
                    is.close();
                    System.out.println("is不为空");
                }

                if (os != null) {
                    os.close();
                    System.out.println("os不为空");
                }

                if (s != null) {
                    s.close();
                    System.out.println("s不为空");
                }

                if (yahoo_date && (is == null || os == null || s == null)) {
                    handler.sendEmptyMessage(-1);
//                    mContext.sendBroadcast(new Intent("com.chenhang.b"));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getNowTime() {
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("HH:mm");
        String time = format.format(date);
        return time;
    }

    /**
     * 放返回的数据不正常时
     */

    public void requestFaild() {
        handler.sendEmptyMessageAtTime(-2, startTime + 5000);

    }

    public void parse() {
        content = MyApplication.xieyi + "HTML##" + MyApplication.json + "##";

        Log.d("koma===content", content);
        Socket s1 = null;
        try {
            s1 = new Socket(MyApplication.server, MyApplication.port);


            // s = new Socket("192.168.0.110", 8818);
            System.out.println("打开socket=======");

            if (!s1.isConnected()) {
                System.out.println("没打开");
            }
            os = s1.getOutputStream();
            is = s1.getInputStream();
            Log.e("111", "11111	--" + content);

            TestRSA.main(mContext, content);
            Log.d("koma===RSA", MyApplication.data);
            os.write(MyApplication.data.getBytes());

            os.flush();
            s1.shutdownOutput();

            System.out.println("获得了is等待解析	" + (br == null)
                    + "		isInputShutdown:	" + (s1.isInputShutdown())
                    + "	isConnected:	" + s1.isConnected()
                    + "		isOutputShutdown:	" + s1.isOutputShutdown());


            byte[] buffer = new byte[8];
            int readBytes = 0;
            StringBuilder sb = new StringBuilder();

            Log.d("koma===available", System.currentTimeMillis() - startTime + "");
            // Thread.sleep(2000); //等两秒 如果没有
            while (is.available() == 0) {
                if ((System.currentTimeMillis() - startTime) > 30 * 1000) {
                    Log.d("koma===", "进来了");
                    handler.sendEmptyMessage(-3);
                    return;
                }
            }


            System.out.println("有没有数据-----------" + is.available());
            while ((readBytes = is.read(buffer)) > 0) {
                sb.append(new String(buffer, 0, readBytes));
            }

            System.out.println("从服务器获得的数据是++++++++++++++++++" + sb.toString());

            // 获得了数据 对数据进行处理
            String backInfo = sb.toString();
            Message msg = new Message();
            if (backInfo.isEmpty()) {
                handler.sendEmptyMessage(-1);
//                mContext.sendBroadcast(new Intent("com.chenhang.b"));
            } else {
                msg.what = 1;
                Log.e("返回的信息是", backInfo);
                String[] strs = backInfo.split("\\*");
                if (strs.length >= 3) {
                    if (strs[2].length() == 8) {
                        strs[2] = strs[2].substring(0, 4);
                    } else {
                        strs[2] = strs[2].substring(0, 2);
                    }

                    InfoBean ib = new InfoBean();
                    ib.setTime(getNowTime());
                    // 将数据解析之后存入对象中去
                    ib.setTime(getNowTime());


//                  这里主要是判断设备上有没有室外温度的模块，如果有，在数据库中更新它的值为0，
//                  haveOut默认值我给的是1，结合主界面的点击事件；--by chenhang
                    Log.d("koma", strs[3] + "");
                    SQLiteDatabase db = mSqliteOpenHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    if (strs[3].equals("null") || strs[3].equals("--.-")) {
                        values.put("haveOut", 1);
                        Log.d("koma", "+1");
                        db.update("out", values, null, null);
                    } else if (!strs[3].equals("null")) {
                        values.put("haveOut", 0);
                        Log.d("koma", "+0");
                        db.update("out", values, null, null);
                    }
                    db.close();

//					Constant.mlistIndoor.add(ib);
                }
                handler.sendMessage(msg);
//                mContext.sendBroadcast(new Intent("com.chenhang.a"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
