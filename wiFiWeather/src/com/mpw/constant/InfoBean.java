package com.mpw.constant;

public class InfoBean {
	private String strC;
	private String strF;
	private String strH;

	private Double tempC;
	private Double tempF;
	private int humidity;
	private String time;
	
	public String getStrH() {
		return strH;
	}

	public void setStrH(String strH) {
		this.strH = strH;
	}

	

	public String getStrC() {
		return strC;
	}

	public void setStrC(String strC) {
		this.strC = strC;
	}

	public String getStrF() {
		return strF;
	}

	public void setStrF(String strF) {
		this.strF = strF;
	}

	public Double getTempC() {
		return tempC;
	}

	public void setTempC(Double tempC) {
		this.tempC = tempC;
	}

	public Double getTempF() {
		return tempF;
	}

	public void setTempF(Double tempF) {
		this.tempF = tempF;
	}

	public int getHumidity() {
		return humidity;
	}

	public void setHumidity(int humidity) {
		this.humidity = humidity;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
