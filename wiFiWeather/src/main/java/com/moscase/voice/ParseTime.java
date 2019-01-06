package com.moscase.voice;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析语音结果获取时间
 *
 * @author lxj
 */
public class ParseTime {

    /**
     * 是否包含上午下午
     */
    private static boolean isPM = false;

    /**
     * 是否包含过
     */
    private static boolean isOver = false;

    /**
     * 是否包含差
     */
    private static boolean isFew = false;
    // private boolean isPM = false;

    // public static final int SUCCESS = 0; // 解析出时间
    // public static final int ERROR = -1; // 解析不出时间

    public static String[] CNTime = {"零点", "一点", "二点", "三点", "四点", "五点", "六点",
            "七点", "八点", "九点", "十点", "十一点", "十二点", "十三点", "十四点", "十五点", "十六点",
            "十七点", "十八点", "十九点", "二十点", "二十一点", "二十二点", "二十三点",
    };

    public static String[] CNLCTime = {"凌晨零点", "凌晨一点", "凌晨二点", "凌晨三点", "凌晨四点",
            "凌晨五点", "凌晨六点", "凌晨七点", "凌晨八点", "凌晨九点", "凌晨十点", "凌晨十一点", "凌晨十二点",
            "凌晨十三点", "凌晨十四点", "凌晨十五点", "凌晨十六点", "凌晨十七点", "凌晨十八点", "凌晨十九点",
            "凌晨二十点", "凌晨二十一点", "凌晨二十二点", "凌晨二十三点",};

    public static String[] DianTime = {"零点", "一点", "二点", "三点", "四点", "五点",
            "六点", "七点", "八点", "九点", "十点", "十一点", "十二点", "十三点", "十四点", "十五点",
            "十六点", "十七点", "十八点", "十九点", "二十点", "二十一点", "二十二点", "二十三点",};

    public static String[] FenTime = {"零分", "一分", "两分", "三分", "四分", "五分",
            "六分", "七分", "八分", "九分", "十分", "十一分", "十二分", "十三分", "十四分", "十五分",
            "十六分", "十七分", "十八分", "十九分", "二十分", "二十一分", "二十二分", "二十三分", "二十四分",
            "二十五分", "二十六分", "二十七分", "二十八分", "二十九分", "三十分", "三十一分", "三十二分",
            "三十三分", "三十四分", "三十五分", "三十六分", "三十七分", "三十八分", "三十九分", "四十分",
            "四十一分", "四十二分", "四十三分", "四十四分", "四十五分", "四十六分", "四十七分", "四十八分",
            "四十九分", "五十分", "五十一分", "五十二分", "五十三分", "五十四分", "五十五分", "五十六分",
            "五十七分", "五十八分", "五十九分"};

    public static String[] ShuTime = {"零", "一", "两", "三", "四", "五", "六", "七",
            "八", "九", "十", "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八",
            "十九", "二十", "二十一", "二十二", "二十三", "二十四", "二十五", "二十六", "二十七", "二十八",
            "二十九", "三十", "三十一", "三十二", "三十三", "三十四", "三十五", "三十六", "三十七",
            "三十八", "三十九", "四十", "四十一", "四十二", "四十三", "四十四", "四十五", "四十六",
            "四十七", "四十八", "四十九", "五十", "五十一", "五十二", "五十三", "五十四", "五十五",
            "五十六", "五十七", "五十八", "五十九"};
    
    public static String[] EnglishHourTime = {"Zero", "One", "Two", "Three", "Four", "Five", "Six",
            "Seven", "Eight", "Nine", "Ten", "Evelen", "Twelve", "Thirteen", "Fourteen",

            "Fifteen", "Sixteen",
            "Seventeen", "Eighteen", "Nineteen", "Twenty", "Twenty one", "Twenty two", "Twenty " +
            "three"
    };

    public static String[] EnglishHourTime1 = {"zero", "one", "two", "three", "four", "five",
            "six", "seven", "eight", "nine", "ten", "eleven", "twelve"};

