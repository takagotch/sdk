package net.npaka.viewanimationex;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

//ビューアニメーション
public class ViewAnimationEx extends Activity implements
    View.OnClickListener {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static String TAG_TRANSLATE = "translate";
    private final static String TAG_SCALE     = "scale";
    private final static String TAG_ROTATE    = "rotate";
    private final static String TAG_ALPHA     = "alpha";
    private final static String TAG_SET       = "set";
    private ImageView imageView;
    
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
        
        //イメージビューの生成
        imageView = new ImageView(this);
        imageView.setImageBitmap(res2bmp(this, R.drawable.sample));
        imageView.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        layout.addView(imageView);
        
        //ボタンの生成
        layout.addView(makeButton("平行移動", TAG_TRANSLATE));
        layout.addView(makeButton("拡大縮小", TAG_SCALE));
        layout.addView(makeButton("回転", TAG_ROTATE));
        layout.addView(makeButton("透過率", TAG_ALPHA));
        layout.addView(makeButton("アニメーションセット", TAG_SET));
    }
    
    //リソース→ビットマップ
    public static Bitmap res2bmp(Context context, int resID) {
        return BitmapFactory.decodeResource(
            context.getResources(), resID);
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
        //移動アニメーション(1)
        if (TAG_TRANSLATE.equals(tag)) {
            TranslateAnimation trans = new TranslateAnimation(0, 320, 0, 0);
            trans.setDuration(500);//(2)
            trans.setInterpolator(new OvershootInterpolator());//(2)
            imageView.startAnimation(trans);//(3)
        }
        //拡大縮小アニメーション(4)
        else if (TAG_SCALE.equals(tag)) {
            ScaleAnimation scale = new ScaleAnimation(1, 2, 1, 2, 120, 120);
            scale.setDuration(500);
            scale.setInterpolator(new AnticipateOvershootInterpolator());
            imageView.startAnimation(scale);
        }
        //回転アニメーション(5)
        else if (TAG_ROTATE.equals(tag)) {
            RotateAnimation rotate = new RotateAnimation(0, 360, 120, 120);
            rotate.setDuration(500);
            rotate.setInterpolator(new LinearInterpolator());
            imageView.startAnimation(rotate);
        }
        //透過率アニメーション(6)
        else if (TAG_ALPHA.equals(tag)) {
            AlphaAnimation alpha = new AlphaAnimation(1, 0);
            alpha.setDuration(500);
            alpha.setInterpolator(new LinearInterpolator());
            imageView.startAnimation(alpha);
        }
        //アニメーションセット(7)
        else if (TAG_SET.equals(tag)) {
            AnimationSet set = new AnimationSet(true);
            set.setInterpolator(new AnticipateOvershootInterpolator());

            //拡大縮小
            ScaleAnimation scale = new ScaleAnimation(1, 2, 1, 2, 120, 120);
            scale.setDuration(500);
            set.addAnimation(scale);
            
            //移動
            TranslateAnimation trans = new TranslateAnimation(0, 320, 0, 0);
            trans.setDuration(500);
            set.addAnimation(trans);

            imageView.startAnimation(set);
        }   
    }
}