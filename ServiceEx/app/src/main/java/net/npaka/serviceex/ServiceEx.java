package net.npaka.serviceex;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Date;
import java.util.List;

//サービス
public class ServiceEx extends Activity
    implements View.OnClickListener {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static String TAG_START   = "start";
    private final static String TAG_STOP    = "stop";
    private final static String TAG_CONTROL = "control";
    private Intent     serviceIntent;
    private IMyService binder;
    private Button     btnStart;
    private Button     btnStop;
    private Button     btnControl;
    
    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //レイアウトの生成
        LinearLayout layout = new LinearLayout(this);
        layout.setBackgroundColor(Color.WHITE);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);         

        //ボタンの生成
        btnStart = makeButton("サービスの開始",TAG_START);
        layout.addView(btnStart);
        btnStop = makeButton("サービスの停止",TAG_STOP);
        layout.addView(btnStop);
        btnControl = makeButton("サービスの操作",TAG_CONTROL);
        layout.addView(btnControl);
        setServiceUI(true);
        
        //サービスインテントの生成
        serviceIntent = new Intent(this,MyService.class);
        
        //サービスとの接続(2)
        if (isServiceRunning("net.npaka.serviceex.MyService")) {
            bindService(serviceIntent,connection,BIND_AUTO_CREATE);
            setServiceUI(false);
        }
    }
    
    //サービスが起動中かどうかを調べる(12)
    private boolean isServiceRunning(String className) {
        ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfos =
            am.getRunningServices(Integer.MAX_VALUE);
        for (int i = 0; i < serviceInfos.size(); i++) {
            if (serviceInfos.get(i).service.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }
    
    //サービス操作の指定
    private void setServiceUI(boolean startable) {
        btnStart.setEnabled(startable);
        btnStop.setEnabled(!startable);
        btnControl.setEnabled(!startable);
    }
    
    //ボタンの生成
    private Button makeButton(String text,String tag) {
        Button button = new Button(this);
        button.setTag(tag);
        button.setText(text);
        button.setOnClickListener(this); 
        button.setLayoutParams(new LinearLayout.LayoutParams(WC,WC));
        return button;        
    }    
    
    //ボタンクリックイベントの処理
    public void onClick(View v) {
        String tag = (String)v.getTag();
        
        //サービスの開始
        if (TAG_START.equals(tag)) {
            setServiceUI(false);
            //サービスの開始(1)
            startService(serviceIntent);
            //サービスとの接続(2)
            bindService(serviceIntent,connection,BIND_AUTO_CREATE);
        } 
        //サービスの停止
        else if (TAG_STOP.equals(tag)) {
            setServiceUI(true);
            //サービスとの切断(4)
            unbindService(connection);
            //サービスの停止(5)
            stopService(serviceIntent);
        }
        //サービスの操作(6)
        else if (TAG_CONTROL.equals(tag)) {
            try {
                binder.setMessage(""+(new Date()));
            } catch (Exception e) {
            }
        }
    }
    
    //サービスコネクションの生成(3)
    private ServiceConnection connection = new ServiceConnection() {
        //サービス接続時に呼ばれる
        public void onServiceConnected(ComponentName name,IBinder service) {
            binder = IMyService.Stub.asInterface(service);
        }
        
        //サービス切断時に呼ばれる
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    };
}