    public static String[] EnglishMinTime = {"zero", "one", "two", "three", "four", "five",
            "six", "seven",
            "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen",
            "sixteen", "seventeen", "eighteen",
            "nineteen", "twenty", "twenty one", "twenty two", "twenty three", "twenty four",
            "twentym five", "twenty six", "twenty seven", "twenty eight",
            "twenty nine", "thirty", "thirty one", "thirty two", "thirty three", "thirty four",
            "thirty five", "thirty six", "thirty seven",
            "thirty eight", "thirty nine", "forty", "forty one", "forty two", "forty three",
            "forty four", "forty five", "forty six",
            "forty seven", "forty eight", "forty nine", "fifty", "fifty one", "fifty two", "fifty" +
            " three", "fifty four", "fifty five",
            "fifty six", "fifty seven", "fifty eight", "fifty nine"};

    public static String[] EnglishMinTimeUp = {"Zero", "One", "Two", "Three", "Four", "Five",
            "Six", "Seven",
            "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
            "Sixteen", "Seventeen", "Eighteen",
            "Nineteen", "Twenty", "Twenty one", "Twenty two", "Twenty three", "Twenty four",
            "Twentym five", "Twenty six", "Twenty seven", "Twenty eight",
            "Twenty nine", "Thirty", "Thirty one", "Thirty two", "Thirty three", "Thirty four",
            "Thirty five", "Thirty six", "Thirty seven",
            "Thirty eight", "Thirty nine", "Forty", "Forty one", "Forty two", "Forty three",
            "Forty four", "Forty five", "Forty six",
            "Forty seven", "Forty eight", "Forty nine", "Fifty", "Fifty one", "Fifty two", "Fifty" +
            " three", "Fifty four", "Fifty five",
            "Fifty six", "Fifty seven", "Fifty eight", "Fifty nine"};

    public static String ParseEnglishTime(String s) {
        String result = "";
        String hour = "";
        String min = "";

        for (int i = 0; i < EnglishHourTime.length; i++) {
            if (s.equals(EnglishHourTime[i])) {
                if (i < 10)
                    hour = "0" + i;
                else
                    hour = i + "";

                result = hour + ":" + "00";

                return result;
            }
        }


        String[] temp = s.split(" ");
        if (s.contains("past")) {
            if (temp[0].contains("Half")) {
                for (int i = 0; i < EnglishHourTime1.length; i++) {
                    if (temp[2].contains(EnglishHourTime1[i])) {
                        if (i < 10)
                            hour = "0" + i;
                        else
                            hour = i + "";
                    }
                }
                result = hour + ":" + "30";

            } else if (s.contains("quarter") || s.contains("Quarter")) {
                for (int i = 0; i < EnglishHourTime1.length; i++) {
                    if (temp[temp.length - 1].contains(EnglishHourTime1[i])) {
                        if (i < 10)
                            hour = "0" + i;
                        else
                            hour = i + "";
                    }
                }
                result = hour + ":" + 15;

            } else {
                for (int i = 0; i < EnglishHourTime1.length; i++) {
                    if (temp[temp.length - 1].contains(EnglishHourTime1[i])) {
                        if (i < 10)
                            hour = "0" + i;
                        else
                            hour = i + "";
                    }
                }
                for (int i = 0; i < EnglishMinTimeUp.length; i++) {
                    if (s.contains(EnglishMinTimeUp[i])) {
                        if (i < 10)
                            min = "0" + i;
                        else
                            min = i + "";
                    }
                }
                result = hour + ":" + min;
            }

        } else if (s.contains("to")) {
            if (s.contains("quarter")) {
                for (int i = 0; i < EnglishHourTime1.length; i++) {
                    if (temp[temp.length - 1].contains(EnglishHourTime1[i])) {
                        if (i < 11)
                            hour = "0" + (i - 1);
                        else
                            hour = (i - 1) + "";
                    }
                }
                result = hour + ":" + 45;
            } else {
                for (int i = 0; i < EnglishHourTime1.length; i++) {
                    if (temp[temp.length - 1].contains(EnglishHourTime1[i])) {
                        if (i < 11)
                            hour = "0" + (i - 1);
                        else
                            hour = (i - 1) + "";
                    }
                }

                for (int i = 0; i < EnglishMinTimeUp.length; i++) {
                    if (temp[0].contains(EnglishMinTimeUp[i])) {
                        if (i < 10)
                            min = "0" + i;
                        else
                            min = (60 - i) + "";
                    }
                }
                result = hour + ":" + min;
            }
        } else if (s.contains("clock")) {
            for (int i = 0; i < EnglishHourTime.length; i++) {
                if (temp[0].contains(EnglishHourTime[i])) {
                    if (i < 10)
                        hour = "0" + i;
                    else
                        hour = i + "";
                }
            }
            result = hour + ":" + "00";
        } else if (temp.length == 2) {
            for (int i = 0; i < EnglishHourTime.length; i++) {
                if (temp[0].contains(EnglishHourTime[i])) {
                    if (i < 10)
                        hour = "0" + i;
                    else
                        hour = i + "";
                }
            }

            for (int i = 0; i < EnglishMinTime.length; i++) {
                if (temp[1].equals(EnglishMinTime[i])) {
                    if (i < 10)
                        min = "0" + i;
                    else
                        min = i + "";
                }
            }

            if (hour.equals("") || min.equals(""))
                return "ERROR";
            else
                result = hour + ":" + min;
        } else if (temp.length == 3) {
            Log.d("koma","解析的语言是三位");
            for (int i = 0; i < EnglishHourTime.length; i++) {
                if (temp[0].contains(EnglishHourTime[i])) {
                    if (i < 10)
                        hour = "0" + i;
                    else
                        hour = i + "";
                    Log.d("koma===三位的小时是",hour);
                }
            }
            int tempMin = 0;
            for (int i = 0; i < EnglishMinTime.length; i++) {
                if (temp[1].equals(EnglishMinTime[i])) {
                    tempMin = i;
                }
            }


            for (int i = 0; i < EnglishMinTime.length; i++) {
                if (temp[2].equals(EnglishMinTime[i])) {
                    tempMin += i;
                    Log.d("koma===三位的分钟是",tempMin+"");
                }
            }
            if (hour.equals("") || tempMin==0)
                result = "ERROR";
            else
                result = hour + ":" + tempMin;

        } else {
            return "ERROR";
        }

        Log.d("koma---result", result);

        return result;
    }

