package net.npaka.recognizespeechex;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

//音声認識
public class RecognizeSpeechEx extends Activity 
    implements View.OnClickListener {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int REQUEST_CODE = 0;
    private EditText editText;
    
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
        editText.setText("");
        editText.setLayoutParams(new LinearLayout.LayoutParams(MP, WC));
        layout.addView(editText);
        
        //ボタンの生成
        layout.addView(makeButton("音声認識", "recog"));
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

    //ボタンクリック時に呼ばれる
    public void onClick(View v) {
        try {
            //音声認識の実行(1)
            Intent intent = new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "RecognizeSpeechEx");
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            toast(e.getMessage());
        }        
    }    
    
    //アクティビティ終了時に呼ばれる
    protected void onActivityResult(int requestCode,
        int resultCode, Intent data) {
        //音声認識結果の取得(2)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String str = "";
            ArrayList<String> results =
                data.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS);
            for (int i = 0; i < results.size(); i++) {
                str += results.get(i)+" ";
            }
            editText.setText(str);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    //トーストの表示
    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}