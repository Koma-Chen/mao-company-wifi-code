package com.wwr.clock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.demo_activity.EspWifiAdminSimple;
import com.hiflying.smartlink.ISmartLinker;
import com.hiflying.smartlink.OnSmartLinkListener;
import com.hiflying.smartlink.SmartLinkedModule;
import com.hiflying.smartlink.v3.SnifferSmartLinker;
import com.hiflying.smartlink.v7.MulticastSmartLinker;
import com.iflytek.aimic.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.moscase.voice.JsonParser;
import com.moscase.voice.ParseTime;
import com.moscase.voice.VoiceDialog;
import com.mpw.constant.Constant;
import com.mrwujay.cascade.service.Event;
import com.mrwujay.cascade.service.FirstEvent;
import com.mrwujay.cascade.service.FiveEvent;
import com.mrwujay.cascade.service.FourEvent;
import com.mrwujay.cascade.service.LevelEvent;
import com.mrwujay.cascade.service.SecordEvent;
import com.mrwujay.cascade.service.SexEvent;
import com.mrwujay.cascade.service.ThreeEvent;
import com.mrwujay.cascade.service.ZeorEvent;
import com.umeng.message.PushAgent;
import com.wifi.utils.DialogActivity;
import com.wifi.utils.LogUtil;
import com.wifi.utils.UpdateWifiInfo;
import com.wwr.clock.temp.SqliteOpenHelper;
import com.wwr.locationselect.PagerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import de.greenrobot.event.EventBus;


public class MainActivity extends Activity implements OnSmartLinkListener {

    private Button display;

    // 开始语音的时间
    private long starMillis;
    // 讯飞语音听写对象
    // 语音听写对象
    SpeechRecognizer mIat;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    // 引擎类型 云
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    // 按住的时候显示的dialog
    private VoiceDialog voiceDialog;

    private boolean isLastResult = false;
    ImageView iv_setting;
    private int haveOutMoudle = -1;

    static ImageView iv_wifi;
    // 一进来就获取保存mac地址的本地SP文件
    Context context;
    String seatime;
    private EditText mSsidEditText, mPasswordEditText;
    View mainLayout;
    Button mStartButton, btnscoket, btncity, btnwendu, btnsearchtime;
    Spinner spcwendu;
    int selectedsexIndex;
    String selecttime;
    Switch switchclock, switchlan;
    String clocktime, name, time01, time00;
    String str;
    boolean isConnect = false;

    private int count = 0;
    private boolean isLongClick = false;

    private boolean is24 = false; // 系统时间是不是24小时制的

    private EspWifiAdminSimple mWifiAdmin;
    private String apBssid; // 手机的mac地址
    private String apSsid; // wifi账号
    private String apPassword; // wifi密码

    String swl, swc, wendu, wen, mac, cn, Min;
    String Hou, hou;
    String ampm, apm, timeampm;
    String alarm, snooze;
    int mResultCode;
    Intent mResultData;
    private TextView textalarm, textOnoff1, textsnooze, textonoff2, textampm;
    TextView texttime;
    LinearLayout tv_alarm_set;
    protected ISmartLinker mSnifferSmartLinker;
    private boolean mIsConncting = false;
    protected Handler mViewHandler = new Handler();
    protected ProgressDialog mWaitingDialog;
    Dialog dialog;
    private BroadcastReceiver mWifiChangedReceiver;
    int hour;
    private int minute;
    private String StringE, lngCityName;
    private LocationClient locationClient = null;

    public static final String EXTRA_SMARTLINK_VERSION = "EXTRA_SMARTLINK_VERSION";

    private static final String TAG = "MainActivity";
    private String serverVersion;
    private String clientVersion;
    ProgressDialog pd = null;
    Socket socket = null;
    private UpdateWifiInfo mUwi;
    private SqliteOpenHelper sqliteOpenHelper;
    private SharedPreferences sp;

    // private String[] sex = { "3 "+getString(R.string.hour),
    // "6 "+getString(R.string.hour), "12 "+getString(R.string.hour),
    // "24 "+getString(R.string.hour) };

    // boolean isConnectWifi = false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mian_activity);

        //------------------我是一个测试用例

        PushAgent.getInstance(context).onAppStart();

        iv_setting = (ImageView) findViewById(R.id.iv_setting);

