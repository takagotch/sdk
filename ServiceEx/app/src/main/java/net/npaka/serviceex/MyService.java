package net.npaka.serviceex;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

//サービスの定義(7)
public class MyService extends Service {
    private Handler handler = new Handler();
    private boolean running = false;
    private String  message = "Message";

    //サービス生成時に呼ばれる
    @Override
    public void onCreate() {
        super.onCreate();
    }
    
    //サービス開始時に呼ばれる
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        
        //ノーティフィケーションの表示
        showNotification(this,
            "自作サービス",
            "自作サービスを操作します");

        //サービスの開始
        Thread thread = new Thread(){public void run() {
            running = true;
            while (running) {
                handler.post(new Runnable(){public void run() {
                    toast(MyService.this, message);
                }});
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                }
            }
        }};
        thread.start();
        return START_NOT_STICKY;
    }
    
    //サービス停止時に呼ばれる
    @Override
    public void onDestroy() {
        running = false;
        super.onDestroy();
    }
    
    //サービス接続時に呼ばれる
    @Override
    public IBinder onBind(Intent intent) {
        return IMyServiceBinder;
    }
    
    //ノーティフィケーションの表示
    private void showNotification(Context context,
        String title, String text) {
        //ノーティフィケーションオブジェクトの生成(9)
        Notification.Builder builder = new Notification.Builder(context);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setSmallIcon(R.mipmap.ic_launcher);

        //ペンディングインテントの指定(10)
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName("net.npaka.serviceex",
            "net.npaka.serviceex.ServiceEx"));
        intent.removeCategory(Intent.CATEGORY_DEFAULT);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        builder.setContentIntent(PendingIntent.getActivity(context, 0,
            intent, PendingIntent.FLAG_CANCEL_CURRENT));

        //ノーティフィケーションの表示(11)
        NotificationManager nm = (NotificationManager)
            context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(1);
        nm.notify(1, builder.build());
    }

    //トーストの表示　
    private static void toast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
    
    //バインダの生成(8)
    private final IMyService.Stub IMyServiceBinder = new IMyService.Stub() {
        public void setMessage(String msg) throws RemoteException {
            message = msg;
        }
    };
}