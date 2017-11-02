package net.npaka.layoutex;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

//ライナーレイアウト(1)
public class LinearLayoutActivity extends Activity {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static int MP = ViewGroup.LayoutParams.MATCH_PARENT;

    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //レイアウトの生成
        LinearLayout layout = new LinearLayout(this);
        layout.setBackgroundColor(Color.WHITE);
        layout.setOrientation(LinearLayout.VERTICAL);//垂直
        layout.setGravity(Gravity.LEFT|Gravity.TOP);//左上寄せ
        setContentView(layout);

        //ボタンの生成
        int[] width = new int[]{MP, 300, 300, 300, 300, 300};
        for (int i = 0; i < 6; i++) {
            Button button = new Button(this);
            button.setText("("+i+")");
            button.setLayoutParams(
                new LinearLayout.LayoutParams(width[i], WC));
            layout.addView(button);
        }
    } 
}