package net.npaka.gestureex;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

//ジェスチャーイベントの処理
public class GestureEx extends Activity {
    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new GestureView(this));
    }
}