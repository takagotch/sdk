package net.npaka.sensorgame;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

//グラフィックス
public class Graphics {
    private Canvas canvas; //キャンバス

    //コンストラクタ
    public Graphics() {
    }

    //キャンバスの指定
    public void setCanvas(Canvas can) {
        canvas = can;
    }

    //ビットマップの描画
    public void drawBitmap(Bitmap bitmap, int x, int y) {
        if (canvas == null) return;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Rect src = new Rect(0, 0, w, h);
        Rect dst = new Rect(x, y, x+w, y+h);
        canvas.drawBitmap(bitmap, src, dst, null);
    }
}
