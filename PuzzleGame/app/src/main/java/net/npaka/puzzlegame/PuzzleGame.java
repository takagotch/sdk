package net.npaka.puzzlegame;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

//スライドパズル
public class PuzzleGame extends Activity {
    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Util.setActivity(this);

        //フルスクリーンの指定
        getWindow().clearFlags(
            WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //ビューの追加
        setContentView(new PuzzleView(this));
    }
}