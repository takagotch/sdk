package net.npaka.launchanimationex;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

//アクティビティ起動アニメーション
public class LaunchAnimationEx extends Activity implements
    View.OnClickListener {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static String TAG_DEFAULT   = "default";
    private final static String TAG_SCALEUP   = "scaleup";
    private final static String TAG_THUMBNAIL = "thumbnail";
    private final static String TAG_CUSTOM    = "custom";
    
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
        layout.addView(makeButton("デフォルト", TAG_DEFAULT));
        layout.addView(makeButton("スケールアップ", TAG_SCALEUP));
        layout.addView(makeButton("サムネイルスケールアップ", TAG_THUMBNAIL));
        layout.addView(makeButton("カスタム", TAG_CUSTOM));
    }
    
    //ボタンの生成
    private Button makeButton(String text, String tag) {
        Button button = new Button(this);
        button.setText(text);
        button.setTag(tag);
        button.setOnClickListener(this);
        button.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        return button;
    }
    
    //ボタンクリック時に呼ばれる
    public void onClick(View view) {
        String tag = (String)view.getTag();
        //デフォルトアニメーション
        if (TAG_DEFAULT.equals(tag)) {
            Intent intent = new Intent(this, MyActivity.class);
            startActivity(intent);
        }
        //スケールアップアニメーション(1)
        else if (TAG_SCALEUP.equals(tag)) {
            ActivityOptions opts = ActivityOptions.makeScaleUpAnimation(
                view, 0, 0, view.getWidth(), view.getHeight());
            startActivity(new Intent(this, MyActivity.class),
                opts.toBundle());
        } 
        //サムネイルスケールアップアニメーション(2)
        else if (TAG_THUMBNAIL.equals(tag)) {
            //ビューの描画キャッシュの取得(3)
            view.setDrawingCacheEnabled(true);
            view.setPressed(false);
            view.refreshDrawableState();
            Bitmap bmp = view.getDrawingCache();

            //サムネイルスケールアップアニメーションによるアクティビティ起動(2)
            ActivityOptions opts = ActivityOptions.makeThumbnailScaleUpAnimation(
                view, bmp, 0, 0);
            startActivity(new Intent(this, MyActivity.class),
                opts.toBundle());

            //キャッシュ無効化(3)
            view.setDrawingCacheEnabled(false);            
        } 
        //カスタムアニメーション(4)
        else if (TAG_CUSTOM.equals(tag)) {
            ActivityOptions opts = ActivityOptions.makeCustomAnimation(
                this, R.anim.zoom_enter, R.anim.zoom_exit);
            startActivity(new Intent(this, MyActivity.class),
                opts.toBundle());
        }
    }
}