package net.npaka.touchex;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

//タッチイベントの処理
public class TouchEx extends Activity {
    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new TouchView(this));
    }
}