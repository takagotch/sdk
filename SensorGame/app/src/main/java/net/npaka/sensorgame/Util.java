package net.npaka.sensorgame;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.Display;

//ユーティリティ
public class Util {
    private static Activity activity;

    //アクティビティの指定
    public static void setActivity(Activity act) {
        activity = act;
    }

    //リソースをビットマップに変換
    public static Bitmap res2bmp(int resId) {
        return BitmapFactory.decodeResource(
            activity.getResources(), resId);
    }

    //ディスプレイサイズの取得
    public static Point getDisplaySize() {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point;
    }

    //画像からピクセル毎の色配列を取得(5)
    public static int[] bmp2pixels(Bitmap bmp) {
        bmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int pixels[] = new int[w*h];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
        return pixels;
    }
}
