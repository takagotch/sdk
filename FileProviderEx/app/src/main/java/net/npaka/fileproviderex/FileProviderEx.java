package net.npaka.fileproviderex;
import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

//ファイルを提供するコンテンツプロバイダの利用
public class FileProviderEx extends Activity implements
    View.OnClickListener {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private final static String TAG_READ = "read";
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
        editText.setLayoutParams(new LinearLayout.LayoutParams(MP, WC));
        layout.addView(editText);

        //ボタンの生成
        layout.addView(makeButton("コンテンツプロバイダ通信", TAG_READ));
    }   

    //ボタンの生成
    private Button makeButton(String text,String tag) {
        Button button = new Button(this);
        button.setText(text);
        button.setTag(tag);
        button.setOnClickListener(this);
        button.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        return button;
    }
    
    //ボタンクリック時に呼ばれる
    public void onClick(View v) {
        String tag = (String)v.getTag();
        if (TAG_READ.equals(tag)) {
            try {
                //コンテンツプロバイダが提供するファイルへのアクセス(1)
                InputStream in = getContentResolver().openInputStream(Uri.parse(
                    "content://net.npaka.fileprovider/test"));
                String str = new String(in2data(in));
                editText.setText(str, TextView.BufferType.EDITABLE);
            } catch (Exception e) {
                toast("読み込み失敗しました。");
            }            
        }
    }    

    //入力ストリーム→データ
    public static byte[] in2data(InputStream in) 
        throws Exception { 
        byte[] w = new byte[1024];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            while (true) { 
                int size = in.read(w);
                if (size <= 0) break;
                out.write(w, 0, size);
            };
            out.close();
            in.close();
            return out.toByteArray();
        } catch (Exception e) {
            try {
                if (in  != null) in.close();
                if (out != null) out.close();
            } catch (Exception e2) {
            }
            throw e;
        }
    }

    //トーストの表示
    private void toast(String text) {
        if (text == null) text = "";
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    } 
}