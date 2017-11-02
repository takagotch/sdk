package net.npaka.todoapp;

import android.app.Activity;
import android.util.TypedValue;

//ユーティリティ
public class Util {
    //dpをpxに変換(7)
    public static int dp2px(Activity activity, float dp) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            dp, activity.getResources().getDisplayMetrics());
    }
}
