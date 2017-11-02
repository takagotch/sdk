package net.npaka.sensorgame;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//センサービュー
public class SensorView extends SurfaceView implements
    SurfaceHolder.Callback, Runnable {
    //シーン定数
    public final static int
        S_TITLE = 0,
        S_PLAY = 1,
        S_GOAL = 2,
        S_GAMEOVER = 3;

    //システム定数
    public final static int
        W     = 750, //画面幅
        H     = 1334,//画面高さ
        MAX_V = 10;  //最大速度

    //システム
    private SurfaceHolder holder;   //サーフェイスホルダー
    private Thread        thread;   //スレッド
    private int           scene;    //シーン
    private Graphics      g;        //グラフィックス
    private float[]       oriValues;//端末の傾き

    //イメージ
    private Bitmap imgBg;      //背景
    private Bitmap imgBall;    //ボール
    private Bitmap imgMap;     //マップ
    private Bitmap imgTitle;   //タイトル
    private Bitmap imgTap;     //タップ
    private Bitmap imgGoal;    //ゴール
    private Bitmap imgGameOver;//ゲームオーバー

    //ボール
    private float ballX=W/2;//X座標
    private float ballY=H/2;//Y座標
    private float ballVX;   //X速度
    private float ballVY;   //Y速度

    //マップ
    private int[] mapData;//ピクセル郡

    //コンストラクタ
    public SensorView(Context context) {
        super(context);

        //イメージの読み込み
        imgBg = Util.res2bmp(R.drawable.bg);
        imgBall = Util.res2bmp(R.drawable.ball);
        imgTitle = Util.res2bmp(R.drawable.title);
        imgTap = Util.res2bmp(R.drawable.tap);
        imgGoal = Util.res2bmp(R.drawable.goal);
        imgGameOver = Util.res2bmp(R.drawable.gameover);

        //システム
        scene = S_TITLE;
        g = new Graphics();
        oriValues = new float[3];
        loadMap();

        //サーフェイスホルダーの生成
        holder = getHolder();
        holder.setFixedSize(W, H);
        holder.addCallback(this);
    }

    //端末の傾きの指定
    public void setOriValues(float[] ori) {
        oriValues = ori;
    }

    //マップの読み込み
    private void loadMap() {
        //マップ
        imgMap = Util.res2bmp(R.drawable.map);
        mapData = Util.bmp2pixels(Util.res2bmp(R.drawable.map_data));

        //スタート位置の取得(3)
        Point startLT = new Point();//左上
        Point startRB = new Point();//右下
        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                if (mapData[x+y*W] == Color.BLUE) {
                    if (startLT.x == 0 && startLT.y == 0) {
                        startLT.x = x;
                        startLT.y = y;
                    } else {
                        startRB.x = x;
                        startRB.y = y;
                    }
                }
            }
        }
        ballX = startLT.x+(startRB.x-startLT.x)/2;
        ballY = startLT.y+(startRB.y-startLT.y)/2;
        ballVX = 0;
        ballVY = 0;
    }

    //サーフェイス生成時に呼ばれる
    public void surfaceCreated(SurfaceHolder holder) {
        //スレッドの開始
        thread = new Thread(this);
        thread.start();
    }

    //サーフェイス変更時に呼ばれる
    public void surfaceChanged(SurfaceHolder holder,
        int format, int w, int h) {
    }

    //サーフェイス破棄時に呼ばれる
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread = null;
    }

    //ループ処理
    public void run() {
        while(thread != null) {
            long nextTime = System.currentTimeMillis()+30;
            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                g.setCanvas(canvas);
                onTick(g);
                holder.unlockCanvasAndPost(canvas);
            }
            try {
                Thread.sleep(nextTime-System.currentTimeMillis());
            } catch (Exception e) {
            }
        }
    }

    //定期処理
    private void onTick(Graphics g) {
        if (scene == S_PLAY) {
            //加速度の変動(4)
            if (oriValues[1] < -10) {
                ballVY += 0.1;
                if (ballVY < 0) ballVY += 0.2;
            } else if (oriValues[1] > 10) {
                ballVY -= 0.1;
                if (ballVY > 0) ballVY -= 0.2;
            }
            if (oriValues[2] < -10) {
                ballVX -= 0.1;
                if (ballVX > 0) ballVX -= 0.2;
            } else if (oriValues[2] > 10) {
                ballVX += 0.1;
                if (ballVX < 0) ballVX += 0.2;
            }

            //加速度の制限(4)
            if (ballVX < -MAX_V) ballVX = -MAX_V;
            if (ballVX >  MAX_V) ballVX =  MAX_V;
            if (ballVY < -MAX_V) ballVY = -MAX_V;
            if (ballVY >  MAX_V) ballVY =  MAX_V;

            //位置の変動(4)
            ballX += ballVX;
            ballY += ballVY;

            //マップデータのチェック(3)
            int data = mapData[(int)ballX+(int)ballY*W];
            if (data == Color.WHITE) {
                scene = S_GAMEOVER;
            } else if (data == Color.RED) {
                scene = S_GOAL;
            }
        }

        //背景の描画
        g.drawBitmap(imgBg, 0, 0);
        g.drawBitmap(imgMap, 0, 0);

        //ボールの描画
        g.drawBitmap(imgBall, (int)(ballX-60), (int)(ballY-60));

        //タイトルとタップの描画
        if (scene == S_TITLE) {
            g.drawBitmap(imgTitle, (W-660)/2, 100);
            g.drawBitmap(imgTap, (W-500)/2, 1100);
        }
        //ゴールの描画
        else if (scene == S_GOAL) {
            g.drawBitmap(imgGoal, (W-430)/2, 120);
        }
        //ゲームオーバーの描画
        else if (scene == S_GAMEOVER) {
            g.drawBitmap(imgGameOver, (W-700)/2, 120);
        }
    }

    //タッチ時に呼ばれる
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (scene == S_TITLE) {
                scene = S_PLAY;
            } else if (scene == S_GOAL || scene == S_GAMEOVER) {
                scene = S_TITLE;
                loadMap();
            }
        }
        return true;
    }
}