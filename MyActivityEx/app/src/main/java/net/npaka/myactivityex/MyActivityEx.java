package net.npaka.myactivityex;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

//アクティビティのパラメータ渡し
public class MyActivityEx extends Activity 
    implements View.OnClickListener {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static int REQUEST_TEXT = 0;//テキストID
    private TextView textView;//テキストビュー
        
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
        button.setText("自作アクティビティの起動");
        button.setOnClickListener(this);   
        button.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        layout.addView(button);        
        
        //テキストビューの生成
        textView = new TextView(this);
        textView.setText("");
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(16);
        textView.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        layout.addView(textView);
    }

    //ボタンクリック時に呼ばれる
    public void onClick(View v) {
        //アプリ内のアクティビティを呼び出すインテントの生成(1)
        Intent intent = new Intent(this, MyActivity.class);
            
        //インテントへのパラメータ指定(2)
        intent.putExtra("text", textView.getText().toString());
            
        //アクティビティの呼び出し(3)
        startActivityForResult(intent, REQUEST_TEXT);
    } 
    
    //アクティビティ呼び出し結果の取得(4)
    @Override
    protected void onActivityResult(int requestCode,
        int resultCode, Intent intent) {
        if (requestCode == REQUEST_TEXT && resultCode == RESULT_OK) {
            //インテントからのパラメータ取得
            String text = "";
            Bundle extras = intent.getExtras();
            if (extras != null) text = extras.getString("text");
            
            //テキストビューへの指定
            textView.setText(text);
        }
    }    
}