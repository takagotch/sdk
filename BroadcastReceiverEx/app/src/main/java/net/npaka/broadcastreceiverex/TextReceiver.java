package net.npaka.broadcastreceiverex;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

//テキストレシーバー
public class TextReceiver extends BroadcastReceiver {
    //インテントの受信
    @Override
    public void onReceive(Context context, Intent intent) {//(2)
        //パラメータの取得(2)
        Bundle bundle = intent.getExtras();
        String text = bundle.getString("TEXT");
        
        //トーストの表示
        toast(context, text);
    }

    //トーストの表示
    private static void toast(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();
    }
}