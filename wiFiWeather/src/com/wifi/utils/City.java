package com.wifi.utils;

public class City {
	public String name;
	public String pinyi;
	public String zip;
	public City(String name, String pinyi,String zip) {
		super();
		this.zip=zip;
		this.name = name;
		this.pinyi = pinyi;
	}
	public City() {
		super();
	}
	public String getzip() {
		return zip;
	}
	public void setzip(String zip) {
		this.zip = zip;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPinyi() {
		return pinyi;
	}
	public void setPinyi(String pinyi) {
		this.pinyi = pinyi;
	}
}
