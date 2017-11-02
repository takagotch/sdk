package net.npaka.layoutex;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;

//グリッドレイアウト(4)
public class GridLayoutActivity extends Activity {
    private final static int MP = ViewGroup.LayoutParams.MATCH_PARENT;

    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //レイアウトの生成
        GridLayout layout = new GridLayout(this);
        layout.setBackgroundColor(Color.WHITE);
        layout.setColumnCount(4);
        layout.setRowCount(5);
        setContentView(layout);

        for (int j = 0; j < 5; j++) {
            for (int i = 1; i < 4; i++) {
                //要素の追加
                Button button = new Button(this);
                button.setText("("+i+","+j+")");
                GridLayout.Spec colSpec = GridLayout.spec(i);
                GridLayout.Spec rowSpec = GridLayout.spec(j);
                GridLayout.LayoutParams params =
                    new GridLayout.LayoutParams(rowSpec, colSpec);
                button.setLayoutParams(params);
                layout.addView(button);
            }
        }

        //要素の追加
        Button button = new Button(this);
        button.setText("(0,0)");
        GridLayout.Spec colSpec = GridLayout.spec(0);
        GridLayout.Spec rowSpec = GridLayout.spec(0, 4);
        GridLayout.LayoutParams params =
            new GridLayout.LayoutParams(rowSpec, colSpec);
        button.setLayoutParams(params);
        params.height = MP;
        layout.addView(button);
    }
}