    public static String pTime(String str) {
        isPM = false;
        isOver = false;
        isFew = false;

        String result = "ERROE";
        String hour = "";
        String min = "00";

        // 是否有下午
        if ((str.indexOf("下午") != -1) || (str.indexOf("晚上") != -1)) {
            isPM = true;
        }

        // 是标准的时间格式
        if (HasStandardTime(str)) {
            String[] strs = str.split(":");
            // 小时大于23或者小于0 分钟数大于59或者小于0
            if (Integer.parseInt(strs[0]) > 23 || Integer.parseInt(strs[0]) < 0
                    || Integer.parseInt(strs[1]) < 0
                    || Integer.parseInt(strs[1]) > 59) {
                return "ERROR";
            } else {// 时间正常
                hour = (str.split(":")[0]);
                min = (str.split(":")[1]);
                if (Integer.parseInt(hour) < 10) {
                    hour = "0" + Integer.parseInt(hour);
                }
                if (Integer.parseInt(min) < 10) {
                    System.out.println("强转之后的数据是" + Integer.parseInt(min));
                    min = "0" + Integer.parseInt(min);
                }
                return hour + ":" + min;
            }
        }

        // 包含标准的时间格式
        if (ContainStandardTime(str)) {
            String time = getTime(str);
            int ihour = Integer.parseInt(time.split(":")[0]);
            int imin = Integer.parseInt(time.split(":")[1]);
            if (ihour < 24 && ihour >= 0 && imin < 60 && imin >= 0) {
                String hour1 = time.split(":")[0];
                String min1 = time.split(":")[1];
                if (isPM) {
                    if (ihour != 0 && ihour < 12) {
                        hour1 = (ihour + 12) + "";
                    }
                }

                if (Integer.parseInt(hour1) < 10) {
                    hour1 = "0" + Integer.parseInt(hour1);
                }

                if (Integer.parseInt(min1) < 10) {
                    min1 = "0" + Integer.parseInt(min1);
                }

                if (str.indexOf(min1 + "吧") != (-1)
                        && (Integer.parseInt(min1) % 10 == 0)) {
                    min1 = "" + (Integer.parseInt(min1) + 8);
                }

                return hour1 + ":" + min1;
            }
        }

        // 包含小数
        if (ContainDouble(str)) {
            // Log.e("解析时间", "小数点");
            String doub = getDouble(str);
            String[] s = doub.split("\\.");
            int ihour = Integer.parseInt(s[0]);
            int imin = Integer.parseInt(s[1]);
            if (ihour < 24 && imin < 60 && ihour >= 0 && imin >= 0) {
                String[] ss = str.split(doub);
                String sHour = "" + ihour;
                String sMin = "" + imin;
                if (ihour < 10) {
                    sHour = "0" + ihour;
                }
                if (imin < 10) {
                    sMin = "0" + imin;
                }
                // 如果小数后面接的是“十分”且小数位乘以十不大于59 那么返回的是小时加上小数位乘以十的分钟数
                // 否则则直接返回小数表示的时间
                if (ss.length > 1 && ss[1].equals("十分") && (imin * 10) < 59) {
                    if (imin * 10 == 0)
                        sMin = "10";
                    if (imin * 10 != 0)
                        sMin = "" + imin * 10;
                    return sHour + ":" + sMin;
                }
                return sHour + ":" + sMin;
            }
        }

        // 包含小数点
        if (ContainPoint(str)) {
            // 将字符串从.分割
            String[] ss = str.split("\\.");
            //
            if (ss.length > 1) {
                String mHour = getNumber(ss[0]);
                String mMin = getNumber(ss[1]);
                if (!"".equals(mHour)) {
                    hour = mHour;
                } else {
                    for (int i = 23; i >= 0; i--) {
                        if (mHour.indexOf(ShuTime[i]) != (-1)) {
                            hour = i + "";
                            break;
                        }
                    }
                }

                if (!"".equals(mMin)) {
                    min = mMin;
                } else {
                    for (int i = 59; i >= 0; i--) {
                        if (mHour.indexOf(ShuTime[i]) != (-1)) {
                            min = i + "";
                            break;
                        }
                    }
                }
            }
        }

        /**
         * 前面已经将特殊情况分析完 首先查找等待解析的字符串中是否包涵下午、晚上 查找是否包含 字符 “点” 如果没包含说明不是个正常的时间
         * 直接返回“ERROR” 包含点的话 分析时间和分钟
         */
        // 是否包含差
        if (str.indexOf("差") != -1) {
            System.out.println("含有差");
            isFew = true;
        }

        // 是否包含差
        if (str.indexOf("过") != -1) {
            isOver = true;
        }

        // 查找 “点”
        String containHour = "";
        String containMin = "";
        int index = str.indexOf("点");
        if (index > -1) {
            containHour = str.substring(0, index + 1);
            containMin = str.substring(index + 1, str.length());

        } else {
            return "ERROR";
        }

        // 解析 点 前面部分 是否有数字
        if (HasDigit(containHour)) { // 有数字
            hour = getNumbers(containHour);
        } else {
            for (int i = 23; i >= 0; i--) {
                if (containHour.indexOf(DianTime[i]) != (-1)) {
                    hour = i + "";
                    break;
                }
            }
            if (containHour.indexOf("两点") != -1) {
                hour = 2 + "";
            }
        }
        System.out.println(hour);
        int iHour = -1;
        if (hour.length() != 0) {
            iHour = Integer.parseInt(hour);
        }
        if (iHour > 23 || iHour < 0) {
            return "ERROR";
        }
        if (isPM) {
            if (iHour != 0 && iHour < 12) {
                iHour = iHour + 12;
            }
        }

        // 解析 点 后面部分 是否有数字
        if (containMin.length() > 0 && HasDigit(containMin)) { // 有数字 且后面部分有zhi

            min = getNumbers(containMin);

        } else if (containMin.length() > 0) { // 没有数字 但是后面不为空
            for (int j = 59; j >= 0; j--) {
                if (containMin.indexOf(FenTime[j]) != (-1)
                        || containMin.indexOf(ShuTime[j]) != (-1)) {
                    min = j + "";
                    break;
                }
            }

            if (containMin.indexOf("半") != -1) {
                min = "30";
            }
            if (containMin.indexOf("一刻") != -1) {
                min = "15";
            }
            if (containMin.indexOf("两刻") != -1) {
                min = "30";
            }
            if (containMin.indexOf("三刻") != -1) {
                min = "45";
            }
            if (containMin.indexOf("差吧") != -1
                    || containMin.indexOf("过吧") != -1) {
                min = "08";
            }

        } else {
            min = "00";
        }

        System.out.println(min);

        int iMin = 0;
        if (min.length() != 0) {
            iMin = Integer.parseInt(min);
        }

        if (iMin > 59 || iMin < 0) {
            return "ERROR";
        }

        // 如果含有差 并且分钟数大于0
        if (isFew && iMin > 0) {
            iHour = iHour - 1;
            if (iHour == -1) {
                iHour = 23;
            }
        }

        // 如果含有差 但是分钟不为0才用60减去分钟
        if (isFew && iMin > 0) {
            iMin = 60 - iMin;
        }

        // 处理最后的结果
        hour = "" + iHour;
        if (iHour < 10) {
            hour = "0" + iHour;
        }

        min = "" + iMin;
        if (iMin < 10) {
            min = "0" + iMin;
        }

        result = hour + ":" + min;

        System.out.println(result);
        return result;
    }