        int smartLinkVersion = getIntent().getIntExtra(EXTRA_SMARTLINK_VERSION,
                3);
        if (smartLinkVersion == 7) {
            mSnifferSmartLinker = MulticastSmartLinker.getInstance();
        } else {
            mSnifferSmartLinker = SnifferSmartLinker.getInstance();
        }
        mWaitingDialog = new ProgressDialog(this);
        mWaitingDialog
                .setMessage(getString(R.string.hiflying_smartlinker_waiting));
        mWaitingDialog.setButton(ProgressDialog.BUTTON_NEGATIVE,
                getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        mWaitingDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mSnifferSmartLinker.setOnSmartLinkListener(null);
                mSnifferSmartLinker.stop();
                mIsConncting = false;
            }
        });

        iv_wifi = (ImageView) findViewById(R.id.iv_wifi);
        mWifiAdmin = new EspWifiAdminSimple(this); // 这是获得wifi管理器 从而获得wifi相关信息
        btnsearchtime = (Button) findViewById(R.id.btnsearchTime);// 自动更新信息的间隔时间
        textalarm = (TextView) findViewById(R.id.textalarm);//
        textampm = (TextView) findViewById(R.id.textAmpm); // 上午下午
        textOnoff1 = (TextView) findViewById(R.id.TextView01);// 开关一 闹钟
        textonoff2 = (TextView) findViewById(R.id.textonoff2);// 开关二 贪睡
        textsnooze = (TextView) findViewById(R.id.textsnooze);//
        texttime = (TextView) findViewById(R.id.texttime); // 显示时间
        btnwendu = (Button) findViewById(R.id.btn_wendo); // 温度按钮
        btncity = (Button) findViewById(R.id.btncity); // 城市按钮
        mSsidEditText = (EditText) findViewById(R.id.editname); // wifi名字编辑框
        mPasswordEditText = (EditText) findViewById(R.id.editpass);// wifi密码编辑框
        mStartButton = (Button) findViewById(R.id.conn_wifi);// 开始连接
        btnscoket = (Button) findViewById(R.id.buttonconn);// 更新信息按钮
        mainLayout = (View) findViewById(R.id.view); // LinearLayout控件
        context = MainActivity.this;
        tv_alarm_set = (LinearLayout) findViewById(R.id.tv_alarm_set);
        sp = getSharedPreferences("pass",MODE_PRIVATE);


        display = (Button) findViewById(R.id.display);

        SharedPreferences display_days = getSharedPreferences(
                "displauy_days", Activity.MODE_PRIVATE);
        sqliteOpenHelper = new SqliteOpenHelper(MainActivity.this,"Out.db",null,1);

        display.setText("03".equals(display_days.getString("days", "03")) ? getString(R.string.display3) : getString(R.string.display1));

        display.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (display.getText().toString().equals(getString(R.string.display1))) {


                    display.setText(getString(R.string.display3));

                    SharedPreferences display_days = getSharedPreferences(
                            "displauy_days", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = display_days.edit();
                    editor.putString("days", "0" + 3);
                    editor.commit();
                } else {
                    display.setText(getString(R.string.display1));
                    SharedPreferences display_days = getSharedPreferences(
                            "displauy_days", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = display_days.edit();
                    editor.putString("days", "0" + 1);
                    editor.commit();
                }

            }
        });


        // mPasswordEditText.setTransformationMethod(MPasswordTransformationMethod.getInstance());
        mPasswordEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                String name = mSsidEditText.getText().toString();

                if (name != null && !TextUtils.isEmpty(name)
                        && !"No Wifi connection".equals(name)) {

                }
                ;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 从本地获取mac地址并判断其是否存在 设置wifi图标颜色
        SharedPreferences macSP = getSharedPreferences("test",
                Activity.MODE_PRIVATE);
        if (macSP.getString("Adress", "").equals("")) {
            iv_wifi.setImageResource(R.drawable.wifi_graw);
        } else {
            if (isWifiConnected(context)) {
                iv_wifi.setImageResource(R.drawable.wifi_blue);
            }
        }

        // 设置城市的相关信息
        SharedPreferences citys = getSharedPreferences("Citys",
                Activity.MODE_PRIVATE);
        cn = citys.getString("cityType", "01");
        String cityName = citys.getString("cityName", "北京");
        String cityID = citys.getString("cityID", "101010100");
        btncity.setText(cityName);
        Log.e("LAG", "从本地获得的数据是：城市名是" + cityName + "	城市ID是" + cityID + "	城市类型是"
                + cn);
        if ("01".equals(cn)) {
            StringE = cityID;
        } else if ("00".equals(cn)) {
            StringE = citys.getString("toService", "");
            System.out.println("99999999999999----------" + StringE);
        }
        // 设置自动更新的间隔时间

        // 从本地中获取数据
        SharedPreferences get_choce = getSharedPreferences("chooce",
                Activity.MODE_PRIVATE);
        int index = get_choce.getInt("chooce", 1);
        String[] sex = {
                "1 " + getString(R.string.hour),
                "3 " + getString(R.string.hour),
                "6 " + getString(R.string.hour),
                "12 " + getString(R.string.hour),
        };
        selectedsexIndex = index;
        btnsearchtime.setText(sex[index]);

        // 设置温度的控制按钮
        SharedPreferences get_temperature = getSharedPreferences("temperature",
                Activity.MODE_PRIVATE);
        // 从本地sp文件中获取温度的值 如果获得的值不是空 则将该控件的值设置为该值
        String temperature = get_temperature.getString("temperature", "°C");
        if (temperature != "") {
            btnwendu.setText(temperature);
        }
        // 设置闹钟时间
        getSystemTime();
        // 获取sp文件第一个开关的状态
        SharedPreferences sharedPreferences = getSharedPreferences(
                "textOnoff1", Activity.MODE_PRIVATE);
        // 获取第一个开关的值
        String textoff1 = sharedPreferences.getString("tt", "OFF");
        // 如果第一个开关不为空
        if (textoff1 != null) {
            textOnoff1.setText(textoff1);
            // 第一个开关要是处于OFF状态，则设置开关字体为红色
            if (textoff1.equals("OFF")) {
                textOnoff1.setTextColor(android.graphics.Color.RED);
            } else {
                textOnoff1.setText("ON");
                textOnoff1.setTextColor(android.graphics.Color.WHITE);
            }
        } else {
            textOnoff1.setText("OFF");
            textOnoff1.setTextColor(android.graphics.Color.RED);
        }

        SharedPreferences sharedPreferences2 = getSharedPreferences("select",
                Activity.MODE_PRIVATE);
        String texton = sharedPreferences2.getString("text", "OFF");
        if (texton != null) {
            textonoff2.setText(texton);
            if (texton.equals("OFF")) {
                textonoff2.setTextColor(android.graphics.Color.RED);
            } else {
                textonoff2.setText("ON");
                textonoff2.setTextColor(android.graphics.Color.WHITE);
            }
        } else {
            textonoff2.setText("OFF");
            textonoff2.setTextColor(android.graphics.Color.RED);
        }
        EventBus.getDefault().register(this);

        // 设置自动更新的时间
        btnsearchtime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        MainActivity.this);
                // 选择几个小时更新一次
                String[] sex = {
                        "1 " + getString(R.string.hour),
                        "3 " + getString(R.string.hour),
                        "6 " + getString(R.string.hour),
                        "12 " + getString(R.string.hour),
                };
                builder.setSingleChoiceItems(sex, selectedsexIndex,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                selectedsexIndex = which;
                            }
                        });

                // 选择确定
                builder.setPositiveButton(getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String[] sex = {
                                        "1 " + getString(R.string.hour),
                                        "3 " + getString(R.string.hour),
                                        "6 " + getString(R.string.hour),
                                        "12 " + getString(R.string.hour)};
                                String str = sex[selectedsexIndex];
                                // 更新界面 显示选择的时间
                                btnsearchtime.setText(str);
                                // 将更新的时间间隔保存至本地SP文件中去
                                SharedPreferences sp_chooce = getSharedPreferences(
                                        "chooce", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp_chooce
                                        .edit();
                                editor.putInt("chooce", selectedsexIndex);
                                // editor.clear();
                                editor.commit();
                            }
                        });

                builder.setNegativeButton(getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                            }
                        });
                builder.setCancelable(false);
                builder.show();
            }
        });

        // 设置一个大的布局的点击事件 当点击了之后跳转到选择时间界面
        mainLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                startActivityForResult(new Intent(MainActivity.this,
                        SelectTimeActivity.class), 1);
            }
        });

        // 设置城市的点击事件 当点击的时候跳转至....
        btncity.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, PagerActivity.class);
                startActivity(intent);
            }
        });

        // 温度的点击事件 当前是摄氏度的时候就换成华氏度 反之亦然
        btnwendu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (btnwendu.getText() == "°C") {
                    btnwendu.setText("°F");
                } else {
                    btnwendu.setText("°C");
                }
                // 将选择的数值保存
                SharedPreferences sp_temperature = getSharedPreferences(
                        "temperature", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp_temperature.edit();
                editor.putString("temperature", btnwendu.getText().toString());
                editor.clear();
                editor.commit();

            }
        });

        // SpeechRecognizer对象的监听事件 不做处理
        InitListener mInitListener = new InitListener() {
            @Override
            public void onInit(int code) {
                Log.d(TAG, "SpeechRecognizer init() code = " + code);
                if (code != ErrorCode.SUCCESS) {
                    LogUtil.LOG("初始化失败，错误码：  " + code);
                }
            }
        };
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(MainActivity.this,
                mInitListener);

        voiceDialog = new VoiceDialog(MainActivity.this);

        setParam();

        tv_alarm_set.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(final View view, MotionEvent event) {
                // TODO Auto-generated method stub
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    if (count == 0) { // 每次事件（双击、单击、长按）只有第一次点击，才开启线程统计600ms内点击字数
                        boolean a = new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                if (count == 2) { // 双击
                                    // Toast.makeText(MainActivity.this, "双击",
                                    // Toast.LENGTH_SHORT).show();
                                } else if (count == 1) {// 单击
                                    // TODO Auto-generated method stub
                                    startActivityForResult(new Intent(
                                            MainActivity.this,
                                            SelectTimeActivity.class), 1);
                                } else if (count == 0) {

                                    LogUtil.LOG("开始了");

                                    starMillis = System.currentTimeMillis();
                                    isLastResult = false;
                                    int ret = mIat
                                            .startListening(mRecognizerListener);
                                    voiceDialog.show();

                                    if (ret != ErrorCode.SUCCESS) {
                                        // showTip("听写失败,错误码：" + ret);
                                    } else {
                                        // showTip("请开始说话");
                                        // mIat.stopListening();
                                    }
                                    isLongClick = true;// 设置标志位，长按
                                } else {

                                }
                                count = 0;// 判断结束，将count置为0
                            }
                        }, 340);
                    } else {
                        count = 0;
                    }

                    LogUtil.LOG("开始了" + count);
                    return true; // 此处若返回 false 则 action_up事件不会触发
                }

                if (MotionEvent.ACTION_UP == event.getAction()) {
                    if (isLongClick) {// 长按
                        isLongClick = false;

                        voiceDialog.dismiss();

                        // mIat.setParameter(SpeechConstant., arg1)
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        LogUtil.LOG("结束了");
                        mIat.stopListening();
                        return true;
                    }
                    count++;
                }
                return true;
            }
        });

        // socket的点击事件跳转至提交方法
        btnscoket.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (isWifiConnected(context) || Network(context)) {
                    SharedPreferences macSP = getSharedPreferences("test",
                            Activity.MODE_PRIVATE);
                    if (macSP.getString("Adress", "").equals("")) {
                        // mac地址为空的时候点击更新信息的按钮的提示
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                MainActivity.this); // 先得到构造器
                        builder.setMessage(getString(R.string.alert_message_connect_device)); // 设置内容
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {    // 设置确定按钮
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();
                    } else {
                        tijiao();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            MainActivity.this); // 先得到构造器

                    builder.setMessage(getString(R.string.alert_message_connect_network));
                    builder.setPositiveButton(getString(R.string.alert_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    // 参数都设置完成了，创建并显示出来
                    builder.create().show();

                }
            }
        });

        // 连接设备的按钮的点击事件
        mStartButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWifiConnected(context)) {
                    // 获得手机的mac地址
                    apBssid = mWifiAdmin.getWifiConnectedBssid();
                    // 获得wifi的名字
                    apSsid = mSsidEditText.getText().toString();
                    // 获得wifi的密码
                    apPassword = mPasswordEditText.getText().toString();
                    if (apPassword.isEmpty()) {
                        // 密码为空的提示
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                MainActivity.this); // 先得到构造器
                        builder.setMessage(getString(R.string.alert_message_sure_no_password)); // 设置内容
                        // 需要国际化
                        builder.setPositiveButton(
                                getString(R.string.alert_button_connect),
                                new DialogInterface.OnClickListener() { // 设置确定按钮
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                        new EsptouchAsyncTask2().execute(
                                                apSsid, apBssid, apPassword);
                                    }
                                });
                        // 不配置
                        builder.setNegativeButton(
                                getString(R.string.alert_button_cancel),
                                new DialogInterface.OnClickListener() { // 设置取消按钮
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                });
                        // 参数都设置完成了，创建并显示出来
                        builder.create().show();

                    } else {
                        Log.e("apSsid", "" + apSsid);
                        Log.e("apBssid", "" + apBssid);
                        Log.e("apPassword", "" + apPassword);
                        new EsptouchAsyncTask2().execute(apSsid, apBssid,
                                apPassword);

                        Editor editor = sp.edit();
                        editor.putString("pass",apPassword).commit();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            MainActivity.this); // 先得到构造器
                    builder.setMessage(getString(R.string.alert_message_connect_wifi));
                    builder.setPositiveButton(getString(R.string.alert_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    // 参数都设置完成了，创建并显示出来
                    builder.create().show();
                }
            }
        });
        mWifiChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (networkInfo != null && networkInfo.isConnected()) {
                    mSsidEditText.setText(getSSid());

//                    SharedPreferences SPremberPassword = getSharedPreferences(
//                            "remberPassword", Activity.MODE_PRIVATE);
//                    mPasswordEditText.setText(SPremberPassword.getString(
//                            getSSid(), ""));
                    mPasswordEditText.setText(sp.getString("pass",""));

                    SplashActivity.downImg();
                } else {
                    if (!Network(context)) {
                        iv_wifi.setImageResource(R.drawable.wifi_graw);
                    }
                    mSsidEditText
                            .setText(getString(R.string.hiflying_smartlinker_no_wifi_connectivity));
                    mPasswordEditText.setText("");
                    mSsidEditText.requestFocus();
                    if (mWaitingDialog.isShowing()) {
                        mWaitingDialog.dismiss();
                    }
                }
            }
        };
        registerReceiver(mWifiChangedReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));

    }

    // private class MyLocationListenner implements BDLocationListener {
    // @Override
    // public void onReceiveLocation(BDLocation location) {
    //
    // if (location == null)
    // return;
    // StringBuffer sb = new StringBuffer(256);
    // if (location.getLocType() == BDLocation.TypeGpsLocation) {
    // sb.append(location.getCity());
    // } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
    // sb.append(location.getCity());
    // }
    // if (sb.toString() != null && sb.toString().length() > 0) {
    // lngCityName = sb.toString();
    // btncity.setText(lngCityName);
    // }
    // }
    //
    // public void onReceivePoi(BDLocation poiLocation) {
    // }
    // }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mSnifferSmartLinker.setOnSmartLinkListener(null);
        try {
            unregisterReceiver(mWifiChangedReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLinked(final SmartLinkedModule module) {
        mViewHandler.post(new Runnable() {
            @Override
            public void run() {
                mac = module.getMac();
                SharedPreferences macSP = getSharedPreferences("test",
                        Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = macSP.edit();
                editor.putString("Adress", mac);
                editor.clear();
                editor.commit();
            }
        });
    }

    // 获取城市ID
    public void onEventMainThread(SecordEvent event) {
        String msg = event.getMsg();
        StringE = msg;
        SharedPreferences sp_chooce = getSharedPreferences("Citys",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp_chooce.edit();
        editor.putString("cityID", StringE);
        editor.commit();
    }

    public void onEventMainThread(FourEvent event) {
        String msg = event.getMsg();
        selecttime = msg;
    }

    // 选择外国城市之后 会通过此方法改变城市名的显示 获取城市名
    public void onEventMainThread(FirstEvent event) {
        String msg = event.getMsg();
        if (msg != null) {

            btncity.setText(msg);
            // 修改之前 传递过来的cityname是用来显示和发送给服务器一起的
            // 显示的地方用到的比较多 所以传递过来的用来显示 即对象中的name 发送给服务器的
            // 为了修改方便 这里传递过来的还是用来显示 发送给服务器的数据从文件中获取 toService
            /**
             * 最终的逻辑是这样子的： 传递过来的用来显示 发送给服务器的直接从文件里面拿
             */

            SharedPreferences sp_chooce = getSharedPreferences("Citys",
                    Activity.MODE_PRIVATE);
            StringE = sp_chooce.getString("toService", "");

            SharedPreferences.Editor editor = sp_chooce.edit();
            editor.putString("cityName", msg);
            editor.commit();
        }
    }

    public void onEventMainThread(ThreeEvent event) {
        cn = event.getMsg();
        SharedPreferences sp_chooce = getSharedPreferences("Citys",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp_chooce.edit();
        editor.putString("cityType", cn);
        editor.commit();

    }

    // 设置闹钟时间
    public void onEventMainThread(FiveEvent event) {
        String tt = event.getMsg().toString();
        if (tt != null) {
            texttime.setText(tt);
        }
    }

    // 设置使上午还是下午
    public void onEventMainThread(Event event) {
        String tt = event.getMsg().toString();
        if (tt != null) {
            textampm.setText(tt);
        }
    }

    public void onEventMainThread(SexEvent event) {
        clocktime = event.getMsg().toString();
    }

    public void onEventMainThread(LevelEvent event) {
        String tt = event.getMsg().toString();
        if (tt != null) {
            textOnoff1.setText(tt);
            SharedPreferences mySharedPreferences = getSharedPreferences(
                    "textOnoff1", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.putString("tt", tt);
            editor.clear();
            editor.commit();
        }
        if (textOnoff1.getText().equals("OFF")) {
            textOnoff1.setTextColor(android.graphics.Color.RED);
        } else {
            textOnoff1.setTextColor(android.graphics.Color.WHITE);
        }
    }

    public void onEventMainThread(ZeorEvent event) {
        String text = event.getMsg().toString();
        if (text != null) {
            textonoff2.setText(text);
            SharedPreferences mySharedPreference = getSharedPreferences(
                    "select", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = mySharedPreference.edit();
            editor.putString("text", text);
            editor.clear();
            editor.commit();
        }
        if (textonoff2.getText().equals("OFF")) {
            textonoff2.setTextColor(android.graphics.Color.RED);
        } else {
            textonoff2.setTextColor(android.graphics.Color.WHITE);
        }
    }

    @Override
    public void onCompleted() {
        mViewHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.hiflying_smartlinker_completed),
                        Toast.LENGTH_SHORT).show();
                mWaitingDialog.dismiss();
                mIsConncting = false;
            }
        });
    }

    // 采用异步请求的方式获取模块的IP及mac地址
    private class EsptouchAsyncTask2 extends
            AsyncTask<String, Void, IEsptouchResult> {

        private ProgressDialog mProgressDialog;

        private EsptouchTask mEsptouchTask;

        @Override
        protected void onPreExecute() {

            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setMessage(getString(R.string.alert_config_wait)); // 需要国际化
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (mEsptouchTask != null) {
                        mEsptouchTask.interrupt();
                    }
                }
            });
            mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    getString(R.string.alert_wait_),
                    new DialogInterface.OnClickListener() {// 需要国际化
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            mProgressDialog.show();
            mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setEnabled(false);
        }

        @Override
        protected IEsptouchResult doInBackground(String... params) {
            String apSsid1 = params[0];
            String apBssid1 = params[1];
            String apPassword1 = params[2];

            Log.e("params[0]", params[0]);
            Log.e("params[1]", params[1]);
            Log.e("params[2]", params[2]);
            boolean isSsidHidden = false;
            mEsptouchTask = new EsptouchTask(apSsid1, apBssid1, apPassword1,
                    isSsidHidden, MainActivity.this);
            IEsptouchResult result = mEsptouchTask.executeForResult();
            return result;
        }

        @Override
        protected void onPostExecute(IEsptouchResult result) {
            mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setEnabled(true);
            mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(
                    getString(R.string.alert_button_confirm));
            if (!result.isCancelled()) { // 是否被取消
                if (result.isSuc()) {
                    mProgressDialog.dismiss();
                    Toast.makeText(MainActivity.this,
                            getString(R.string.toast_connect_success),
                            Toast.LENGTH_SHORT).show();// 需要国际化
                    mac = result.getBssid();
                    // result.getInetAddress(); //获得IP地址
                    Constant.mlistIndoor.clear();
                    Constant.mlistOutdoor.clear();


                    // 请求成功 则将请求返回的数据保存起来
                    SharedPreferences macSP = getSharedPreferences("test",
                            Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = macSP.edit();
                    editor.putString("Adress", mac);

                    editor.commit();

                    SharedPreferences SPremberPassword = getSharedPreferences(
                            "remberPassword", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = SPremberPassword.edit();
                    editor1.putString(mac, mPasswordEditText.getText()
                            .toString());
                    editor1.commit();


                    tijiao();

                    // 设置wifi图标颜色为蓝色
                    iv_wifi.setImageResource(R.drawable.wifi_blue);

                } else {
                    SharedPreferences macSP = getSharedPreferences("test",
                            Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = macSP.edit();
                    // 请求失败则将数据清除掉
                    editor.clear();
                    editor.commit();
                    // 设置wifi图标为灰色
                    iv_wifi.setImageResource(R.drawable.wifi_graw);
                    mProgressDialog
                            .setMessage(getString(R.string.alert_message_config_fail));
                }
            }
        }
    }

    @Override
    public void onTimeOut() {
        mViewHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(),
                        getString(R.string.hiflying_smartlinker_timeout),
                        Toast.LENGTH_SHORT).show();

                mWaitingDialog.dismiss();
                mIsConncting = false;
            }
        });
    }

    private long exitTime = 0;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.toast_notify_exit),
                        Toast.LENGTH_SHORT).show(); // 需要国际化
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getSystemTime();
    }

    // 获得时间设置以及设置闹钟
    public void getSystemTime() {
        ContentResolver cv = this.getContentResolver();
        String strTimeFormat = android.provider.Settings.System.getString(cv,
                android.provider.Settings.System.TIME_12_24);
        // 通过SP文件获取存储的闹钟数据 闹钟里面的数据都保存为24小时制 根据系统的当前时间制 显示为12/24小时制
        SharedPreferences spAlarm = getSharedPreferences("alarm",
                Activity.MODE_PRIVATE);
        String alarm = spAlarm.getString("alarm", "12:00");
        String[] times = alarm.split(":");
        hour = Integer.parseInt(times[0]);
        minute = Integer.parseInt(times[1]);
        // 获得系统是12小时制还是24小时制
        // 并且设置相关值
        if ("24".equals(strTimeFormat)) { // 24小时制时
            is24 = true;
            timeampm = "01";
            textampm.setText("");
        } else { // 12小时制时
            is24 = false;
            timeampm = "00";
            if (hour < 12) {
                if (hour == 0) {
                    hour = 12;
                }
                textampm.setText("AM");
            } else {
                if (hour > 12) {
                    hour = hour - 12;
                }
                textampm.setText("PM");
            }
        }

        texttime.setText(String.format("%02d", hour) + ":"
                + String.format("%02d", minute));

    }

    private static int times = 0;
    private static boolean isShow = false;
    Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            boolean isSuccess = false;
            if (msg.what == 1) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this,
                        getString(R.string.toast_update_success), 2000).show();
                isSuccess = true;
                iv_wifi.setImageResource(R.drawable.wifi_blue);
                times = 0;

            } else if (msg.what == -1) {// 连接失败
                Log.e("进来了", "-1-1-1--1--1-1-1-1");
                dialog.dismiss();
                times = 0;
                System.out.println("提醒的是" + R.string.toast_update_faile);
                Toast.makeText(MainActivity.this,
                        getString(R.string.toast_update_faile), 2000).show();

            } else if (msg.what == -2) {
                System.out.println("进来了 -2-2-2-2-2-2-2-2-2"
                        + R.string.toast_network_anomaly);
                if (times < 6) {
                    tijiao();
                    // }else if(times >= 6){
                } else {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this,
                            getString(R.string.toast_network_anomaly), 2000)
                            .show();
                    times = 0;
                }
                times++;
            }
        }
    };

    // 提交数据
    public void tijiao() {
        String time = "";
        // 从本地sp文件中获取Address
        SharedPreferences macSP = getSharedPreferences("test",
                Activity.MODE_PRIVATE);
        name = macSP.getString("Adress", "");
        try {
            Message msg = new Message();
            msg.what = 0x345;
            str = texttime.getText().toString();
            // 如果是十二小时制 并且是下午则
            if ("00".equals(timeampm)
                    && "PM".equals(textampm.getText().toString())) {
                time = str.substring(0, str.indexOf(":"));
                int nHour;
                if (Integer.parseInt(time) == 12) {
                    nHour = Integer.parseInt(time);
                } else {
                    nHour = Integer.parseInt(time) + 12;
                }
                time = nHour + "";
            } else if ("00".equals(timeampm)
                    && "AM".equals(textampm.getText().toString())) {
                time = str.substring(0, str.indexOf(":"));
                int nHour = Integer.parseInt(time);
                if (nHour == 12) {
                    time = "00";
                }
            } else {
                time = str.substring(0, str.indexOf(":"));
                int nHour = Integer.parseInt(time);

            }
            // 获取小时
            String time2 = str.substring(3, 5);
            // 将字符串转换成时间格式
            clocktime = time + time2;
            String snoozeselect = (String) textonoff2.getText();
            // 从本地文件中获取闹钟的时间
            SharedPreferences preferences = getSharedPreferences(
                    "togglebuttonstatus", Context.MODE_PRIVATE);
            boolean alarm1 = preferences.getBoolean("alarm1", false);
            boolean alarm2 = preferences.getBoolean("alarm2", false);
            boolean isSnooze = preferences.getBoolean("isSnooze", false);
            if (alarm1) {
                swc = "01";
            } else if (alarm2) {
                swc = "02";
            } else {
                swc = "00";
            }

            if (snoozeselect.equals("ON")) {
                swl = "01";
            } else {
                swl = "00";
            }

            if (btnwendu.getText().equals("°C")) {
                wen = "00";
            } else {
                wen = "01";
            }

            SharedPreferences sp_chooce = getSharedPreferences("chooce",
                    Activity.MODE_PRIVATE);
            int selectedIndex = sp_chooce.getInt("chooce", 1);

            if (selectedIndex == 0) {
                seatime = "01";
            } else if (selectedIndex == 1) {
                seatime = "03";
            } else if (selectedIndex == 2) {
                seatime = "06";
            } else if (selectedIndex == 3) {
                seatime = "12";
            }

            // String searchtime = btnsearchtime.getText().toString();
            // if (searchtime.equals("3 Hour")) {
            // seatime = "03";
            // } else if (searchtime.equals("6 Hour")) {
            // seatime = "06";
            // } else if (searchtime.equals("12 Hour")) {
            // seatime = "12";
            // } else if (searchtime.equals("24 Hour")) {
            // seatime = "24";
            // }
            // 将获得的信息按一定格式组成字符串

            if ("00".equals(cn)) {
                SharedPreferences citys = getSharedPreferences("Citys",
                        Activity.MODE_PRIVATE);
                StringE = citys.getString("toService", "");

            }


            String str = (clocktime + timeampm + swc + swl + wen + StringE
                    + "**" + name + "**" + seatime + cn).trim();

            /**
             * 新添加的数据     dimmer  显示天气的天数
             */

            SharedPreferences display_days = getSharedPreferences(
                    "displauy_days", Activity.MODE_PRIVATE);

            String days = display_days.getString("days", "03");

            str = str + "##1021030713" + days + "##";


            // 发送信息给服务器
            System.out.println("发送给服务器的数据是" + str);
            mUwi = new UpdateWifiInfo(this,str, handler2, cn, StringE,sqliteOpenHelper);
            new Thread(mUwi).start();

            if (!isShow) {
                dialog = new DialogActivity(MainActivity.this);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
                Toast.makeText(MainActivity.this,
                        getString(R.string.toast_being_update), 500).show();
                isShow = true;
            }

            dialog.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface arg0) {
                    isShow = false;
                }
            });
        } catch (Exception e) {
        }
    }

    // 上传模块
    public void shangchuan() {
        String time;
//		SharedPreferences macSP = getSharedPreferences("test",
//				Activity.MODE_PRIVATE);
//		mac = macSP.getString("Adress", "");
        try {
            str = texttime.getText().toString();
            // 如果是十二小时制 并且是下午则
            if ("00".equals(timeampm)
                    && "PM".equals(textampm.getText().toString())) {
                time = str.substring(0, str.indexOf(":"));
                int nHour;
                if (Integer.parseInt(time) == 12) {
                    nHour = Integer.parseInt(time);
                } else {
                    nHour = Integer.parseInt(time) + 12;
                }
                time = nHour + "";
            } else if ("00".equals(timeampm)
                    && "AM".equals(textampm.getText().toString())) {
                time = str.substring(0, str.indexOf(":"));
                int nHour = Integer.parseInt(time);
                if (nHour == 12) {
                    time = "00";
                }
            } else {
                time = str.substring(0, str.indexOf(":"));
                int nHour = Integer.parseInt(time);
            }
            // 获取小时
            String time2 = str.substring(3, 5);
            clocktime = time + time2;

            // 获得两个开关的值
            String alarmselect = (String) textOnoff1.getText();
            String snoozeselect = (String) textonoff2.getText();

            // 从本地文件中获取闹钟的时间
            SharedPreferences preferences = getSharedPreferences(
                    "togglebuttonstatus", Context.MODE_PRIVATE);
            boolean alarm1 = preferences.getBoolean("alarm1", false);
            boolean alarm2 = preferences.getBoolean("alarm2", false);
            boolean isSnooze = preferences.getBoolean("isSnooze", false);
            if (alarm1) {
                swc = "01";
            } else if (alarm2) {
                swc = "02";
            } else {
                swc = "00";
            }
            if (snoozeselect.equals("ON")) {
                swl = "01";
            } else {
                swl = "00";
            }
            if (btnwendu.getText().equals("°C")) {
                wen = "00";
            } else {
                wen = "01";
            }
            // 选择更新时间的值

            SharedPreferences sp_chooce = getSharedPreferences("chooce",
                    Activity.MODE_PRIVATE);
            int selectedIndex = sp_chooce.getInt("chooce", 1);

            if (selectedIndex == 0) {
                seatime = "01";
            } else if (selectedIndex == 1) {
                seatime = "03";
            } else if (selectedIndex == 2) {
                seatime = "06";
            } else if (selectedIndex == 3) {
                seatime = "12";
            }

            // String searchtime = btnsearchtime.getText().toString();
            // if (searchtime.equals("3 Hour")) {
            // seatime = "03";
            // } else if (searchtime.equals("6 Hour")) {
            // seatime = "06";
            // } else if (searchtime.equals("12 Hour")) {
            // seatime = "12";
            // } else if (searchtime.equals("24 Hour")) {
            // seatime = "24";
            // }
            // 字符串按服务器格式拼接

            /**
             * clocktime 闹钟时间 timaampm 12/24小时制 swc 闹钟模式 swl 贪睡 wen 温度模式 StringE
             * 城市ID mac MAC码 seatime 自动更新时间 cn 国内还是国外
             */

            if ("00".equals(cn)) {
                SharedPreferences citys = getSharedPreferences("Citys",
                        Activity.MODE_PRIVATE);
                StringE = citys.getString("toService", "");
            }


            String str = (clocktime + timeampm + swc + swl + wen + StringE
                    + "**" + mac + "**" + seatime + cn);


            /**
             * 新添加的数据     dimmer  显示天气的天数
             */

            SharedPreferences display_days = getSharedPreferences(
                    "displauy_days", Activity.MODE_PRIVATE);

            String days = display_days.getString("days", "03");

            str = str + "##1021030713" + days + "##";

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // TODO Auto-generated method stub
                    super.handleMessage(msg);
                    if (msg.what == 1) {
                        Toast.makeText(MainActivity.this,
                                getString(R.string.toast_update_success), 2000)
                                .show(); // 需要国际化
                        iv_wifi.setImageResource(R.drawable.wifi_blue);
                    } else if (msg.what == -1) {
                        Toast.makeText(MainActivity.this,
                                getString(R.string.toast_update_faile), 2000)
                                .show(); // 需要国际化
                    } else if (msg.what == -2) {
                        if (times < 3) {
                            shangchuan();
                        } else if (times >= 3) {
                            Toast.makeText(MainActivity.this,
                                    getString(R.string.toast_network_anomaly),
                                    2000).show();
                            times = 0;
                        }
                        times++;
                    }
                }
            };
            // 发送信息给服务器
            UpdateWifiInfo uwi = new UpdateWifiInfo(this,str, handler, cn, StringE,sqliteOpenHelper);
            new Thread(uwi).start();
        } catch (Exception e) {


        }
    }

    private String getSSid() {
        // initGps();
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        if (wm != null) {
            WifiInfo wi = wm.getConnectionInfo();
            if (wi != null) {
                String ssid = wi.getSSID();
                apBssid = wi.getMacAddress();

                // 连上wifi之后看是否已经已经连上设备 若是则显示蓝色wifi标志即连通标志
                SharedPreferences macSP = getSharedPreferences("test",
                        Activity.MODE_PRIVATE);
                if (!macSP.getString("Adress", "").equals("")) {
                    iv_wifi.setImageResource(R.drawable.wifi_blue);
                }

                if (ssid.length() > 2 && ssid.startsWith("\"")
                        && ssid.endsWith("\"")) {
                    return ssid.substring(1, ssid.length() - 1);
                } else {
                    return ssid;
                }
            }
        }
        return "";
    }

    PopupWindow pop;
    private TextView tv_share;
    private TextView tv_temp_indoor;
    private TextView tv_temp_outdoor;
    private TextView feedback;

    // 创建并显示popupWindow
    public void showPopupWindow(View view) {

        View contentView = View.inflate(MainActivity.this,
                R.layout.popup_window, null);

        tv_share = (TextView) contentView.findViewById(R.id.tv_share);
        tv_temp_indoor = (TextView) contentView.findViewById(R.id.tv_temp_indoor);
        tv_temp_outdoor = (TextView) contentView.findViewById(R.id.tv_temp_outdoor);
        feedback = (TextView) contentView.findViewById(R.id.feedback);
        tv_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showShare();

                pop.dismiss();
            }
        });

        tv_temp_indoor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SharedPreferences macSP = getSharedPreferences("test",
                        Activity.MODE_PRIVATE);
                if (macSP.getString("Adress", "").equals("")) {
                    // mac地址为空的时候点击更新信息的按钮的提示
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            MainActivity.this); // 先得到构造器
                    builder.setMessage(getString(R.string.alert_message_connect_device)); // 设置内容
                    // 需要国际化
                    builder.setPositiveButton(getString(R.string.alert_ok),
                            new DialogInterface.OnClickListener() { // 设置确定按钮
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                } else {
                    if (isWifiConnected(context) || Network(context)) {
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, TempActivity.class);
                        intent.putExtra(IndoorTempView.KEY_ID, IndoorTempView.ID_FROM_INDOOR);
                        startActivity(intent);

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                MainActivity.this); // 先得到构造器
                        builder.setMessage(getString(R.string.alert_message_connect_network)); // 设置内容
                        builder.setPositiveButton(getString(R.string.alert_ok),
                                new DialogInterface.OnClickListener() { // 设置确定按钮
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();

                    }
                }

                pop.dismiss();

            }
        });


        tv_temp_outdoor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences macSP = getSharedPreferences("test",
                        Activity.MODE_PRIVATE);
                //这里为了结合UpdateWifiInfo一起来判断有没有室外温度模块，我也尝试过使用一个bollean变量来保存，可是
                //bollean变量在每次启动程序的时候会被初始化；--by chenhang
                SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("haveOut",1);
                db.insert("out",null,values);
                Cursor cursor = db.query("out",null,null,null,null,null,null);
                if (cursor.moveToFirst()){
                    haveOutMoudle = cursor.getInt(cursor.getColumnIndex("haveOut"));
                    Log.d("chenhang", haveOutMoudle + "");
                }
                if (macSP.getString("Adress", "").equals("")) {
                    // mac地址为空的时候点击更新信息的按钮的提示
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            MainActivity.this); // 先得到构造器
                    builder.setMessage(getString(R.string.alert_message_connect_device)); // 设置内容
                    // 需要国际化
                    builder.setPositiveButton(getString(R.string.alert_ok),
                            new DialogInterface.OnClickListener() { // 设置确定按钮
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                } else if (haveOutMoudle == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            MainActivity.this); // 先得到构造器
                    builder.setMessage(getString(R.string.alert_message_missing_model)); // 设置内容
                    // 需要国际化
                    builder.setPositiveButton(getString(R.string.alert_ok),
                            new DialogInterface.OnClickListener() { // 设置确定按钮
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                } else {
                    if (isWifiConnected(context) || Network(context)) {
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, TempActivity.class);
                        intent.putExtra(IndoorTempView.KEY_ID, IndoorTempView.ID_FROM_OUTDOOR);
                        startActivity(intent);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                MainActivity.this); // 先得到构造器
                        builder.setMessage(getString(R.string.alert_message_connect_network)); // 设置内容
                        builder.setPositiveButton(getString(R.string.alert_ok),
                                new DialogInterface.OnClickListener() { // 设置确定按钮
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();

                    }
                }

                db.close();
                pop.dismiss();
            }
        });

        feedback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent data = new Intent(Intent.ACTION_SENDTO);
                data.setData(Uri.parse("mailto:sales@moscase8.com"));
                data.putExtra(Intent.EXTRA_SUBJECT, "User feedback");
                data.putExtra(Intent.EXTRA_TEXT,
                        "Please enter your comments or Suggestions");
                startActivity(data);

                pop.dismiss();
            }
        });

        pop = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(contentView);
        // 设置点击视图外面 popupWindow消失
        pop.setBackgroundDrawable(new BitmapDrawable());
        // 设置触发的控件以及相对位置
        pop.showAsDropDown(iv_setting, 0, 0);
    }

    // 分享的具体设置
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();
        // getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("Wi-Fi Weather Share");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("https://app.moscase8.com/apps/8001D/Wi-FiWeather.apk");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("这个很好用，也推荐给你用用  下载请戳https://app.moscase8.com/apps/8001D/Wi-FiWeather.apk");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/share.png");// 确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("https://app.moscase8.com/apps/8001D/Wi-FiWeather.apk");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        // oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("https://app.moscase8.com/apps/8001D/Wi-FiWeather.apk");
        // 启动分享GUI
        oks.show(this);
    }

    // 查看手机联网情况，看是否有网络
    public static boolean Network(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null || !cm.getBackgroundDataSetting()) {
            return false;
        }
        return true;

    }

    // 查看手机wifi连接情况
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        } else {
            iv_wifi.setImageResource(R.drawable.wifi_graw);
        }
        return false;
    }

    /**
     * 讯飞语音听写的各项参数设置 参数设置
     *
     * @param param
     * @return
     */
    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = "mandarin";
        mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");

        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "" + 3000);

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "" + 1000);

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "" + 0);

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
                Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }

    // /*听写的监听事件
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            // showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            voiceDialog.dismiss();
            error.getPlainDescription(true);
            if ((System.currentTimeMillis() - starMillis) > 350) {
                showTip("请说话");
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            // manager.setMicrophoneMute(false);
            voiceDialog.dismiss();
            // Log.e(TAG, "结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());

            LogUtil.LOG("有结果");

            if (isLast) {
                isLastResult = true;
            }
            printResult(results);
            LogUtil.LOG("最后的进来qian");

        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            // LogUtil.LOG("当前音量是"+volume);
            // 返回音量大小
            if (volume >= 0 && volume < 1) {
                voiceDialog.setImg(R.drawable.voice1);
            } else if (volume >= 1 && volume < 4) {
                voiceDialog.setImg(R.drawable.voice2);
            } else if (volume >= 4 && volume < 7) {
                voiceDialog.setImg(R.drawable.voice3);
            } else if (volume >= 7 && volume < 10) {
                voiceDialog.setImg(R.drawable.voice4);
            } else if (volume >= 13 && volume < 16) {
                voiceDialog.setImg(R.drawable.voice5);
            } else if (volume >= 16) {
                voiceDialog.setImg(R.drawable.voice6);
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }

    };
    private AlertDialog.Builder voiceDialog2 = null;

    // 解析语音听写的结果并显示 主线程
    private void printResult(RecognizerResult results) {

        String text = JsonParser.parseIatResult(results.getResultString());
        SharedPreferences preferences = getSharedPreferences(
                "togglebuttonstatus", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        // String result = ParseTime.parse(resultBuffer.toString());

        final String result = ParseTime.pTime(resultBuffer.toString());
        final String OnOff = ParseTime.ParseOnOff(resultBuffer.toString());

        LogUtil.LOG("" + isLastResult);

        if (isLastResult) {

            voiceDialog2 = new AlertDialog.Builder(MainActivity.this);
            // voiceDialog2.setMessage(getDialogMessage(result, OnOff));
            TextView view = new TextView(context);
            view.setPadding(10, 30, 10, 0);
            view.setGravity(Gravity.CENTER);
            view.setTextSize(18);
            view.setText(getDialogMessage(result, OnOff));
            voiceDialog2.setView(view);
            voiceDialog2.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() { // 设置确定按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ShowResult(result, OnOff);

                            dialog.dismiss();
                        }
                    });

            voiceDialog2.create().show();
        }

        // if (!"ERROR".equals(result)) {
        // setAlarmTime(result);
        //
        // }
        //
        // if ("ALARM_ON".equals(OnOff)) {
        // textOnoff1.setText("ON");
        // textOnoff1.setTextColor(android.graphics.Color.WHITE);
        //
        // editor.putBoolean("alarm1",true);
        // editor.putBoolean("alarm2",false);
        // editor.commit();
        //
        // } else if ("ALARM_OFF".equals(OnOff)) {
        // textOnoff1.setTextColor(android.graphics.Color.RED);
        // textOnoff1.setText("OFF");
        //
        // //闹钟关了贪睡也需要跟着关闭
        // textonoff2.setTextColor(android.graphics.Color.RED);
        // textonoff2.setText("OFF");
        //
        // editor.putBoolean("alarm1",false);
        // editor.putBoolean("alarm2",false);
        // editor.putBoolean("isSnooze",false);
        // editor.commit();
        //
        // } else if ("SNOOZE_ON".equals(OnOff)) {
        // textonoff2.setText("ON");
        // textonoff2.setTextColor(android.graphics.Color.WHITE);
        //
        // textOnoff1.setText("ON");
        // textOnoff1.setTextColor(android.graphics.Color.WHITE);
        //
        // if(preferences.getBoolean("alarm2", false)){
        // editor.putBoolean("isSnooze",true);
        // editor.commit();
        //
        // }else{
        // editor.putBoolean("alarm1",true);
        // editor.putBoolean("alarm2",false);
        // editor.putBoolean("isSnooze",true);
        // editor.commit();
        // }
        //
        // } else if ("SNOOZE_OFF".equals(OnOff)) {
        // textonoff2.setTextColor(android.graphics.Color.RED);
        // textonoff2.setText("OFF");
        // editor.putBoolean("isSnooze",false);
        // editor.commit();
        //
        // } else if ("ALL_OFF".equals(OnOff)) {
        // textonoff2.setTextColor(android.graphics.Color.RED);
        // textonoff2.setText("OFF");
        // textOnoff1.setTextColor(android.graphics.Color.RED);
        // textOnoff1.setText("OFF");
        //
        // editor.putBoolean("alarm1",false);
        // editor.putBoolean("alarm2",false);
        // editor.putBoolean("isSnooze",false);
        // editor.commit();
        //
        // } else if ("ALL_ON".equals(OnOff)) {
        //
        // textOnoff1.setText("ON");
        // textOnoff1.setTextColor(android.graphics.Color.WHITE);
        // textonoff2.setText("ON");
        // textonoff2.setTextColor(android.graphics.Color.WHITE);
        //
        //
        //
        // editor.putBoolean("alarm1",true);
        // editor.putBoolean("alarm2",false);
        // editor.putBoolean("isSnooze",true);
        // editor.commit();
        //
        // }
        //
        // if ("ERROR".equals(OnOff)&&"ERROR".equals(result)) {
        // Toast.makeText(context, "请说出正确的时间", 0).show();
        // }

        // texttime.setText();
        // tv_result.setText(resultBuffer.toString());
    }

    private void showTip(String plainDescription) {
        Toast.makeText(MainActivity.this, plainDescription, 1500).show();
    }

    // 获得时间设置以及设置闹钟
    public void setAlarmTime(String alarm) {
        ContentResolver cv = this.getContentResolver();
        String strTimeFormat = android.provider.Settings.System.getString(cv,
                android.provider.Settings.System.TIME_12_24);
        // 通过SP文件获取存储的闹钟数据 闹钟里面的数据都保存为24小时制 根据系统的当前时间制 显示为12/24小时制

        String[] times = alarm.split(":");
        hour = Integer.parseInt(times[0]);
        minute = Integer.parseInt(times[1]);
        // 获得系统是12小时制还是24小时制
        // 并且设置相关值
        if ("24".equals(strTimeFormat)) { // 24小时制时
            is24 = true;
            timeampm = "01";
            textampm.setText("");
        } else { // 12小时制时
            is24 = false;
            timeampm = "00";
            if (hour < 12) {
                if (hour == 0) {
                    hour = 12;
                }
                textampm.setText("AM");
            } else {
                if (hour > 12) {
                    hour = hour - 12;
                }
                textampm.setText("PM");
            }
        }

        SharedPreferences spAlarm = getSharedPreferences("alarm",
                Activity.MODE_PRIVATE);
        Editor e = spAlarm.edit();
        e.putString("alarm", alarm);
        e.commit();

        texttime.setText(String.format("%02d", hour) + ":"
                + String.format("%02d", minute));

    }

    public String getDialogMessage(String time, String onOff) {
        String result = "";

        if (!"ERROR".equals(time)) {
            result = result + " " + time;
        }

        if ("ALARM_ON".equals(onOff)) {
            result = result + " " + "闹钟开";
        } else if ("ALARM_OFF".equals(onOff)) {
            result = result + " " + "闹钟关";
        } else if ("SNOOZE_ON".equals(onOff)) {
            result = result + " " + "贪睡开";
        } else if ("SNOOZE_OFF".equals(onOff)) {
            result = result + " " + "贪睡关";
        } else if ("ALL_OFF".equals(onOff)) {
            result = result + " " + "闹钟关" + " " + "贪睡关";
        } else if ("ALL_ON".equals(onOff)) {
            result = result + " " + "闹钟开" + " " + "贪睡开";
        }

        if ("ERROR".equals(onOff) & "ERROR".equals(time)) {
            result = "请说出正确时间";
        }

        return result;
    }

    public void ShowResult(String result, String OnOff) {

        SharedPreferences preferences = getSharedPreferences(
                "togglebuttonstatus", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if (!"ERROR".equals(result)) {
            setAlarmTime(result);

        }

        if ("ALARM_ON".equals(OnOff)) {
            textOnoff1.setText("ON");
            textOnoff1.setTextColor(android.graphics.Color.WHITE);

            if (!preferences.getBoolean("alarm1", false) && !preferences.getBoolean("alarm2", false)) {
                editor.putBoolean("alarm1", true);
                editor.putBoolean("alarm2", false);
                editor.commit();
            }
        } else if ("ALARM_OFF".equals(OnOff)) {
            textOnoff1.setTextColor(android.graphics.Color.RED);
            textOnoff1.setText("OFF");

            // 闹钟关了贪睡也需要跟着关闭
            textonoff2.setTextColor(android.graphics.Color.RED);
            textonoff2.setText("OFF");

            editor.putBoolean("alarm1", false);
            editor.putBoolean("alarm2", false);
            editor.putBoolean("isSnooze", false);
            editor.commit();

        } else if ("SNOOZE_ON".equals(OnOff)) {
            textonoff2.setText("ON");
            textonoff2.setTextColor(android.graphics.Color.WHITE);

            textOnoff1.setText("ON");
            textOnoff1.setTextColor(android.graphics.Color.WHITE);


            editor.putBoolean("isSnooze", true);
            if (!preferences.getBoolean("alarm1", false) && !preferences.getBoolean("alarm2", false)) {
                editor.putBoolean("alarm1", true);
                editor.putBoolean("alarm2", false);
            }
            editor.commit();

        } else if ("SNOOZE_OFF".equals(OnOff)) {
            textonoff2.setTextColor(android.graphics.Color.RED);
            textonoff2.setText("OFF");
            editor.putBoolean("isSnooze", false);
            editor.commit();

        } else if ("ALL_OFF".equals(OnOff)) {
            textonoff2.setTextColor(android.graphics.Color.RED);
            textonoff2.setText("OFF");
            textOnoff1.setTextColor(android.graphics.Color.RED);
            textOnoff1.setText("OFF");

            editor.putBoolean("alarm1", false);
            editor.putBoolean("alarm2", false);
            editor.putBoolean("isSnooze", false);
            editor.commit();

        } else if ("ALL_ON".equals(OnOff)) {

            textOnoff1.setText("ON");
            textOnoff1.setTextColor(android.graphics.Color.WHITE);
            textonoff2.setText("ON");
            textonoff2.setTextColor(android.graphics.Color.WHITE);

            if (!preferences.getBoolean("alarm1", false) && !preferences.getBoolean("alarm2", false)) {
                editor.putBoolean("alarm1", true);
                editor.putBoolean("alarm2", false);
            }
            editor.putBoolean("isSnooze", true);
            editor.commit();

        }

        // if ("ERROR".equals(OnOff) && "ERROR".equals(result)) {
        // Toast.makeText(context, "请说出正确的时间", 0).show();
        // }
    }

}
