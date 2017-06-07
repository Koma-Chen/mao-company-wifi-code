package com.wifi.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

public class UpdataInfo {
	private String version;  
    private String url;  
    private String name;  
    
    public String getVersion() {  	
        return version;  
    }  
    public void setVersion(String string) {  
        this.version = string;  
    }  
    public String getUrl() {  
        return url;  
    }  
    public void setUrl(String url) {  
        this.url = url;  
    }  
    public String getName() {  
        return name;  
    }  
    public void setName(String name) {  
        this.name = name;  
    }  
 

}
