package com.wwr.clock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mpw.constant.Constant;
import com.mpw.constant.InfoBean;

public class GetTemp implements Runnable {

	private BufferedReader br = null;
	private OutputStream os = null;
	private Socket s;
	private String content;
	private InputStream is;

	public GetTemp(String content) {
		this.content = content;
	}

	@Override
	public void run() {
		try {
			s = new Socket("120.25.207.192", 8818);
			os = s.getOutputStream();
			is = s.getInputStream();
			// 向服务器端发送一条消息
			os.write(content.getBytes());
			os.flush();
			s.shutdownOutput();

			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			// 获得了数据 对数据进行处理
			String content = sb.toString();
			String[] strs = content.split("\\*");
			if (strs.length > 6) {
				strs[6] = strs[6].substring(0, 2);
				// 将数据解析之后存入对象中去
				InfoBean ib = new InfoBean();
				ib.setTime(getNowTime());
				ib.setTempC(Double.parseDouble(strs[2]));
				ib.setTempF(Double.parseDouble(strs[4]));
				ib.setHumidity(Integer.parseInt(strs[6]));
				Constant.mlistIndoor.add(ib);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
				if (s != null) {
					s.close();
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

}