package net.npaka.appwidgetex;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

//ホームスクリーンウィジェットを提供するブロードキャストレシーバー(1)
public class AppWidgetEx extends AppWidgetProvider {
    //ホームスクリーンウィジェット更新時に呼ばれる
    @Override
    public void onUpdate(Context context,
        AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        //ホームスクリーンウィジェットのイベント処理を担当するサービスの起動(2)
        Intent intent = new Intent(context, AppWidgetService.class);
        context.startService(intent);
    }
}