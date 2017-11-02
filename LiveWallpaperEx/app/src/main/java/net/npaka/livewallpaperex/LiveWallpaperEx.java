package net.npaka.livewallpaperex;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.content.res.Resources;

//ライブ壁紙
public class LiveWallpaperEx extends WallpaperService {//(1)
    //ライブ壁紙エンジン生成時に呼ばれる(1)
    @Override
    public Engine onCreateEngine() {
        return new WallpaperEngine(getResources());
    }

    //ライブ壁紙エンジン(2)
    public class WallpaperEngine extends Engine
        implements Runnable {
        private SurfaceHolder holder;//サーフェイスホルダー
        private Thread        thread;//スレッド

        private Bitmap image; //イメージ
        private int    pos;   //位置
        private int    size;  //サイズ
        private int    width; //画面幅
        private int    height;//画面高さ

        //コンストラクタ
        public WallpaperEngine(Resources r) {
            image = BitmapFactory.decodeResource(r, R.drawable.sample);
            size = image.getWidth();
            pos = -size;
        }
        
        //サーフェイス生成時に呼ばれる
        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            this.holder = holder;
        }

        //サーフェイス変更時に呼ばれる
        @Override
        public void onSurfaceChanged(SurfaceHolder holder,
            int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            this.width = width;
            this.height = height;
        }

        //表示状態変更時に呼ばれる
        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                //スレッドの開始
                thread = new Thread(this);
                thread.start();
            } else {
                //スレッドの停止
                thread = null;
            }
        }
        
        //サーフェイス破棄時に呼ばれる
        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
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
            //ロック
            Canvas canvas = holder.lockCanvas();

            //画像の描画
            canvas.drawColor(Color.WHITE);
            for (int j = 0; pos+size*j < height; j++) {
                for (int i = 0; pos+size*i < width; i++) {
                    if ((i+j)%2 == 0) {
                        canvas.drawBitmap(image,
                            pos + size * i, pos + size * j, null);
                    }
                }
            }
            pos++;
            if (pos >= 0) pos = -size;

            //アンロック
            holder.unlockCanvasAndPost(canvas);
        }
    }
}