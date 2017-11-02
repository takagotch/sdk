package net.npaka.stringex;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

//文字列の描画
public class StringEx extends Activity {
    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new StringView(this));
    }
}