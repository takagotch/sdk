package net.npaka.texttospeechex;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Locale;

//音声合成
public class TextToSpeechEx extends Activity 
    implements View.OnClickListener, TextToSpeech.OnInitListener {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private EditText     editText;//エディットテキスト
    private TextToSpeech tts;     //TTS
    
    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //レイアウトの生成
        LinearLayout layout = new LinearLayout(this);
        layout.setBackgroundColor(Color.WHITE);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);       
        
        //エディットテキストの生成
        editText = new EditText(this);
        editText.setText("これはテストです。");
        editText.setLayoutParams(new LinearLayout.LayoutParams(MP, WC));
        layout.addView(editText);
        
        //ボタンの生成
        layout.addView(makeButton("音声合成", "speech"));
        
        //音声合成の準備(1)
        tts = new TextToSpeech(this, this);
    }
    
    //ボタンの生成
    private Button makeButton(String text, String tag) {
        Button button = new Button(this);
        button.setText(text);
        button.setTag(tag);
        button.setOnClickListener(this);
        button.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        return button;
    }      

    //音声合成の準備完了時に呼ばれる(1)
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            //音声合成のロケール指定(2)
            Locale locale = Locale.JAPANESE;
            if (tts.isLanguageAvailable(locale) >=
                TextToSpeech.LANG_AVAILABLE) {
                tts.setLanguage(locale);
            } else {
                toast("Unsupported Language");
            }
        } else if (status == TextToSpeech.ERROR) {
            toast("TextToSpeech.ERROR");
        }
    }

    //アクティビティ破棄時に呼ばれる
    protected void onDestroy() {
        super.onDestroy();
        
        //音声合成の終了(4)
        if (tts != null) tts.shutdown();
    }
    
    //ボタンクリック時に呼ばれる(3)
    public void onClick(View v) {
        String str = editText.getText().toString();
        if (str.length() == 0) return;
        //スピーチの停止
        if (tts.isSpeaking()) tts.stop();
            
        //スピーチの再生
        tts.setSpeechRate(1.0f);
        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
    }    
    
    //トーストの表示
    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}