    public static String parse(String str) {

        // 是否是标准的时间格式
        if (HasStandardTime(str)) {
            String[] strs = str.split(":");
            // 小时大于23或者小于0 分钟数大于59或者小于0
            if (Integer.parseInt(strs[0]) > 23 || Integer.parseInt(strs[0]) < 0
                    || Integer.parseInt(strs[1]) < 0
                    || Integer.parseInt(strs[1]) > 59) {
                return "ERROR";
            } else {// 时间正常
                String hour = (str.split(":")[0]);
                String min = (str.split(":")[1]);
                if (Integer.parseInt(hour) < 10) {
                    hour = "0" + hour;
                }
                if (Integer.parseInt(min) < 10) {
                    min = "0" + min;
                }
                return hour + ":" + min;
            }
            // 包含数字
        } else if (HasDigit(str)) {
            // 包含有标准时间格式
            if (ContainStandardTime(str)) {
                String time = getTime(str);
                int hour = Integer.parseInt(time.split(":")[0]);
                int min = Integer.parseInt(time.split(":")[1]);
                if (hour < 24 && hour >= 0 && min < 60 && min >= 0) {
                    String[] ss = str.split(time);
                    if (ss[0].equals("上午") || ss[0].equals("中午")
                            || ss[0].equals("早上") || ss[0].equals("凌晨")
                            || hour > 12) {
                        String hour1 = time.split(":")[0];
                        String min1 = time.split(":")[1];
                        if (Integer.parseInt(hour1) < 10) {
                            hour1 = "0" + hour1;
                        }
                        if (Integer.parseInt(min1) < 10) {
                            min1 = "0" + min1;
                        }

                        return hour1 + ":" + min1;
                    } else if (ss[0].equals("下午") || ss[0].equals("晚上")) {
                        String hour1 = (hour + 12) + "";
                        return hour1 + ":" + min;
                    } else {
                        return getTime(str);
                    }
                }
                // 包含小数
            } else if (ContainDouble(str)) {
                Log.e("解析时间", "小数点");
                String doub = getDouble(str);
                String[] s = doub.split("\\.");
                int hour = Integer.parseInt(s[0]);
                int min = Integer.parseInt(s[1]);
                if (hour < 24 && min < 60 && hour >= 0 && min >= 0) {
                    String[] ss = str.split(doub);
                    String sHour = "" + hour;
                    String sMin = "" + min;
                    if (hour < 10) {
                        sHour = "0" + hour;
                    }
                    if (min < 10) {
                        sMin = "0" + min;
                    }
                    // 如果小数后面接的是“十分”且小数位乘以十不大于59 那么返回的是小时加上小数位乘以十的分钟数
                    // 否则则直接返回小数表示的时间
                    if (ss.length > 1 && ss[1].equals("十分") && (min * 10) < 59) {
                        if (min * 10 == 0)
                            sMin = "10";
                        if (min * 10 != 0)
                            sMin = "" + min * 10;
                        return sHour + ":" + sMin;
                    }
                    return sHour + ":" + sMin;
                }
                return "ERROR";
                // 包含数字 但是没有固定的格式
            } else {

                // //十三点过55分
                for (int i = 23; i >= 0; i--) {
                    // 查看小时
                    if (str.indexOf(DianTime[i]) != (-1)) {
                        String hour = i + "";
                        String min = "";
                        if (i < 10) {
                            hour = "0" + i + "";
                        }

                        min = getNumbers(str);
                        int m = Integer.parseInt(min);

                        if (str.indexOf("过") != (-1)) {
                            return hour + ":" + min;
                        }

                        if (str.indexOf("差") != (-1)) {
                            if ((Integer.parseInt(hour) - 1) < 10) {
                                hour = "0" + (i - 1) + "";
                                if ((Integer.parseInt(hour) - 1) == -1) {
                                    hour = "23";
                                }
                            } else {
                                hour = (i - 1) + "";
                            }
                            if ((60 - m) < 10) {
                                min = "0" + (60 - m);
                            } else {
                                min = "" + (60 - m);
                            }
                            return hour + ":" + min;
                        }
                    }
                }

                String min = "00";
                String hour = getNumbers(str);
                String[] ss = str.split(hour + "点");

                if (Integer.parseInt(hour) < 24 && Integer.parseInt(hour) >= 0) {

                    if (str.indexOf("点半") != -1) {
                        min = "30";
                        if (ss.length > 0
                                && (ss[0].equals("下午") || ss[0].equals("晚上"))
                                && Integer.parseInt(hour) < 12) {
                            if (Integer.parseInt(hour) > 0) {
                                hour = "" + (Integer.parseInt(hour) + 12);
                            }
                        }
                        return hour + ":" + min;
                    }

                    if (ss[0].equals("上午") || ss[0].equals("中午")
                            || ss[0].equals("早上") || ss[0].equals("凌晨")
                            || Integer.parseInt(hour) > 12) {
                        // 上午11点
                        if (Integer.parseInt(hour) < 10) {
                            hour = "0" + hour;
                        }

                        if (ss.length > 1) {
                            // 上午11点半
                            if ("半".equals(ss[1])) {
                                min = "30";
                                return hour + ":" + min;
                                // 下午13点过55分 /下午13点过55
                            } else if (HasDigit(ss[1])) {
                                min = getNumbers(ss[1]);
                                if (Integer.parseInt(min) < 59
                                        && Integer.parseInt(min) >= 0) {
                                    int i = Integer.parseInt(hour);
                                    int j = Integer.parseInt(min);
                                    if (Integer.parseInt(min) < 10) {
                                        min = "0" + min;
                                    }

                                    if (ss[1].indexOf("过") != (-1)) {
                                        return hour + ":" + min;
                                    }

                                    if (ss[1].indexOf("差") != (-1)) {
                                        if ((Integer.parseInt(hour) - 1) < 10) {
                                            hour = "0" + (i - 1) + "";
                                            if ((Integer.parseInt(hour) - 1) == -1) {
                                                hour = "23";
                                            }
                                        } else {
                                            hour = (i - 1) + "";
                                        }
                                        if ((60 - j) < 10) {
                                            min = "0" + (60 - j);
                                        } else {
                                            min = "" + (60 - j);
                                        }
                                        return hour + ":" + min;
                                    }

                                }
                                // 下午13点过三分
                            } else {
                                int i = Integer.parseInt(hour);
                                for (int j = 59; j >= 0; j--) {
                                    if (str.indexOf(FenTime[j]) != (-1)) {
                                        min = j + "";
                                        if (j < 10) {
                                            min = "0" + j;
                                        }

                                        if (str.indexOf("过") != (-1)) {
                                            return hour + ":" + min;
                                        }

                                        if (str.indexOf("差") != (-1)) {
                                            if ((Integer.parseInt(hour) - 1) < 10) {
                                                hour = "0" + (i - 1) + "";
                                                if ((Integer.parseInt(hour) - 1) == -1) {
                                                    hour = "23";
                                                }
                                            } else {
                                                hour = (i - 1) + "";
                                            }
                                            if ((60 - j) < 10) {
                                                min = "0" + (60 - j);
                                            } else {
                                                min = "" + (60 - j);
                                            }
                                            return hour + ":" + min;
                                        }

                                    }
                                }
                            }
                        }
                        return hour + ":" + min;

                    } else if (ss[0].equals("下午") || ss[0].equals("晚上")) {
                        if (Integer.parseInt(hour) > 0) {
                            hour = "" + (Integer.parseInt(hour) + 12);
                        }
                        if (str.indexOf("点半") != -1) {
                            min = "30";
                            return hour + ":" + min;
                        }

                        if (ss.length > 1) {
                            if ("半".equals(ss[1])) {
                                min = "30";
                                return hour + ":" + min;
                            } else {
                                int i = Integer.parseInt(hour);
                                for (int j = 59; j >= 0; j--) {
                                    if (str.indexOf(FenTime[j]) != (-1)) {
                                        min = j + "";
                                        if (j < 10) {
                                            min = "0" + j;
                                        }
                                        if (str.indexOf("过") != (-1)) {
                                            return hour + ":" + min;
                                        }
                                        if (str.indexOf("差") != (-1)) {
                                            if ((Integer.parseInt(hour) - 1) < 10) {
                                                hour = "0" + (i - 1) + "";
                                                if ((Integer.parseInt(hour) - 1) == -1) {
                                                    hour = "23";
                                                }
                                            } else {
                                                hour = (i - 1) + "";
                                            }
                                            if ((60 - j) < 10) {
                                                min = "0" + (60 - j);
                                            } else {
                                                min = "" + (60 - j);
                                            }
                                            return hour + ":" + min;
                                        }

                                    }
                                }
                                return "ERROR";
                            }

                        } else {
                            if (ss[0].equals("半")) {
                                return hour + ":" + min;
                            }
                        }
                        return hour + ":" + min;
                    } else {

                    }
                }
            }

            return "ERROR";
            // 只有汉字
        } else {
            for (int i = 0; i < CNTime.length; i++) {
                if (CNTime[i].equals(str)) {
                    String ss = i + ":00";
                    if (i < 10) {
                        ss = "0" + i + ":00";
                    }
                    return ss;
                }
            }
            for (int i = 0; i < CNTime.length; i++) {
                if (CNLCTime[i].equals(str)) {
                    String ss = i + ":00";
                    if (i < 10) {
                        ss = "0" + i + ":00";
                    }
                    return ss;
                }
            }
            if ("两点".equals(str) || "凌晨两点".equals(str)) {
                return "02:00";
            }

            // //十三点过55分
            for (int i = 23; i >= 0; i--) {
                if (str.indexOf(DianTime[i]) != (-1)) {
                    String hour = i + "";
                    String min = "";
                    if (i < 10) {
                        hour = "0" + i + "";
                    }

                    for (int j = 59; j >= 0; j--) {
                        if (str.indexOf(FenTime[j]) != (-1)) {
                            min = j + "";
                            if (j < 10) {
                                min = "0" + j;
                            }

                            if (str.indexOf("过") != (-1)) {
                                return hour + ":" + min;
                            }

                            if (str.indexOf("差") != (-1)) {
                                if ((i - 1) < 10) {

                                    hour = "0" + (i - 1) + "";

                                    if ((i - 1) < 0) {
                                        hour = 23 + "";
                                    }

                                } else {
                                    hour = (i - 1) + "";
                                }
                                if ((60 - j) < 10) {
                                    min = "0" + (60 - j);
                                } else {
                                    min = "" + (60 - j);
                                }
                                return hour + ":" + min;
                            }

                        }
                    }
                }
            }

            return "ERROR";
        }
    }

