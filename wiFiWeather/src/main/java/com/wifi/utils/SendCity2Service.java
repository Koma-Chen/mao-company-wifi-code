package com.wifi.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class SendCity2Service {
	private Socket s;
	private OutputStream os;
	
	public void send(String city){
		try {
			s = new Socket("120.25.207.192", 8118);
			if (!s.isConnected()) {
				System.out.println("没打开");
			}

			os = s.getOutputStream();

			os.write(("**"+city+"**").getBytes());

			os.flush();
			s.shutdownOutput();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
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
