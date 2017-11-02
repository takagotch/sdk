package net.npaka.surfaceviewex;
import android.content.res.Resources;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//サーフェイスビューの利用
public class SurfaceViewView extends SurfaceView 
    implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder holder;//サーフェイスホルダー
    private Thread        thread;//スレッド

    private Bitmap image;//イメージ
    private int    px = 0; //X座標
    private int    py = 0; //Y座標
    private int    vx = 6; //X速度
    private int    vy = 6; //Y速度
    
    //コンストラクタ
    public SurfaceViewView(Context context) {
        super(context);

        //画像の読み込み
        Resources r = getResources();
        image = BitmapFactory.decodeResource(r, R.drawable.sample);
                
        //サーフェイスホルダーの生成(2)
        holder = getHolder();
        holder.addCallback(this);
    }

    //サーフェイスの生成(1)
    public void surfaceCreated(SurfaceHolder holder) {
        //スレッドの開始(3)
        thread = new Thread(this);
        thread.start();
    }

    //サーフェイスの変更(1)
    public void surfaceChanged(SurfaceHolder holder,
        int format, int w, int h) {
    }   
    
    //サーフェイスの破棄(1)
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread = null;
    }

    //ループ処理
    public void run() {
        while(thread != null) {
            long nextTime = System.currentTimeMillis()+30;
            onTick();
            try {
                Thread.sleep(nextTime-System.currentTimeMillis());
            } catch (Exception e) {
            }
        }
    }
    
    //定期処理
    private void onTick() {
        //ダブルバッファリング(4)
        Canvas canvas = holder.lockCanvas();
        if (canvas == null) return;
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(image, px-120, py-120, null);
        holder.unlockCanvasAndPost(canvas);
        
        //移動
        if (px < 0 || getWidth() < px) vx = -vx;
        if (py < 0 || getHeight() < py) vy = -vy;
        px += vx;
        py += vy;
    }
}