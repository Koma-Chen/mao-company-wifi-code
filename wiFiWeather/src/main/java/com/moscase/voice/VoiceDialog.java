package com.moscase.voice;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.wwr.clock.R;

import java.util.Locale;

public class VoiceDialog extends Dialog {
    private TextView textView;
    private ImageView voice;

    public VoiceDialog(Context context) {
        super(context, R.style.MyDialogStyle2);
    }

    public VoiceDialog(Context context, int theme) {
        super(context, theme);
    }


    protected void onCreate(Bundle saveInstanceState) {

        super.onCreate(saveInstanceState);

        setContentView(R.layout.dialog_voice);
        textView = (TextView) findViewById(R.id.tv);
        voice = (ImageView) this.findViewById(R.id.iv_voice);
        String locale = Locale.getDefault().getLanguage();
        if (locale.equals("zh")) {

        } else {
            textView.setText("The default does not speak automatically shut down after 3 " +
                    "seconds,the interval dose not speak off after 2 seconds,up to 6 seconds");
        }
    }


    public void setText(String string) {
        if (this.isShowing()) {
            textView.setText(string);
        }
    }

    public void setImg(int id) {
        voice.setImageResource(id);
    }

}
