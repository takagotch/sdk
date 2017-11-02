package net.npaka.appwidgetex;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import java.util.Random;
     
//ホームスクリーンウィジェットを制御するサービス
public class AppWidgetService extends Service {
    private static final String ACTION_BTNCLICK =
        "net.npaka.appwidget.ACTION_BTNCLICK";
    
    //サービス開始時に呼ばれる
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        
        //リモートビューの生成(3)
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.appwidget);

        //ペンディングインテントの設定(4)
        Intent newintent = new Intent();
        newintent.setAction(ACTION_BTNCLICK);
        PendingIntent pending = PendingIntent.getService(this, 0, newintent, 0);
        view.setOnClickPendingIntent(R.id.button1, pending);
        
        //振るボタンがクリックされた時の処理(5)
        if (ACTION_BTNCLICK.equals(intent.getAction())) {
            btnClicked(view);
        }
        
        //ホームスクリーンウィジェットの画面更新(6)
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        ComponentName widget = new ComponentName(
            "net.npaka.appwidgetex",
            "net.npaka.appwidgetex.AppWidgetEx");
        manager.updateAppWidget(widget, view);
        return START_STICKY;
    }
     
    //バインダを返す
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
     
    //振るボタンがクリックされた時の処理(5)
    public void btnClicked(RemoteViews view){
        int[] ids = {
            R.drawable.dice1, R.drawable.dice2, R.drawable.dice3,
            R.drawable.dice4, R.drawable.dice5, R.drawable.dice6};
        int idx = rand(6);
        view.setImageViewResource(R.id.imageview1, ids[idx]);
    }
    
    //乱数の取得
    private static Random rand = new Random();
    public static int rand(int num) {
        return (rand.nextInt()>>>1)%num;
    }    
}