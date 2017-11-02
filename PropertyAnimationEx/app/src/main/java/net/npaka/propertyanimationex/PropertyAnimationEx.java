package net.npaka.propertyanimationex;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

//プロパティアニメーション
public class PropertyAnimationEx extends Activity implements
    View.OnClickListener {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static String TAG_OBJECT_ANIMATOR = "object_animator";
    private final static String TAG_VALUE_ANIMATOR  = "value_animator";
    private final static String TAG_ANIMATOR_SET    = "animator_set";
    
    //イメージビュー
    private ImageView imageView;
    private Animator  animator;
    
    //バリューアニメータの変数
    private float fromX;    //移動元X座標
    private float fromY;    //移動元Y座標
    private float fromScale;//移動元スケール
    private float toX;      //移動先X座標
    private float toY;      //移動先Y座標
    private float toScale;  //移動先スケール
    
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
        layout.addView(makeButton("オブジェクトアニメータ", TAG_OBJECT_ANIMATOR));
        layout.addView(makeButton("バリューアニメータ", TAG_VALUE_ANIMATOR));
        layout.addView(makeButton("アニメータセット", TAG_ANIMATOR_SET));
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
        //アニメーションのキャンセル(3)
        if (animator != null) {
            animator.cancel();
            imageView.setTranslationX(0);
            imageView.setTranslationY(0);
            imageView.setScaleX(1);
            imageView.setScaleY(1);
            imageView.setRotation(0);
            animator = null;
        }
        String tag = (String)view.getTag();
        
        //オブジェクトアニメータ
        if (TAG_OBJECT_ANIMATOR.equals(tag)) {
            //オブジェクトアニメータの生成(1)
            ObjectAnimator rotationAnimator =
                ObjectAnimator.ofFloat(imageView, "rotation", 0.0f, 360.0f);
            rotationAnimator.setDuration(500);
            
            //アニメーションの開始(2)
            rotationAnimator.start();
            animator=rotationAnimator;
        }
        //バリューアニメータ
        else if (TAG_VALUE_ANIMATOR.equals(tag)) {
            //移動元
            fromX     = imageView.getTranslationX();
            fromY     = imageView.getTranslationY();
            fromScale = imageView.getScaleX();
            
            //移動先
            toX     = fromX+20;
            toY     = fromY;
            toScale = 0.98f;
            
            //バリューアニメータの生成(4)
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
            valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            valueAnimator.setDuration(300);
            
            //バリューアニメータの更新リスナーの追加(5)
            valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
                //アニメーションフレーム毎に呼ばれる
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float r = ((Float)animation.getAnimatedValue()).floatValue();
                    float x = r*toX+(1-r)*fromX;
                    float y = r*toY+(1-r)*fromY;
                    float s = r*toScale +(1-r)*fromScale;
                    imageView.setTranslationX(x);
                    imageView.setTranslationY(y);
                    imageView.setScaleX(s);
                    imageView.setScaleY(s);
                }
            });
            
            //アニメーションの開始
            valueAnimator.start();
            animator = valueAnimator;
        }
        //アニメータセット
        else if (TAG_ANIMATOR_SET.equals(tag)) {
            //回転するアニメータの生成
            ObjectAnimator rotationAnimator =
                ObjectAnimator.ofFloat(imageView, "rotation", 0.0f, 360.0f);
            rotationAnimator.setDuration(500);
            
            //伸縮するアニメータの生成
            ObjectAnimator scalexAnimator =
                ObjectAnimator.ofFloat(imageView, "scaleX", 1.0f, 2.0f);
            scalexAnimator.setDuration(500);
            scalexAnimator.setRepeatMode(ValueAnimator.REVERSE);
            scalexAnimator.setRepeatCount(1);
            
            //アニメータセットの生成(6)
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(rotationAnimator).before(scalexAnimator);
            
            //アニメーションの開始
            animatorSet.start();
            animator = animatorSet;
        }
    }
}