package net.npaka.dreamex;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.service.dreams.DreamService;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//スクリーンセーバー(Daydream)
public class DreamEx extends DreamService {//(1)
    private TextView textView;

    //ウィンドウに取り付けた時に呼ばれる
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        //入力イベントを受け付けるかどうかを指定(2)
        setInteractive(false);

        //フルスクリーンにするかどうかを指定(2)
        setFullscreen(true);

        //レイアウトの生成
        FrameLayout layout = new FrameLayout(this);
        layout.setBackgroundColor(Color.WHITE);
        setContentView(layout);

        //イメージビューの生成
        Bitmap bmp = BitmapFactory.decodeResource(
            getResources(), R.drawable.bg);
        ImageView iv = new ImageView(this);
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iv.setImageBitmap(bmp);
        layout.addView(iv);

        //サブレイアウトの生成
        LinearLayout sublayout = new LinearLayout(this);
        sublayout.setPadding(20, 20, 20, 20);
        sublayout.setGravity(Gravity.LEFT|Gravity.TOP);
        layout.addView(sublayout);

        //テキストビューの生成
        textView = new TextView(this);
        textView.setTextSize(44);
        textView.setTextColor(Color.DKGRAY);
        sublayout.addView(textView);

        //現在時刻の更新
        updateCurrentTime();
    }

    //開始時に呼ばれる
    @Override
    public void onDreamingStarted() {
        super.onDreamingStarted();

        //時間経過のブロードキャストレシーバーの登録(3)
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(receiver, filter);
    }

    //停止時に呼ばれる
    @Override
    public void onDreamingStopped() {
        super.onDreamingStopped();

        //時間経過のブロードキャストレシーバーの解除(3)
        unregisterReceiver(receiver);
    }

    //ウィンドウから取り外した時に呼ばれる
    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    //現在時刻の更新(4)
    private void updateCurrentTime() {
        SimpleDateFormat dateFormat =
            new SimpleDateFormat("HH:mm", Locale.JAPANESE);
        textView.setText(dateFormat.format(new Date()));
    }

    //時刻経過のブロードキャストレシーバー(3)
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        //時刻経過時に呼ばれる
        @Override
        public void onReceive(Context context, Intent intent) {
            updateCurrentTime();
        }
    };
}
