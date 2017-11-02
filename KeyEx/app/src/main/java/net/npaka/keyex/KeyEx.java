package net.npaka.keyex;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

//キーイベントの処理
public class KeyEx extends Activity {
    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new KeyView(this));
    }
}