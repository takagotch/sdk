package net.npaka.layoutex;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

//テーブルレイアウト(3)
public class TableLayoutActivity extends Activity {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static int MP = ViewGroup.LayoutParams.MATCH_PARENT;

    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //レイアウトの生成
        TableLayout layout = new TableLayout(this);
        layout.setBackgroundColor(Color.WHITE);
        setContentView(layout);
        
        for (int j = 0; j < 5; j++) {
            //行の生成
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams(MP, WC));
            row.setGravity(Gravity.CENTER);//中央寄せ
            layout.addView(row);
            for (int i = 0; i < 4; i++) {
                //要素の追加
                Button button = new Button(this);
                button.setText("("+i+","+j+")");
                button.setLayoutParams(new TableRow.LayoutParams(WC, WC));
                row.addView(button);
            }
        }
    } 
}