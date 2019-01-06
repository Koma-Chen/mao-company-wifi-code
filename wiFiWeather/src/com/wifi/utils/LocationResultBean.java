package com.wifi.utils;

import java.util.List;

public class LocationResultBean {

	private List<City> LocationCitys;  //用于返回匹配到的城市详细信息
	/**
	 * 用于判断定位情况
	 * 
	 * -1	定位失败
	 * 	0	定位成功，但是不
	 * 	1	定位成功，匹配到一个城市
	 * 	2	定位成功，匹配到两个城市
	 * 	3 	定位成功，匹配到三个城市
	 */
	private int resltCode;		
	
	/**
	 *定位到城市的国家 
	 */
	String coutry;	//具体哪个国家	定位到的城市

	public List<City> getLocationCitys() {
		return LocationCitys;
	}

	public void setLocationCitys(List<City> locationCitys) {
		LocationCitys = locationCitys;
	}

	public int getResltCode() {
		return resltCode;
	}

	public void setResltCode(int resltCode) {
		this.resltCode = resltCode;
	}

	public String getCoutry() {
		return coutry;
	}

	public void setCoutry(String coutry) {
		this.coutry = coutry;
	}
}
