package net.npaka.fcmex;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

//Firebase Cloud Messaging
public class FCMEx extends Activity {
    //UI
    private TextView textView;

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

        //テキストビューの生成
        textView = new TextView(this);
        textView.setTextColor(Color.BLACK);
        textView.setText("FCMEx>");
        layout.addView(textView);
    }
}