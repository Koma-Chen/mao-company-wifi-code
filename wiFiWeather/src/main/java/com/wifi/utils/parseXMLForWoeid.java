package com.wifi.utils;

import android.os.Handler;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mpw.constant.MyApplication;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by 陈航 on 2018/3/21.
 * <p>
 * 少年一世能狂，敢骂天地不仁
 */

public class parseXMLForWoeid {


    String cityData[] = null;

    boolean bl = true;//网络请求正常
    int tryTimes = 1;//网络请求次数

    String city = "";
    String province = "";
    String iso = "";
    String cityName;
    String strArray[] = new String[17];
    public Handler mHandler;

    public parseXMLForWoeid(Handler handler) {
        mHandler = handler;
    }

    public void main(String s) {


        String sortData[] = sort(s);
//        String[] temp = s.split(",");
        String[] temp = sortData;

        city = temp[0];
        province = temp[1];
        iso = temp[2];

        cityName = city;
        //用%20代替空格
        cityName = handleCityWord(cityName);
        //用%20代替空格
        String cityData[] = null;

        //boolean bl = true;//网络请求正常


        try {
            //--请求国内实时温、湿度、每天18：00后的天气预报--//
            HttpClient httpclient = new DefaultHttpClient();
            //HTTP Get请求
            HttpGet httpGet = new HttpGet(
                    "http://sugg.us.search.yahoo" +
                            ".net/gossip-gl-location/?appid=weather&output=xml&command=" +
                            cityName);
            HttpResponse response = httpclient.execute(httpGet);
            try {
                HttpEntity entity = response.getEntity();

                Log.d("koma=====","第一次进parse方法");
                if (entity != null) {// 打印响应内容长度
                    String text = EntityUtils.toString(entity);

//						System.out.print("\ntext:" + text);
                    cityData = parseWoeid1(text, city, province, iso);

                    //---判断是否为null object----//
                    if (cityData[0] == null) {
                        //请求数据失败
                        bl = false;//解析网址有问题
                        strArray = retry(cityName, city, province, iso);
                    } else {
                        System.out.print("\nwoeid:" + cityData[0]);
                        parseJson(cityData);
                    }

                    //System.out.print("\nwoeid:" + cityData[0]);
//						System.out.print("\ncity:" + cityData[1]);
//						System.out.print("\nprovince:" + cityData[2]);
//						System.out.print("\niso:" + cityData[3]);

                }
            } catch (Exception e) {
                System.out.println("\n第1次查询woeid请求网络异常");
                //第1次请求失败
                bl = false;//第1次解析网址有问题
                strArray = retry(cityName, city, province, iso);

                e.printStackTrace();

            } finally {
                //System.out.println("关闭响应");
//                response.close();
            }
//            if (bl) {
//                //第一次解析网址没问题，继续解析天气预报
//                strArray = loadYahooHtml.main(cityData);
//            }
        } catch (Exception e) {
//            tryTimes++;
//            if (tryTimes == 2) {
//                //请求第二次
//                System.out.println("\n第一次loadurl请求网络异常");
//                strArray = loadYahooHtml.main(cityData);
//            } else {
//                System.out.println("\n第2次loadurl请求网络异常");
//            }
            e.printStackTrace();

        }

    }

    public String[] retry(String cityName, String City, String Province, String ISO) {

        try {

            String[] cityData = parseWoeid2(cityName, City, Province, ISO);
            //--处理-返回空数组-//
            if (cityData[0] == null) {
                //请求数据失败
                mHandler.sendEmptyMessage(-3);
            } else {
                System.out.print("\nwoeid:" + cityData[0]);
//                strArray = loadYahooHtml.main(cityData);
                parseJson(cityData);
            }

//			System.out.print("\ncity:" + cityData[1]);
//			System.out.print("\nprovince:" + cityData[2]);
//			System.out.print("\niso:" + cityData[3]);


        } catch (Exception e) {
            System.out.println("\n第二次请求网络异常");
            e.printStackTrace();
        }
//
//        return strArray;
        return cityData;
    }

    public String[] parseWoeid1(String protocolXML, String City, String Province, String
            ISO) {
        Log.d("koma=====","走了parseWoeid1");
        String strArray[] = new String[4];
        String city = "";
        String iso = "";
        String woeid = "";
        String province = "";
        String country = "";
        String fullCityName = "";

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(
                    protocolXML)));
            Element root = doc.getDocumentElement();