    public static String ParseOnOff(String str) {
        boolean isAlarm = false;
        boolean isSnooze = false;
        boolean isOn = false;
        boolean isOff = false;
        int indexOn = str.lastIndexOf("开");
        int indexOff = str.lastIndexOf("关");
        String result = "ERROR";

        if (str.indexOf("闹钟") != -1 || str.indexOf("闹铃") != -1) {
            isAlarm = true;
        }
        if (str.indexOf("贪睡") != -1) {
            isSnooze = true;
        }
        if ((indexOn != -1)) {
            isOn = true;
        }

        if ((indexOff != -1)) {
            isOff = true;
        }

        // 先判断字符串是否包含贪睡或者闹钟 切同时包含开和关
        if ((isAlarm || isSnooze) && isOff && isOn) {
            if (indexOn > indexOff) {
                isOff = false;
            } else {
                isOn = false;
            }
        }

        // 贪睡和闹钟都包含的时候
        if (isAlarm && isSnooze) {
            if (isOff) {
                result = "ALL_OFF";
            } else if (isOn) {
                result = "ALL_ON";
            } else {
            }
        } else {
            // 闹钟开的时候
            if (isAlarm) {
                if (isOff) {
                    result = "ALARM_OFF";
                } else if (isOn) {
                    result = "ALARM_ON";
                }
            }

            // 贪睡开的时候
            if (isSnooze) {
                if (isOff) {
                    result = "SNOOZE_OFF";
                } else if (isOn) {
                    result = "SNOOZE_ON";
                }
            }
        }

        return result;
    }

