package net.npaka.textviewex;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;

//テキストビューとイメージビュー
public class TextViewEx extends Activity {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    
    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //レイアウトの生成(1)
        LinearLayout layout = new LinearLayout(this);
        layout.setBackgroundColor(Color.WHITE);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout); 

        //テキストビューの生成(2)
        TextView textView = new TextView(this);
        textView.setText("これはテストです");
        textView.setTextSize(16.0f);
        textView.setTextColor(Color.rgb(0, 0, 0));
        
        //コンポーネントのサイズの指定(3)
        textView.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        
        //レイアウトへのコンポーネントの追加(4)
        layout.addView(textView);

        //画像の読み込み
        Bitmap bitmap = BitmapFactory.decodeResource(
            getResources(), R.drawable.sample);

        //イメージビューの生成(5)
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        layout.addView(imageView);
    }  
}