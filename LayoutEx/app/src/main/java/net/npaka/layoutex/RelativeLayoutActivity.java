package net.npaka.layoutex;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

//リレイティブレイアウト(2)
public class RelativeLayoutActivity extends Activity {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //レイアウトの生成
        RelativeLayout layout = new RelativeLayout(this);
        layout.setBackgroundColor(Color.WHITE);
        setContentView(layout);

        //ボタン0の生成
        Button button0 = new Button(this);
        button0.setText("左上から(10,10)");
        RelativeLayout.LayoutParams params0;
        params0 = new RelativeLayout.LayoutParams(WC, WC);
        params0.setMargins(10, 10, 0, 0);
        button0.setLayoutParams(params0);
        layout.addView(button0);

        //ボタン1の生成
        Button button1 = new Button(this);
        button1.setText("右上から(10,10)");
        RelativeLayout.LayoutParams params1;
        params1 = new RelativeLayout.LayoutParams(WC, WC);
        params1.setMargins(0, 10, 10, 0);
        params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        button1.setLayoutParams(params1);
        layout.addView(button1);

        //ボタン2の生成
        Button button2 = new Button(this);
        button2.setText("左下から(10,10)");
        RelativeLayout.LayoutParams params2;
        params2 = new RelativeLayout.LayoutParams(WC, WC);
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        button2.setLayoutParams(params2);
        layout.addView(button2);
        
        //ボタン3の生成
        Button button3 = new Button(this);
        button3.setText("右下から(10,10)");
        RelativeLayout.LayoutParams params3;
        params3 = new RelativeLayout.LayoutParams(WC, WC);
        params3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        button3.setLayoutParams(params3);
        layout.addView(button3);
    }
}