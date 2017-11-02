package net.npaka.puzzlegame;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Display;

import java.util.Random;

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

    //dpをpxに変換
    public static int dp2px(float dp) {
        return (int)TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp,
            activity.getResources().getDisplayMetrics());
    }

    //乱数の取得(10)
    private static Random rand = new Random();
    public static int rand(int num) {
        return rand.nextInt(num);
    }

    //ディスプレイサイズの取得(11)
    public static Point getDisplaySize() {
        Display display = activity.getWindowManager().
            getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point;
    }
}