//			System.out.print("\nroot:" + root);
            String num = root.getAttribute("n");
            System.out.print("\nnum:" + num);

            NodeList books = root.getChildNodes();


            if (books != null) {
                int times = 1;

                for (int i = 0; i < books.getLength(); i++) {
                    Node book = books.item(i);
                    if (book.getNodeName().equals("s")) {

                        Element ele = (Element) book;
                        city = ele.getAttribute("k");

                        if (city.equalsIgnoreCase(City)) {
                            //城市数据相同
                            //忽略大小写比较
                            //System.out.print("\ncity:" + city);
                            String dstring = ele.getAttribute("d");
                            //System.out.print("\nd:" + dstring);
                            iso = dstring.substring(dstring.indexOf("iso=")+4,dstring.indexOf("&woeid="));
//							System.out.print("\niso===:" + iso);
                            woeid = dstring.substring(dstring.indexOf("&woeid=")+7,dstring.indexOf("&lon="));
                            //System.out.print("\nwoeid:" + woeid);
                            province = dstring.substring(dstring.indexOf("&s=")+3,dstring.indexOf("&c="));
                            //System.out.print("\nprovince:" + province);
                            if(dstring.indexOf("&sc=") != -1){
                                //找得到
                                country = dstring.substring(dstring.indexOf("&c=")+3,dstring.indexOf("&sc"));

                            }else if(dstring.indexOf("&country_woeid=") != -1){

                                //找得到
                                country = dstring.substring(dstring.indexOf("&c=")+3,dstring.indexOf("&country_woeid="));
                            }
                            else{
                                //找不到
                                continue;
                            }

                            Log.d("koma===country",country);

//							System.out.print("\ncountry:" + country);
                            fullCityName = dstring.substring(dstring.indexOf("&n=") + 3, dstring
                                    .length());
//							System.out.print("\nfullCityName=n==:" + fullCityName);
                            String FullCityNameBycountry = City + ", " + Province + ", " + country;
                            String FullCityNameByISO = City + ", " + Province + ", " + ISO;
//							System.out.print("\nfullCityName=c==:" + FullCityNameBycountry);
//							System.out.print("\nfullCityName=i==:" + FullCityNameByISO);

                            if (!Province.isEmpty() && !ISO.isEmpty()
                                    && province.equalsIgnoreCase(Province)
                                    && (iso.equalsIgnoreCase(ISO) || country.equalsIgnoreCase(ISO))
                                    && (fullCityName.equalsIgnoreCase(FullCityNameBycountry)
                                    || fullCityName.equalsIgnoreCase(FullCityNameByISO))) {
                                //省份、国家不为空，并且省份、国家,搜索城市全名数据相同，且第一个满足条件
                                strArray[0] = woeid;
                                strArray[1] = city;
                                strArray[2] = province;
                                strArray[3] = iso;
                                times = Integer.valueOf(num);
                            } else if (times == 1) {
                                //第一组数据
                                strArray[0] = woeid;
                                strArray[1] = city;
                                strArray[2] = province;
                                strArray[3] = iso;
                                times++;
                            }
                            //拿第一个符合条件的数据
                            if (strArray.length == 4 && times == Integer.valueOf(num)) break;
                        }

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("koma", strArray[0] + strArray[1] + strArray[2] + strArray[3]);
        return strArray;
    }

    public static String[] parseWoeid2(String cityName, String City, String Province, String ISO) {
        Log.d("koma=====","走了parseWoeid2");
        String strArray[] = new String[4];
        String iso = "";
        String woeid = "";
        String province = "";

        try {

            String URL = "http://woeid.rosselliot.co.nz/lookup/" + cityName;

            //获取到整个静态页面
            org.jsoup.nodes.Document doc = Jsoup.connect(URL).get();
//			System.out.print("\ndoc:" + doc);
//			//----网页实时时间----//
            org.jsoup.nodes.Element resultElement = doc.getElementById("lookup_result");

            org.jsoup.nodes.Document textDoc = Jsoup.parse(resultElement.toString());
            String text = textDoc.text();
//			System.out.println("\ntext： "+text);
            String num = text.substring(text.indexOf("(") + 1, text.indexOf(")"));
//			System.out.print("\nnum:" + num);

            Elements woeidElement = doc.getElementsByClass("woeid_row");
//			System.out.println("\nwoeidElement： "+woeidElement);

            int times = 1;
            for (org.jsoup.nodes.Element subEle : woeidElement) {
//				System.out.println("\ntrEle： "+subEle);
                String subCity = subEle.attr("data-city");
//	            System.out.println("\nsubCity： "+subCity);
                String subCountry = subEle.attr("data-country");
//	            System.out.println("\nsubCountry： "+subCountry);
                String subWoeid = subEle.attr("data-woeid");
//	            System.out.println("\nsubWoeid： "+subWoeid);
                String subProvince = subEle.attr("data-province_state");
//	            System.out.println("\nsubProvince： "+subProvince);

                if (subCity.equalsIgnoreCase(City)) {
                    //城市数据相同
                    //忽略大小写比较
//					System.out.print("\ncity:" + city);
                    iso = subCountry;
//					System.out.print("\niso===:" + iso);
                    woeid = subWoeid;
                    //System.out.print("\nwoeid:" + woeid);
                    province = subProvince;
                    //System.out.print("\nprovince:" + province);

                    if (!Province.isEmpty() && !ISO.isEmpty() && province.equalsIgnoreCase
                            (Province)) {
                        //省份、国家不为空，并且城市 省份数据相同
                        strArray[0] = woeid;
                        strArray[1] = subCity;
                        strArray[2] = province;
                        strArray[3] = iso;
                        times++;
                    } else if (times == 1) {

                        strArray[0] = woeid;
                        strArray[1] = subCity;
                        strArray[2] = province;
                        strArray[3] = iso;
                        times++;
                    }
                    //拿第一个符合条件的数据
                    if (strArray.length == 4 && times == Integer.valueOf(num)) break;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("koma", strArray[0] + strArray[1] + strArray[2] + strArray);
        return strArray;
    }

    public static String handleCityWord(String str) {

        str = str.trim();// 去掉首尾空格
        int index = 0;// num表示单词数，index表示空格的索引位置
        do {
            ++index;// 至少有一个空格
            index = str.indexOf(' ', index);// 搜索空格的索引位置
            str = str.replaceAll(" ", "%20");
        } while (index != -1);// 若没有空格则结束循环

        return str;
    }

    public static String[] sort(String fullName) {

        String strArray[] = new String[3];
        String provinceAndiso = "";
        String city = "";
        String province = "";
        String iso = "";
        //判断是否包含逗号","
        boolean comma1 = fullName.contains(",");
        if (comma1) {

//            System.out.println("包含");
            provinceAndiso = fullName.substring(fullName.indexOf(",") + 1);

            city = fullName.substring(0, fullName.indexOf(","));

            //判断是否包含逗号","
            boolean comma2 = provinceAndiso.contains(",");
            if (comma2) {
                //包含第二个逗号
                province = provinceAndiso.substring(0, provinceAndiso.indexOf(","));
                iso = provinceAndiso.substring(provinceAndiso.indexOf(",") + 1);

            } else {
                //不包含第二个逗号
                province = provinceAndiso;
                iso = "";
            }

        } else {
//            System.out.println("不包含");
            city = fullName;

        }

        city = city.trim();// 去掉首尾空格
        province = province.trim();// 去掉首尾空格;
        iso = iso.trim();// 去掉首尾空格;

        strArray[0] = city;
        strArray[1] = province;
        strArray[2] = iso;

        return strArray;
    }

    public  String parseJson(String[] strArray) {

        String str1 = strArray[0];
        String str2 = strArray[1];
        String str3 = strArray[2];
        String str4 = strArray[3];

        JsonObject object = new JsonObject();
        JsonArray array = new JsonArray();
        JsonObject lan1 = new JsonObject();

        lan1.addProperty("woeid", str1);

        lan1.addProperty("city", str2);

        lan1.addProperty("province", str3);
        lan1.addProperty("iso", str4);

//将 lan1 添加到 array

        array.add(lan1);
        object.add("WoeidData", array);

        MyApplication.json = object.toString();
        Log.d("koma==json", object.toString());
//        if (MyApplication.json != null && !MyApplication.json.equals(""))
//            mHandler.sendEmptyMessage(-3);

        return "";
    }

}
