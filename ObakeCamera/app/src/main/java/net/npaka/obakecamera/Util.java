package net.npaka.obakecamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.TypedValue;

import java.io.ByteArrayOutputStream;
import java.util.Random;

//ユーティリティ
public class Util {
    private static Activity activity;

    //アクティビティの指定
    public static void setActivity(Activity act) {
        activity = act;
    }

    //dpをpxに変換
    public static int dp2px(float dp) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            dp, activity.getResources().getDisplayMetrics());
    }

    //リソースをビットマップに変換
    public static Bitmap res2bmp(int resId) {
        return BitmapFactory.decodeResource(
            activity.getResources(), resId);
    }

    //バイト配列をビットマップに変換(4)
    public static Bitmap data2bmp(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    //ビットマップをバイト配列に変換(5)
    public static byte[] bmp2data(Bitmap src) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 100, os);
        return os.toByteArray();
    }

    //ビットマップの左右反転(6)
    public static Bitmap flipBitmap(Bitmap bmp) {
        Bitmap result = Bitmap.createBitmap(
            bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.scale(-1, 1);
        canvas.drawBitmap(bmp, -bmp.getWidth(), 0, null);
        return result;
    }

    //ビットマップの切り取り(6)
    public static Bitmap cutBitmap(Bitmap bmp, int w, int h) {
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bmp, 0, (h-bmp.getHeight())/2, null);
        return result;
    }

    //乱数の取得(7)
    private static Random rand = new Random();
    public static int rand(int num) {
        return rand.nextInt(num);
    }
}
