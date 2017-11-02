package net.npaka.socketex;
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
import android.widget.TextView;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

//ソケット通信
public class SocketEx extends Activity    
    implements View.OnClickListener {
    private final static String BR = System.getProperty("line.separator");
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    
    //IPアドレスの指定(1)
    private final static String IP="192.168.100.6";//★変更必須
    
    private TextView lblReceive;//受信ラベル
    private EditText edtSend;   //送信エディットテキスト
    private Button   btnSend;   //送信ボタン
    
    private Socket       socket; //ソケット
    private InputStream  in;     //入力ストリーム
    private OutputStream out;    //出力ストリーム
    private boolean      error;  //エラー
    
    private final Handler handler = new Handler();//ハンドラ
    
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
        
        //送信エディットテキストの生成
        edtSend = new EditText(this);
        edtSend.setText("");
        edtSend.setLayoutParams(new LinearLayout.LayoutParams(MP, WC));
        layout.addView(edtSend);
        
        //送信ボタンの生成
        btnSend = new Button(this);
        btnSend.setText("送信");
        btnSend.setOnClickListener(this);
        btnSend.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        layout.addView(btnSend);

        //受信ラベルの生成
        lblReceive = new TextView(this);
        lblReceive.setText("");
        lblReceive.setTextSize(16.0f);
        lblReceive.setTextColor(Color.BLACK);
        lblReceive.setLayoutParams(new LinearLayout.LayoutParams(MP, WC));
        layout.addView(lblReceive);        
    }

    //アクティビティ開始時に呼ばれる
    @Override
    public void onStart() {
        super.onStart();
        
        //スレッドの生成
        Thread thread = new Thread(){
            public void run() {
                try {
                    connect(IP, 8081);
                } catch (Exception e) {
                }
            }
        };
        thread.start();
    }
    
    //アクティビティの停止時に呼ばれる
    @Override
    public void onStop() {
        super.onStop();
        disconnect();
    }
    
    //受信テキストの追加
    private void addText(final String text) {
        //ハンドラの生成
        handler.post(new Runnable(){
            public void run() {
                lblReceive.setText(text+BR+
                    lblReceive.getText());
            }
        });
    }
    
    //接続
    private void connect(String ip, int port) {
        int size;
        String str;
        byte[] w = new byte[1024];
        try {
            //ソケット接続(2)
            addText("接続中");
            socket = new Socket(ip, port);
            in  = socket.getInputStream();
            out = socket.getOutputStream();
            addText("接続完了");
            
            //受信ループ(3)
            while (socket != null && socket.isConnected()) {
                //データの受信(4)
                size = in.read(w);
                if (size <= 0) continue;
                str = new String(w, 0, size, "UTF-8");

                //ラベルへの文字列追加
                addText(str);
            }
        } catch (Exception e) {
            addText("通信失敗しました");
        }
    }
    
    //切断
    private void disconnect() {
        try {
            socket.close();
            socket = null;
        } catch (Exception e) {
        }
    }

    //ボタンクリックイベントの処理
    public void onClick(View v) {
        //スレッッドの生成
        Thread thread = new Thread(new Runnable() {public void run() {
            error = false;
            try {
                //データの送信(5)
                if (socket != null && socket.isConnected()) {
                    byte[] w = edtSend.getText().toString().getBytes("UTF8");
                    out.write(w);
                    out.flush();
                }
            } catch (Exception e) {
                error = true;
            }
            //ハンドラの生成
            handler.post(new Runnable() {public void run() {
                if (!error) {
                    edtSend.setText("");
                } else {
                    addText("通信失敗しました");
                }
            }});
        }});
        thread.start();
    }
}