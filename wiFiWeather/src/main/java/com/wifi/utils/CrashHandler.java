package com.wifi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Thread.UncaughtExceptionHandler;

/**
 * 异常信息上报
 */
public class CrashHandler implements UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";
    private static final boolean DEBUG = true;


    private static final String PATH =
            Environment.getExternalStorageDirectory() + "/Crash/log/";

    private static final String FILE_NAME = "crash";

    private static final String FILE_NAME_SUFFIX = ".log";

    private static CrashHandler sInstance = new CrashHandler();

    private UncaughtExceptionHandler mDefaultCrashHandler;
    private Context mContext;


    public static CrashHandler getInstance() {
        return sInstance;
    }

    private SharedPreferences mSharedPreferences;


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            this.obtainMessage();
            if (msg.what == 1) {

            }
            if (msg.what == 2) {

            }
            if (msg.what == 3) {
                String path = (String) msg.obj;
                Log.d("koma", "成功");
                File file1 = new File(path);
                file1.delete();
            }
            if (msg.what == 4) {

            }
        }
    };


    public void init(Context context) {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
        mSharedPreferences = mContext.getSharedPreferences("clock", Context.MODE_PRIVATE);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        Log.d("koma", "抓到了异常");
        boolean isCrash = mSharedPreferences.edit().putBoolean("isCrash", true).commit();
        if (isCrash)
            Log.d("koma", "已保存isCrash");
        // 导出异常信息到 SD 卡中
        dumpExceptionToSDCard(throwable);
        throwable.printStackTrace();

        // 如果系统提供了默认的异常处理器，则交给系统去结束程序，否则就由自己结束自己
        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler.uncaughtException(thread, throwable);
        } else {
            Process.killProcess(Process.myPid());
//            System.exit(0);
        }
    }

    private void dumpExceptionToSDCard(Throwable ex) {
        // 如果 SD 卡不存在或无法使用，则无法把异常信息写入 SD 卡
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (DEBUG) {
                Log.w(TAG, "sdcard unmounted, skip dump exception");
            }
            return;
        }

        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date(current));
        final File file = new File(PATH + FILE_NAME + FILE_NAME_SUFFIX);
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            String SerialNumber = android.os.Build.SERIAL;
            pw.print("设备ID: ");
            pw.println(SerialNumber);
            pw.println(time);
            dumpPhoneInfo(pw);
            pw.println();
            ex.printStackTrace(pw);
            pw.close();
            Log.d("koma", file.getPath());
//            UpLoad(file.getPath());
//            uploadFile(file, "http://120.25.207.192:8080/apps/android/WiFiWeather/8001/servlet" +
//                    "/UploadHandleServlet");


        } catch (Exception e) {
            Log.e(TAG, "dump crash info failed");
        }
    }

    private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager
                .GET_ACTIVITIES);
        // App 版本号
        pw.print("App版本号: ");
        pw.print(pi.versionName);
        pw.print("_");
        pw.println(pi.versionCode);

        // Android 系统版本号
        pw.print("Android 系统版本号: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);

        // 手机制造商
        pw.print("手机制造商: ");
        pw.println(Build.MODEL);

        // CPU 架构
        pw.print("CPU 架构: ");
        pw.println(Build.CPU_ABI);
    }
}

