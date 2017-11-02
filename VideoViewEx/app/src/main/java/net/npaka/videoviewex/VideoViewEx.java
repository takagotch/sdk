package net.npaka.videoviewex;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.VideoView;
import android.widget.MediaController;

//ムービーの再生
public class VideoViewEx extends Activity {
    
    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        try {
            //ビデオビューの生成(1)
            VideoView videoView = new VideoView(this);
            videoView.requestFocus();
            videoView.setMediaController(new MediaController(this));
            setContentView(videoView);

            //動画の再生(2)
            videoView.setVideoURI(Uri.parse("android.resource://"+
                getPackageName()+"/"+R.raw.sample));
            videoView.start();
        } catch (Exception e) {
            android.util.Log.e("", e.toString());
        }
    }
}