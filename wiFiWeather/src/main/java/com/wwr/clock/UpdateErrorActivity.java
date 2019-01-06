package com.wwr.clock;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

public class UpdateErrorActivity extends Activity {

    WebView mWebView;
    ImageView mImageView;
    ImageView mBack;
    boolean isEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_error);


        mWebView = (WebView) findViewById(R.id.webview);
        mBack = findViewById(R.id.imgage);
        mWebView.loadUrl("file:///android_asset/testEN.html");
        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

//
        mWebView.addJavascriptInterface(new AndroidtoJs(), "Android");

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setLoadWithOverviewMode(true);


        mImageView = (ImageView) findViewById(R.id.change);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEN){
                    mWebView.loadUrl("file:///android_asset/test.html");
                    isEN = true;
                } else{
                    mWebView.loadUrl("file:///android_asset/testEN.html");
                    isEN = false;
                }

            }
        });


        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public class AndroidtoJs {

        // 定义JS需要调用的方法
        // 被JS调用的方法必须加入@JavascriptInterface注解
        @JavascriptInterface
        public void toFinish() {
//            System.out.println("JS调用了Android的hello方法");
            finish();
        }
    }

}
