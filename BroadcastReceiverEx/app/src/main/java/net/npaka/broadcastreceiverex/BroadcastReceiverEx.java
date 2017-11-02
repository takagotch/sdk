package net.npaka.broadcastreceiverex;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

//ブロードキャストレシーバー
public class BroadcastReceiverEx extends Activity    
    implements View.OnClickListener {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
        
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
        
        //ボタンの生成
        Button button = new Button(this);
        button.setText("インテントのブロードキャスト");
        button.setOnClickListener(this);
        button.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        layout.addView(button);
    } 

    //ボタンクリックイベントの処理
    public void onClick(View v) {
        //インテントのブロードキャスト(1)
        Intent intent = new Intent();
        intent.setAction("net.npaka.broadcastreceiverex.VIEW");
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("TEXT", "ブロードキャストレシーバーのテストです");
        sendBroadcast(intent);
    } 
}