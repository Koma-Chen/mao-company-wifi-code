package com.wifi.utils;

import java.io.FileInputStream;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class ParseXml {
	
	
	public UpdataInfo parse(InputStream is){
		UpdataInfo ui = new UpdataInfo();
		try {
			
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(is, "utf-8");
			int eventType = parser.getEventType();
			
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:		
					break;
				case XmlPullParser.START_TAG:
					if ("version".equals(parser.getName())) {
						String version = parser.nextText();	
						ui.setVersion(version);
					}else if ("name".equals(parser.getName())) {
						String name = parser.nextText();
						ui.setName(name);
					}else if ("url".equals(parser.getName())) {
						String url = parser.nextText();
						ui.setUrl(url);
					}
					break;
				case XmlPullParser.END_TAG:
				default:
					break;
				}
				eventType = parser.next();
			}
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ui;
	}
	

}
