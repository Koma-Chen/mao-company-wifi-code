package com.wwr.clock.temp;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mpw.constant.*;
import com.mpw.constant.MyApplication;
import com.wifi.utils.Base64Utils;
import com.wifi.utils.RSAUtils;
import com.wifi.utils.TestRSA;
import com.wwr.clock.IndoorTempView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class GetTempNo {
    private BufferedReader br = null;
    private OutputStream os = null;
    private Socket s = null;
    private InputStream is = null;
    private Handler handler;

    public GetTempNo(Handler handler) {
        this.handler = handler;
    }

    public void getTemp(String content, int id, Context context) {
        Log.e("Socket的各种状态4", "s:	" + s + "	is:	" + is + "	os:	" + os);

        try {
            s = new Socket(MyApplication.server, MyApplication.port);
            os = s.getOutputStream();
            is = s.getInputStream();
            // 向服务器端发送一条消息
            TestRSA.main(context, content);
            Log.d("koma===RSA===temperatur", com.mpw.constant.MyApplication.data);
            os.write(com.mpw.constant.MyApplication.data.getBytes());
            os.flush();
            s.shutdownOutput();

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            // 获得了数据 对数据进行处理
            String contents = sb.toString();
            //从服务器发过来的加密的数据
            if (sb.toString().startsWith("##")){
                byte[] decode = Base64Utils.decode(sb.toString().substring(2,sb.toString().length()-2));
                byte[] tempBytes = RSAUtils.decryptByPrivateKey(decode, com.mpw.constant
                        .MyApplication
                        .jiemikey);

                contents = new String(tempBytes,"UTF-8");
                System.out.println("从服务器获得的解密后的数据是++++++++++++++++++" + contents);

                if (contents.contains("Error")){
                    String[] temp = contents.split("-");
                    Message message = new Message();
                    Log.d("koma===dialog111",temp[1]);
                    message.what = -4;
                    message.obj = temp[1];
                    handler.sendMessage(message);
                    return;
                }
            }


            Log.e("******获得的内容是：|||||||||", contents);
            String[] strs = contents.split("\\*");
            for (String s : strs) {
                Log.e("从服务器获得很多数据 ：", "" + s);
            }
            Log.e("从服务器获得很多数据 ：", strs.length+"");
            if (strs.length > 6) {
                strs[6] = strs[6].substring(0, 2);
                // 将数据解析之后存入对象中去
                InfoBean ib = new InfoBean();
                ib.setTime(TimeUtils.getNowTime());
                //“--.-”  设备上没有相应模块  LL最低值  HH最高值
                if ("--.-".equals(strs[2])) {
                    ib.setTempC(-99.0);
                } else if ("LL.L".equals(strs[2])) {
                    if (id == IndoorTempView.ID_FROM_INDOOR) {
                        ib.setTempC(-20.0);
                    } else if (id == IndoorTempView.ID_FROM_OUTDOOR) {
                        ib.setTempC(-40.0);
                    }
                } else if ("HH.H".equals(strs[2])) {
                    if (id == IndoorTempView.ID_FROM_INDOOR) {
                        ib.setTempC(50.0);
                    } else if (id == IndoorTempView.ID_FROM_OUTDOOR) {
                        ib.setTempC(70.0);
                    }
                } else if ("null".equals(strs[2])) {
                    ib.setTempC(-99.0);
                } else {
                    ib.setTempC(Double.parseDouble(strs[2]));
                }
                ib.setStrC(strs[2]);
                if ("--.-".equals(strs[4])) {
                    ib.setTempF(-99.0);
                } else if ("LL.L".equals(strs[4])) {
                    if (id == IndoorTempView.ID_FROM_INDOOR) {
                        ib.setTempC(-4.0);
                    } else if (id == IndoorTempView.ID_FROM_OUTDOOR) {
                        ib.setTempC(-40.0);
                    }
                } else if ("HH.H".equals(strs[4])) {
                    if (id == IndoorTempView.ID_FROM_INDOOR) {
                        ib.setTempC(122.0);
                    } else if (id == IndoorTempView.ID_FROM_OUTDOOR) {
                        ib.setTempC(158.0);
                    }
                } else if ("null".equals(strs[4])) {
                    ib.setTempF(-99.0);
                } else {
                    ib.setTempF(Double.parseDouble(strs[4]));
                }
                ib.setStrF(strs[4]);

                if ("--".equals(strs[6])) {
                    ib.setHumidity(-99);
                    ib.setStrH(strs[6]);
                } else if ("LL".equals(strs[6])) {
                    ib.setHumidity(20);
                    ib.setStrH(strs[6]);
                } else if ("HH".equals(strs[6])) {
                    ib.setHumidity(95);
                    ib.setStrH(strs[6]);
                } else if ("nu".equals(strs[6])) {
                    ib.setHumidity(-99);
                    ib.setStrH("null");
                } else {
                    Log.d("chenhang Humidity:",strs[6]);
                    Log.d("chenhang 湿度是:",strs[6]);
                    ib.setHumidity(Integer.parseInt(strs[6]));
                    ib.setStrH(strs[6]);
                }
                Log.d("koma","id is" + id);

                if (id == IndoorTempView.ID_FROM_INDOOR) {
                    Log.d("koma","室内添加");
                    Constant.mlistIndoor.add(ib);
                } else if (id == IndoorTempView.ID_FROM_OUTDOOR) {
                    Log.d("koma","室外添加");
                    Constant.mlistOutdoor.add(ib);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Log.e("Socket的各种状态1", "s:	" + s + "	is:	" + is + "	os:	" + os);
            try {
                if (s == null || os == null || is == null) {
                    Log.e("Socket的各种状态2", "s:	" + s + "	is:	" + is + "	os:	" + os);
                    if (is != null) {
                        is.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                    if (s != null) {
                        s.close();
                    }
                    s = null;
                    os = null;
                    is = null;

                    handler.sendEmptyMessage(-1);
                }
                if (s != null && os != null && is != null) {
                    Log.e("Socket的各种状态3", "s:	" + s + "	is:	" + is + "	os:	" + os);

                    s.close();
                    os.close();
                    is.close();
                    s = null;
                    os = null;
                    is = null;
                    handler.sendEmptyMessage(1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
