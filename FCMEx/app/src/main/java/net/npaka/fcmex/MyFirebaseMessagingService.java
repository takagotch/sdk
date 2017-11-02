package net.npaka.fcmex;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

//メッセージ受信サービス(2)
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    //メッセージ受信時に呼ばれる
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        android.util.Log.d("debug","onMessageReceived>>>>"+
            remoteMessage.getNotification());
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            showNotification(this.getApplicationContext(),
                    title, body);
        }
    }

    //ノーティフィケーションの表示
    private void showNotification(Context context,
        String title, String text) {
        //ノーティフィケーションオブジェクトの生成
        Notification.Builder builder = new Notification.Builder(context);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setSmallIcon(R.mipmap.ic_launcher);

        //ペンディングインテントの指定
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName("net.npaka.fcmex",
            "net.npaka.fcmex.FCMEx"));
        intent.removeCategory(Intent.CATEGORY_DEFAULT);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        builder.setContentIntent(PendingIntent.getActivity(context, 0,
            intent, PendingIntent.FLAG_CANCEL_CURRENT));

        //ノーティフィケーションの表示
        NotificationManager nm = (NotificationManager)
            context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(1);
        nm.notify(1, builder.build());
    }
}