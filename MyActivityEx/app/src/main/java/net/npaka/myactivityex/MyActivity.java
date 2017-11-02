package net.npaka.myactivityex;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

//自作アクティビティ
public class MyActivity extends Activity 
    implements View.OnClickListener {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private EditText editText;//エディットテキスト
    
    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //戻り値の指定(6)
        setResult(Activity.RESULT_CANCELED);

        //インテントからのパラメータ取得(5)
        String text = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) text = extras.getString("text");

        //レイアウトの生成
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);
        
        //OKボタンの生成
        Button button = new Button(this);
        button.setText("OK");
        button.setOnClickListener(this);
        button.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        layout.addView(button);

        //エディットテキストの生成
        editText = new EditText(this);
        editText.setText(text);
        editText.setLayoutParams(new LinearLayout.LayoutParams(MP, WC));
        layout.addView(editText);    
    }

    //ボタンクリック時に呼ばれる
    public void onClick(View v) {
        //戻り値の指定(6)
        Intent intent = new Intent();
        intent.putExtra("text", editText.getText().toString());
        setResult(Activity.RESULT_OK, intent);
            
        //アクティビティの終了(7)
        finish();
    }
}