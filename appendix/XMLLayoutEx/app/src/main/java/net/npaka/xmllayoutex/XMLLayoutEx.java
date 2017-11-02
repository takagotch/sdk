package net.npaka.xmllayoutex;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

//XMLによるレイアウト作成
public class XMLLayoutEx extends Activity 
    implements View.OnClickListener {
    private Button      button;     //ボタン
    private ImageButton imageButton;//イメージボタン

    //アクティビティ起動時に呼ばれる
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //レイアウトの指定(1)
        setContentView(R.layout.sample);       
        
        //コンポーネントの関連づけ(2)
        button = (Button)this.findViewById(R.id.button);
        button.setOnClickListener(this);
        imageButton = (ImageButton)this.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(this);
    }   
    
    //ボタンクリック時に呼ばれる
    public void onClick(View view) {
        //ボタンを押した時の処理
        if (view == button) {
            toast("ボタンを押した");
        } 
        //イメージボタンを押した時の処理
        else if (view == imageButton) {
            toast("イメージボタンを押した");
        }
    }
    
    //トーストの表示
    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    } 
}