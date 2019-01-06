package com.wifi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.mpw.constant.MyApplication;
import com.wwr.clock.R;

import java.util.Map;

public class TestRSA {
    static String publicKey;
    static String privateKey;
    static String mContent;
    static Context mContext;
    private static SharedPreferences sSharedPreferences;


    public static void main(Context context,String content) throws Exception {
        mContext = context;
        mContent = content;
        sSharedPreferences = mContext.getSharedPreferences("pass", mContext.MODE_PRIVATE);
        try {
            Map<String, Object> keyMap = RSAUtils.genKeyPair();

            //此公钥分四段，第一段就在这个页面，第二个在全局的MyApplication类，第三个在strings.XML文件，第四个在Share数据库
//            publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCHM8P2V8IgqMD7bjJKgxn+GoIZAfP/fzZ6Iy4/iuS5PfP8z8intgaZTK/H5ztYeDklFGoJv6FAQjpFYGKDHZL/ZB434CbWKLunW1hqdUNgYgJkLSjeAa+0REOHTl2di3zEpengebpMgFX0/MSNBd1JnN7tQPTcXbh615V9iE1oWwIDAQAB";

            publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBg" +MyApplication.key+
                    mContext.getString(R.string.key)+
                    sSharedPreferences.getString("key","");
            Log.d("koma===key",publicKey);


        } catch (Exception e) {
            e.printStackTrace();
        }
        test();
    }
    //加密数据
    static void test() throws Exception {
        String source = mContent;
        System.out.println("加密前文字：" + source);
        byte[] data = source.getBytes();

        byte[] encodedData = RSAUtils.encryptByPublicKey(data, publicKey);
        String base64Str = Base64Utils.encode(encodedData);

        MyApplication.data = base64Str;
        System.out.println("加密后文字：" + new String(encodedData));

//        //base64解码转成字节数组
//        byte[] encodedData1 = Base64Utils.decode(base64Str);
//        byte[] decodedData = RSAUtils.decryptByPrivateKey(encodedData1,privateKey);
//
//        String target = new String(decodedData);
//        System.out.println("解密后文字: \r\n" + target);
    }
    //验证签名
    static void testSign() throws Exception {
        System.err.println("私钥加密——公钥解密");
        String source = "这是一行测试RSA数字签名的无意义文字";
        System.out.println("原文字：\r\n" + source);
        byte[] data = source.getBytes();
        byte[] encodedData = RSAUtils.encryptByPrivateKey(data, privateKey);
        System.out.println("加密后：\r\n" + new String(encodedData));
        byte[] decodedData = RSAUtils.decryptByPublicKey(encodedData, publicKey);
        String target = new String(decodedData);
        System.out.println("解密后: \r\n" + target);
        System.err.println("私钥签名——公钥验证签名");
        String sign = RSAUtils.sign(encodedData, privateKey);
        System.err.println("签名:\r" + sign);
        boolean status = RSAUtils.verify(encodedData, publicKey, sign);
        System.err.println("验证结果:\r" + status);
    }
}
