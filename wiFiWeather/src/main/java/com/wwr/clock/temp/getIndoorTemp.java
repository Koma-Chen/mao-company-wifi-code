package com.wwr.clock.temp;

import android.os.Handler;
import android.util.Log;

import com.mpw.constant.Constant;
import com.mpw.constant.InfoBean;
import com.mpw.constant.MyApplication;
import com.wwr.clock.IndoorTempView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 解析 indoor
 */

public class getIndoorTemp{

    private Handler handler;

    public void getTemp(String content,int id, Handler handler) {
        OutputStream os = null;
        Socket socket = null;
        InputStream is = null;
        this.handler = handler;

        Log.e("Socket的各种状态4","s:	"+socket+"	is:	"+is+"	os:	"+os);
        try {
            socket = new Socket(MyApplication.server, MyApplication.port);
            os = socket.getOutputStream();
            is = socket.getInputStream();
            // 向服务器端发送一条消息
            os.write(content.getBytes());
            os.flush();
            socket.shutdownOutput();

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            // 获得了数据 对数据进行处理
            String contents = sb.toString();
            Log.e("******获得的内容是：|||||||||", contents);
            String[] strs = contents.split("\\*");
            for (String s : strs) {
                Log.e("从服务器获得很多数据 ：",""+s);
            }
            if (strs.length > 6) {
                strs[6] = strs[6].substring(0, 2);
                // 将数据解析之后存入对象中去
                InfoBean ib = new InfoBean();
                ib.setTime(TimeUtils.getNowTime());
                //“--.-”  设备上没有相应模块  LL最低值  HH最高值
                if("--.-".equals(strs[2])){
                    ib.setTempC(-99.0);
                }else if("LL.L".equals(strs[2])){
                    ib.setTempC(-20.0);
                }else if("HH.H".equals(strs[2])){
                    ib.setTempC(50.0);
                }else if("null".equals(strs[2])){
                    ib.setTempC(-99.0);
                }else{
                    ib.setTempC(Double.parseDouble(strs[2]));
                }
                ib.setStrC(strs[2]);
                if("--.-".equals(strs[4])){
                    ib.setTempF(-99.0);
                }else if("LL.L".equals(strs[4])){
                    ib.setTempF(-4.0);
                }else if("HH.H".equals(strs[4])){
                    ib.setTempF(122.0);
                }else if("null".equals(strs[4])){
                    ib.setTempF(-99.0);
                }else{
                    ib.setTempF(Double.parseDouble(strs[4]));
                }
                ib.setStrF(strs[4]);

                if("--".equals(strs[6])){
                    ib.setHumidity(-99);
                    ib.setStrH(strs[6]);
                }else if("LL".equals(strs[6])){
                    ib.setHumidity(20);
                    ib.setStrH(strs[6]);
                }else if("HH".equals(strs[6])){
                    ib.setHumidity(95);
                    ib.setStrH(strs[6]);
                }else if("nu".equals(strs[6])){
                    ib.setHumidity(-99);
                    ib.setStrH("null");
                }else{
                    ib.setHumidity(Integer.parseInt(strs[6]));
                    ib.setStrH(strs[6]);
                }
                if (id == IndoorTempView.ID_FROM_INDOOR){

                    Constant.mlistIndoor.add(ib);
                }else if(id == IndoorTempView.ID_FROM_OUTDOOR){

                    Constant.mlistOutdoor.add(ib);
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Log.e("Socket的各种状态1","s:	"+socket+"	is:	"+is+"	os:	"+os);
            try {
                if (socket == null || os == null || is == null) {
                    Log.e("Socket的各种状态2","s:	"+socket+"	is:	"+is+"	os:	"+os);
                    if (is != null) {
                        is.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                    if (socket != null) {
                        socket.close();
                    }
                    socket = null;
                    os =null;
                    is=null;

                    handler.sendEmptyMessage(-1);
                }
                if (socket != null && os != null && is != null) {
                    Log.e("Socket的各种状态3","s:	"+socket+"	is:	"+is+"	os:	"+os);

                    socket.close();
                    os.close();
                    is.close();
                    socket = null;
                    os =null;
                    is=null;
                    handler.sendEmptyMessage(1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
