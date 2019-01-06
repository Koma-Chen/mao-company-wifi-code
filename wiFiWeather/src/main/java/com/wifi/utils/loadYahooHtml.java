package com.wifi.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by 陈航 on 2018/3/21.
 * <p>
 * 少年一世能狂，敢骂天地不仁
 */

public class loadYahooHtml {

    static String strArray[] = new String[17];
    int tryTimes = 1;//网络请求次数

    public static String[] main(String[] cityData) {

        try {
            if (cityData != null) {

                Log.d("komacountry", cityData[3]);

                String URL = "https://www.yahoo.com/news/weather/" + cityData[3] + "/" + cityData[1]
                        + "/" + cityData[1] + "-" + cityData[0];

                //获取到整个静态页面
                Document doc = Jsoup.connect(URL).get();
                //----网页实时时间----//
                Elements scriptElement = doc.getElementsByTag("script").eq(24);
//			Element scriptEle = scriptElement.get(0);
                StringBuffer scriptBuffer = new StringBuffer();

//	        System.out.println("\nscriptEle： "+scriptElement.toString());
                for (Element scriptEle : scriptElement) {
                    scriptBuffer.append(scriptEle.toString());
                }
//			System.out.println("\nscriptBuffer： "+scriptBuffer);
                String scriptString = scriptBuffer.substring(scriptBuffer.indexOf("context") - 2,
                        scriptBuffer.indexOf("}(this))") - 2);
//			System.out.println("\nscriptString=====:"+scriptString);
                //将str转换成JSONObjct格式
                JSONObject json = new JSONObject(scriptString);
                //--解析出json数据中的time：时间
                JSONObject context = json.getJSONObject("context");
                JSONObject dispatcher = context.getJSONObject("dispatcher");
                JSONObject stores = dispatcher.getJSONObject("stores");
                JSONObject WeatherStore = stores.getJSONObject("WeatherStore");
                JSONObject weathers = WeatherStore.getJSONObject("weathers");
                JSONObject woeid = weathers.getJSONObject(cityData[0]);
                JSONObject forecasts = woeid.getJSONObject("forecasts");
                JSONArray daily = forecasts.getJSONArray("daily");

//			JSONArray hourly         = forecasts.getJSONArray("hourly");
                JSONObject row0 = daily.getJSONObject(0);
                JSONObject localTime = row0.getJSONObject("localTime");
                String timestamp = (String) localTime.get("timestamp");

                String year = timestamp.substring(0, 4);
                String month = timestamp.substring(5, 7);
                String day = timestamp.substring(8, 10);
                String hour = timestamp.substring(11, 13);
                String min = timestamp.substring(14, 16);
                String second = timestamp.substring(17, 19);
                System.out.println("\ntimestamp=" + timestamp);
//		    System.out.println("\nyear="+timestamp.substring(0,4));
//		    System.out.println("\nmonth="+timestamp.substring(5,7));
//		    System.out.println("\nday="+timestamp.substring(8,10));
//		    System.out.println("\nhour="+timestamp.substring(11,13));
//		    System.out.println("\nmin="+timestamp.substring(14,16));
//		    System.out.println("\nsecond="+timestamp.substring(17,19));

                //--解析出json数据中的time：时间
                //----网页实时时间----//
                //----网页第一天天气代码----//
                //方法一：抓取网页json分析
                JSONObject row1 = daily.getJSONObject(0);
//			int weatherCode1         = (int) row1.get("conditionCode");
//			System.out.println("\n第1天天气代码： "+weatherCode1);
//			String condition1         = (String) row1.get("conditionDescription");
//			System.out.println("\n第1天天气描述： "+condition1);
                //方法二：抓取网页数据
                Elements myElements = doc.getElementsByClass("My(2px)");
                Element element = myElements.get(0);
                Elements spans = element.getElementsByTag("span");
                Element span = spans.get(0);
                String weatherCode1 = span.attr("data-code");
                System.out.println("\n第1天天气代码： " + weatherCode1);
                Element span1 = spans.get(1);
                Document span1Doc = Jsoup.parse(span1.toString());
                String condition1 = span1Doc.text();
                System.out.println("\n第1天天气描述： " + condition1);
                //----网页第一天天气代码----//
                //----网页第一天温度----//
                //方法一：抓取网页json分析
                JSONObject temperature1 = row1.getJSONObject("temperature");
                int high1F = (int) temperature1.get("high");
                int low1F = (int) temperature1.get("low");
                System.out.print("\n第1天highF:" + high1F);
                System.out.print("\n第1天lowF:" + low1F);
                //方法二：抓取网页数据
//			Elements temperatureElement = doc.getElementsByClass("Va(m) Px(6px)");
//			Document temperatureDoc = Jsoup.parse(temperatureElement.toString());
//			//System.out.print("element:" + element);
//			//System.out.print("temperatureDoc:" + temperatureDoc.text());
//			String temperatureText = temperatureDoc.text();
//			//System.out.print("\ntemperatureText:" + temperatureText.length());
//			String first = temperatureText.substring(temperatureText.indexOf("°")+1,
// temperatureText.length());
//			//System.out.print("\nfirst:" + first);
//			String highF = temperatureText.substring(0,
//					temperatureText.indexOf("°") - 1);
//			System.out.print("\n第一天highF:" + highF);
//			String lowF = first.substring(0,
//					first.indexOf("°") - 1);
//			System.out.print("\n第一天lowF:" + lowF);
                //----网页第一天温度----//
                //----网页第一天湿度----//
                //方法一：抓取网页json分析
//			int humidity1         = (int) row1.get("humidity");
//			System.out.println("\n第1天天气湿度： "+humidity1);
                //方法二：抓取网页数据
                Elements humidityElement = doc.getElementsByClass("list Pstart(10px) Bxz(bb)");
                Document humidityDoc = Jsoup.parse(humidityElement.toString());
                //System.out.print("\nhumidityDoc:" + humidityDoc.text());
                String humidityText = humidityDoc.text();
                String humidity = humidityText.substring(humidityText.indexOf("Humidity") + 9,
                        humidityText.indexOf("%"));
                System.out.print("\n第一天humidity:" + humidity);
                //----网页第一天湿度----//
                //----网页第一天实时温度----//
                Elements currentTempElement = doc.getElementsByClass("now Fz(8em)--sm Fz(10em) Lh" +
                        "(0.9em) Fw(100) My(2px) Trsdu(.3s)");
                Element tempElement = currentTempElement.get(0);
                Elements tempSpans = tempElement.getElementsByTag("span");
                Element tempSpan = tempSpans.get(0);
                Document tempDoc = Jsoup.parse(tempSpan.toString());
                String tempText = tempDoc.text();
                System.out.print("\n第一天实时温度:" + tempText);
                //----网页第一天实时温度----//
                //----网页第二天天气代码----//
                JSONObject row2 = daily.getJSONObject(1);
                int weatherCode2 = (int) row2.get("conditionCode");
                System.out.println("\n第2天天气代码： " + weatherCode2);
                String condition2 = (String) row2.get("conditionDescription");
                System.out.println("\n第2天天气描述： " + condition2);
                //----网页第二天天气代码----//
                //----网页第二天温度----//
                JSONObject temperature2 = row2.getJSONObject("temperature");
                int high2F = (int) temperature2.get("high");
                int low2F = (int) temperature2.get("low");
                System.out.print("\n第2天highF:" + high2F);
                System.out.print("\n第2天lowF:" + low2F);
                //----网页第二天温度----//
                //----网页第三天天气代码----//
                JSONObject row3 = daily.getJSONObject(2);
                int weatherCode3 = (int) row3.get("conditionCode");
                System.out.println("\n第3天天气代码： " + weatherCode3);
                String condition3 = (String) row3.get("conditionDescription");
                System.out.println("\n第3天天气描述： " + condition3);
                //----网页第三天天气代码----//
                //----网页第三天温度----//
                JSONObject temperature3 = row3.getJSONObject("temperature");
                int high3F = (int) temperature3.get("high");
                int low3F = (int) temperature3.get("low");
                Log.d("koma3", low3F + "");
                System.out.print("\n第3天highF:" + high3F);
                System.out.print("\n第3天lowF:" + low3F);
                //----网页第三天温度----//


                strArray[0] = year;//网页实时年
                strArray[1] = month;//网页实时月
                strArray[2] = day;//网页实时日
                strArray[3] = hour;//网页实时时
                strArray[4] = min;//网页实时分
                strArray[5] = second;//网页实时秒

                strArray[6] = humidity;//网页第1天实时湿度
                strArray[7] = tempText;//网页第1天实时温度
                strArray[8] = weatherCode1;//网页第1天天气代码
                strArray[9] = String.valueOf(high1F);//网页第1天温度--高温
                strArray[10] = String.valueOf(low1F);//网页第1天温度--低温

                strArray[11] = String.valueOf(weatherCode2);//网页第2天天气代码
                strArray[12] = String.valueOf(high2F);//网页第2天温度--高温
                strArray[13] = String.valueOf(low2F);//网页第2天温度--低温

                strArray[14] = String.valueOf(weatherCode3);//网页第三天天气代码
                strArray[15] = String.valueOf(high3F);//网页第三天温度--高温
                strArray[16] = String.valueOf(low3F);//网页第三天温度--低温

            }
        } catch (IOException e) {
            System.out.println("\n网络异常");
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return strArray;
    }

}