    // 判断一个字符串是否是标准时间格式
    public static boolean HasStandardTime(String content) {
        boolean flag = false;
        Pattern p = Pattern.compile("[0-9]{1,2}:[0-9]{1,2}");
        Matcher m = p.matcher(content);
        if (m.matches()) {
            flag = true;
        }
        System.out.println("字符串中包含有时间" + flag);
        return flag;
    }

    // 判断一个字符串是否含有数字
    public static boolean HasDigit(String content) {
        boolean flag = false;
        Pattern p = Pattern.compile(".*\\d+.*");
        Matcher m = p.matcher(content);
        if (m.matches()) {
            flag = true;
        }
        System.out.println("字符串中包含有数字" + flag);
        return flag;
    }

    //
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern
                .compile("[\u4e00-\u9fa5]{1,}[0-9]{1-2}[\u4e00-\u9fa5]{1,}");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    // 判断一个字符串是否包含标准时间格式
    public static boolean ContainStandardTime(String content) {
        boolean flag = false;
        Pattern p = Pattern.compile(".*[0-9]{1,2}:[0-9]{1,2}.*");
        Matcher m = p.matcher(content);
        if (m.matches()) {
            flag = true;
        }
        System.out.println("字符串中包含有时间" + flag);
        return flag;
    }

