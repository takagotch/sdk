package net.npaka.imageex;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

//イメージの描画
public class ImageEx extends Activity {
    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new ImageView(this));
    }
}