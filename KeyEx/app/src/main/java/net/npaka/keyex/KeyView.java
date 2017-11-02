package net.npaka.keyex;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.View;

//キーイベントの処理
public class KeyView extends View {
    private int keyCode = -999;//キーコード
    
    //コンストラクタ
    public KeyView(Context context) {
        super(context);
        setBackgroundColor(Color.WHITE);

        //フォーカス指定(1)
        setFocusable(true);
        setFocusableInTouchMode(true);
    }
    
    //描画時に呼ばれる
    @Override 
    protected void onDraw(Canvas canvas) {
        //ペイント
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(48);
        
        //キーコードの描画
        String str = "";
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP)    str = "DPAD_UP";
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)  str = "DPAD_DOWN";
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)  str = "DPAD_LEFT";
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) str = "DPAD_RIGHT";
        if (keyCode == KeyEvent.KEYCODE_BACK)       str = "BACK";
        if (keyCode == KeyEvent.KEYCODE_MENU)       str = "MENU";
        canvas.drawText("KeyCode>"+keyCode+" "+str, 0, 60, paint);
    }
    
    //キーダウン時に呼ばれる(2)
    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        this.keyCode = keyCode;
        
        //画面の再描画(3)
        invalidate();
        return true;
    }

    //キーアップ時に呼ばれる(2)
    @Override 
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        this.keyCode = -999;
        
        //画面の再描画(3)
        invalidate();
        return true;
    }
}