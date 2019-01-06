package com.wwr.clock;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Locale;

public class LanguageActivity extends Activity {

    private RadioGroup mRadioGroup;

    private RadioButton mChineseButton;
    private RadioButton mEnglishButton;
    private RadioButton mGermanButton;
    private RadioButton mFrenchButton;
    private RadioButton mRussianButton;
    private RadioButton mSpanishButton;
    private RadioButton mItalianButton;
    private RadioButton mPortugueseButton;
    private RadioButton mDutchButton;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_language);
        mRadioGroup = findViewById(R.id.radiogroup);
        mSharedPreferences = getSharedPreferences("language", MODE_PRIVATE);


        mChineseButton = findViewById(R.id.chinese);
        mEnglishButton = findViewById(R.id.english);
        mGermanButton = findViewById(R.id.german);
        mFrenchButton = findViewById(R.id.franch);
        mRussianButton = findViewById(R.id.russian);
        mSpanishButton = findViewById(R.id.spanish);
        mItalianButton = findViewById(R.id.italian);
        mPortugueseButton = findViewById(R.id.portuguese);
        mDutchButton = findViewById(R.id.dutch);

        Locale locale = getResources().getConfiguration().locale;
        String language = mSharedPreferences.getString("language", locale.getLanguage());
        if (language.equals("zh"))
            mChineseButton.setChecked(true);
        else if (language.equals("en"))
            mEnglishButton.setChecked(true);
        else if (language.equals("de"))
            mGermanButton.setChecked(true);
        else if (language.equals("fr"))
            mFrenchButton.setChecked(true);
        else if (language.equals("ru"))
            mRussianButton.setChecked(true);
        else if (language.equals("es"))
            mSpanishButton.setChecked(true);
        else if (language.equals("it"))
            mItalianButton.setChecked(true);
        else if (language.equals("pt"))
            mSpanishButton.setChecked(true);
        else if (language.equals("nl"))
            mDutchButton.setChecked(true);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == mFrenchButton.getId()) {
                    mSharedPreferences.edit().putString("language", "fr").commit();
                    changeLanguage(Locale.FRENCH);
                } else if (i == mEnglishButton.getId()) {
                    mSharedPreferences.edit().putString("language", "us").commit();
                    changeLanguage(Locale.ENGLISH);
                } else if (i == mChineseButton.getId()) {
                    mSharedPreferences.edit().putString("language", "zh").commit();
                    changeLanguage(Locale.CHINA);
                } else if (i == mGermanButton.getId()) {
                    mSharedPreferences.edit().putString("language", "de").commit();
                    changeLanguage(Locale.GERMAN);
                } else if (i == mRussianButton.getId()) {
                    mSharedPreferences.edit().putString("language", "ru").commit();
                    changeLanguage(new Locale("ru"));
                } else if (i == mSpanishButton.getId()) {
                    mSharedPreferences.edit().putString("language", "es").commit();
                    changeLanguage(new Locale("es"));
                } else if (i == mItalianButton.getId()) {
                    mSharedPreferences.edit().putString("language", "it").commit();
                    changeLanguage(Locale.ITALY);
                } else if (i == mPortugueseButton.getId()) {
                    mSharedPreferences.edit().putString("language", "pt").commit();
                    changeLanguage(new Locale("pt"));
                } else if (i == mDutchButton.getId()) {
                    mSharedPreferences.edit().putString("language", "nl").commit();
                    changeLanguage(new Locale("nl"));
                }

            }
        });
    }

    private void changeLanguage(Locale local) {

        Resources resources = getResources();// 获得res资源对象
        Configuration config = resources.getConfiguration();// 获得设置对象
        DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
        config.locale = local;
        resources.updateConfiguration(config, dm);
        finish();////如果不重启当前界面，是不会立马修改的
        Intent intent = new Intent(LanguageActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
