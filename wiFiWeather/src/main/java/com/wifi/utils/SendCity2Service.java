package com.wifi.utils;

import android.content.Context;
import android.util.Log;

import com.mpw.constant.MyApplication;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class SendCity2Service {
	private Socket s;
	private OutputStream os;
	Context mContext;

    public SendCity2Service(Context context) {
        mContext = context;
    }

    public void send(String city){
		try {
			s = new Socket(MyApplication.server, MyApplication.port);
			if (!s.isConnected()) {
				System.out.println("没打开");
			}

			os = s.getOutputStream();

			TestRSA.main(mContext, "**"+city+"**");
			Log.d("koma===RSA===location", MyApplication.data);
			os.write(MyApplication.data.getBytes());
//			os.write(("**"+city+"**").getBytes());

			os.flush();
			s.shutdownOutput();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
            e.printStackTrace();
        } finally{
			if(s!=null){
				try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(os!=null){
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	

}
