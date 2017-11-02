package net.npaka.puzzlegame;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

//グラフィックス
public class Graphics {
    private Bitmap bmp;   //ビットマップ
    private Canvas canvas;//キャンバス

    //コンストラクタ
    public Graphics(int w, int h) {
        bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bmp);
    }

    //ビットマップの取得
    public Bitmap getBitmap() {
        return bmp;
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

    //ビットマップの描画
    public void drawBitmap(Bitmap bitmap, Rect src, Rect dst) {
        if (canvas == null) return;
        canvas.drawBitmap(bitmap, src, dst, null);
    }
}
