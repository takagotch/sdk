package net.npaka.edittextex;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

//エディットテキスト
public class EditTextEx extends Activity 
    implements View.OnClickListener {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private EditText editText;//エディットテキスト
    private Button   button;  //ボタン
    
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
        
        //エディットテキストの生成(1)
        editText = new EditText(this);
        editText.setTextColor(Color.BLACK);
        editText.setText("これはテストです", EditText.BufferType.NORMAL);
        editText.setLayoutParams(new LinearLayout.LayoutParams(MP, WC));
        layout.addView(editText);
        
        //ボタンの生成
        button = new Button(this);
        button.setText("結果表示");
        button.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        button.setOnClickListener(this);
        layout.addView(button);
    }

    //ボタンクリック時に呼ばれる
    public void onClick(View v) {
        toast("エディット>"+editText.getText().toString());
    }    
    
    //トーストの表示
    private void toast(String text) {
        if (text == null) text = "";
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }   
}