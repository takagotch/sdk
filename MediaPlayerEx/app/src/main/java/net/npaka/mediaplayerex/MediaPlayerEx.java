package net.npaka.mediaplayerex;
import android.app.Activity;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

//サウンドの再生
public class MediaPlayerEx extends Activity 
    implements View.OnClickListener {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static String TAG_BGM = "bgm";
    private final static String TAG_SE  = "se";
    private MediaPlayer mediaPlayer;//メディアプレーヤー(1)
    private SoundPool   soundPool;  //サウンドプール(4)
    private int         soundId;    //サウンドID(5)

    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //サウンドプールの生成(4)
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        
        //サウンドプールのサウンドの読み込み(5)
        soundId = soundPool.load(this, R.raw.se, 1);
        
        //レイアウトの生成
        LinearLayout layout = new LinearLayout(this);
        layout.setBackgroundColor(Color.WHITE);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);            
        
        //ボタンの生成
        layout.addView(makeButton("BGM再生/停止", TAG_BGM));
        layout.addView(makeButton("SE再生", TAG_SE));
    }
    
    //ボタンの生成
    private Button makeButton(String text, String tag) {
        Button button = new Button(this);
        button.setText(text);
        button.setTag(tag);
        button.setOnClickListener(this);
        button.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        return button;
    }
    
    //ボタンクリック時に呼ばれる
    public void onClick(View view) {
        String tag = (String)view.getTag();
        //BGM再生/停止
        if (TAG_BGM.equals(tag)) {
            if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
                playBGM();
            } else {
                stopBGM();
            }
        } 
        //SE再生
        else if (TAG_SE.equals(tag)) {
            playSE();
        }
    }

    //アクティビティ停止時に呼ばれる
    @Override
    public void onStop() {
        super.onStop();
        
        //メディアプレイヤーの停止(7)
        stopBGM();
        
        //サウンドプールの解放(7)
        soundPool.release();
    }
    
    //BGMの再生
    private void playBGM() {
        stopBGM();
        try {
            //メディアプレイヤーの生成(1)
            mediaPlayer = MediaPlayer.create(this, R.raw.bgm);
            mediaPlayer.setLooping(true);
            
            //メディアプレイヤーの再生(2)
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        } catch (Exception e) {
        }
    }

    //BGMの停止
    private void stopBGM() {
        if (mediaPlayer == null) return;
        try {
            //メディアプレイヤーの停止(3)
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        } catch (Exception e) {
        }
    }
    
    //SEの再生
    private void playSE() {
        //サウンドプールのサウンドの再生(6)
        soundPool.play(soundId, 100, 100, 1, 0, 1);
    }
}