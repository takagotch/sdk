package net.npaka.httpex;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

//HTTP通信
public class HttpEx extends Activity   
    implements View.OnClickListener {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private final static String TAG_READ = "read";
    private EditText editText;
    private String   text;
    private Handler  handler = new Handler();
    
    //テキストファイルのURLの指定(1)
    private final static String URL =
        "http://npaka.net/android/test.txt";
        
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
        layout.addView(makeButton("HTTP通信", TAG_READ));
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
        String tag = (String)v.getTag();
        if (TAG_READ.equals(tag)) {
            //スレッドの生成(4)
            Thread thread = new Thread(new Runnable() {public void run() {
                //HTTP通信
                try {
                    text = new String(http2data(URL));
                } catch (Exception e) {
                    text = null;
                }
                //ハンドラの生成(5)
                handler.post(new Runnable() {public void run() {
                    if (text != null) {
                        editText.setText(text);
                    } else {
                        editText.setText("読み込み失敗しました。");
                    }
                }});
            }});
            thread.start();
        }
    }     
    
    //HTTP通信
    public static byte[] http2data(String path) throws Exception {
        byte[] w=new byte[1024]; 
        HttpURLConnection c = null;
        InputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            //HTTP接続のオープン(2)
            URL url = new URL(path);
            c = (HttpURLConnection)url.openConnection();
            c.setRequestMethod("GET");
            c.connect();
            in = c.getInputStream();
            
            //バイト配列の読み込み
            out = new ByteArrayOutputStream();
            while (true) {
                int size = in.read(w);
                if (size <= 0) break;
                out.write(w, 0, size);
            }
            out.close();

            //HTTP接続のクローズ(3)
            in.close();
            c.disconnect();
            return out.toByteArray();
        } catch (Exception e) {
            try {
                if (c != null) c.disconnect();
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (Exception e2) {
            }
            throw e;
        }
    }     
}