    // 截取时间
    public static String getTime(String content) {
        Pattern pattern = Pattern.compile("[0-9]{1,2}:[0-9]{1,2}");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    // 截取数字
    public static String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    // // 截取小数
    // public static String getDouble(String content) {
    // Pattern pattern = Pattern.compile("[0-9]{1,2}\\.[0-9]{1,2}");
    // Matcher matcher = pattern.matcher(content);
    // while (matcher.find()) {
    // return matcher.group(0);
    // }
    // return "";
    // }
    //
    // // 判断一个字符串是否包含标小数
    // public static boolean ContainDouble(String content) {
    // boolean flag = false;
    // Pattern p = Pattern.compile(".*[0-9]{1,2}\\.[0-9]{1,2}.*");
    // Matcher m = p.matcher(content);
    // if (m.matches()) {
    // flag = true;
    // }
    // System.out.println("字符串中包含有小数" + flag);
    // return flag;
    // }

    // 截取小数
    public static String getDouble(String content) {
        Pattern pattern = Pattern.compile("[0-9]{1,2}\\.[0-9]{1,2}");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    // 判断一个字符串是否包含标小数
    public static boolean ContainDouble(String content) {
        boolean flag = false;
        Pattern p = Pattern.compile(".*[0-9]{1,2}\\.[0-9]{1,2}.*");
        Matcher m = p.matcher(content);
        if (m.matches()) {
            flag = true;
        }
        System.out.println("字符串中包含有小数" + flag);
        return flag;
    }

    // 判断一个字符串是否包含标小数点
    public static boolean ContainPoint(String content) {
        boolean flag = false;
        Pattern p = Pattern.compile(".*\\..*");
        Matcher m = p.matcher(content);
        if (m.matches()) {
            flag = true;
        }
        System.out.println("字符串中包含有小数" + flag);
        return flag;
    }

    // 截取数字
    public static String getNumber(String content) {
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

}
