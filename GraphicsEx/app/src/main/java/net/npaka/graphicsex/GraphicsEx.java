package net.npaka.graphicsex;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

//図形の描画
public class GraphicsEx extends Activity {
    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new GraphicsView(this));
    }
}