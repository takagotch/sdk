package net.npaka.puzzlegame;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

//スライドパズル
public class PuzzleView extends View {
    //シーン定数(1)
    private final static int
        S_TITLE = 0,//タイトル
        S_PLAY  = 1,//プレイ
        S_CLEAR = 2;//クリア

    //ゲーム画面サイズ定数
    public final static int
        W = 750, //ゲーム画面幅
        H = 1334;//ゲーム画面高さ

    //システム
    private int   scene = S_TITLE;   //シーン(1)
    private int[] data = new int[25];//ピース配列情報(3)
    private int   shuffle;           //シャッフル

    //グラフィックス
    private Graphics g;   //グラフィックス
    private Rect     gSrc;//描画元領域
    private Rect     gDst;//描画先領域

    //イメージ
    private Bitmap imgBg;   //背景
    private Bitmap imgFrame;//フレーム
    private Bitmap imgPic;  //絵
    private Bitmap imgTitle;//タイトル
    private Bitmap imgTap;  //タップ
    private Bitmap imgClear;//クリア

    //コンストラクタ
    public PuzzleView(Activity activity) {
        super(activity);

        //イメージの読み込み
        imgBg = Util.res2bmp(R.drawable.bg);
        imgFrame = Util.res2bmp(R.drawable.frame);
        imgPic = Util.res2bmp(R.drawable.pic);
        imgTitle = Util.res2bmp(R.drawable.title);
        imgTap = Util.res2bmp(R.drawable.tap);
        imgClear = Util.res2bmp(R.drawable.clear);

        //描画元領域と描画先領域のの計算(2)
        Point displaySize = Util.getDisplaySize();
        int srcH = W*displaySize.y/displaySize.x;
        gSrc = new Rect(0, (H-srcH)/2, W, (H-srcH)/2+srcH);
        gDst = new Rect(0, 0, displaySize.x, displaySize.y);

        //グラフィックスの生成
        g = new Graphics(W, H);

        //シーンの初期化
        initScene(S_TITLE);
    }

    //シーンの初期化
    private void initScene(int scene) {
        this.scene = scene;

        //タイトル
        if (scene == S_TITLE) {
            for (int i = 0; i < 16; i++) data[i] = i;
        }
        //プレイ
        else if (scene == S_PLAY) {
            //シャッフルの実行(9)
            shuffle = 20;
            while (shuffle > 0) {
                if (movePiece(Util.rand(4), Util.rand(4))) {
                    shuffle--;
                }
            }
        }
        invalidate();
    }

    //描画時に呼ばれる
    @Override
    protected void onDraw(Canvas canvas) {
        //背景の描画
        g.drawBitmap(imgBg, 0, 0);
        g.drawBitmap(imgFrame, (W-700)/2, (H-700)/2);

        //ピースの描画
        int px = (W-600)/2;
        int py = (H-600)/2;
        for (int i = 0; i < 16; i++) {
            int sx = data[i]%4;
            int sy = data[i]/4;
            int dx = i%4;
            int dy = i/4;
            if (scene != S_PLAY || data[i] != 15) {
                g.drawBitmap(imgPic,
                    new Rect(150*sx, 150*sy, 150*sx+150,150*sy+150),
                    new Rect(px+150*dx, py+150*dy, px+150*dx+150,
                    py+150*dy+150));
            }
        }

        //タイトルとタップの描画
        if (scene == S_TITLE) {
            g.drawBitmap(imgTitle, (W-600)/2, 120);
            g.drawBitmap(imgTap, (W-500)/2, 1100);
        }

        //クリアの描画
        else if (scene == S_CLEAR) {
            g.drawBitmap(imgClear, (W-600)/2, 120);
        }

        //画面に反映(2)
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(g.getBitmap(), gSrc, gDst, null);
    }

    //タッチ時に呼ばれる
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //端末のタッチ位置をゲーム画面のタッチ位置に変換(4)
        int touchX = (int)(event.getX()*gSrc.width()/gDst.width());
        int touchY = (int)(event.getY()*gSrc.width()/gDst.width()+gSrc.top);

        //アクション
        int touchAction = event.getAction();
        if (touchAction == MotionEvent.ACTION_DOWN) {
            //タイトル
            if (scene == S_TITLE) {
                initScene(S_PLAY);
            }
            //プレイ
            else if (scene == S_PLAY) {
                //タッチ位置からピースの列番号と行番号を求める(5)
                int px = (W-600)/2;
                int py = (H-600)/2;
                if (px < touchX && touchX < px+600 &&
                    py < touchY && touchY < py+600) {
                    int tx = (touchX-px)/150;
                    int ty = (touchY-py)/150;
                    movePiece(tx, ty);
                }
            }
            //ゲームオーバー
            else if (scene == S_CLEAR) {
                initScene(S_TITLE);
            }
        }
        return true;
    }

    //ピースの移動
    private boolean movePiece(int tx, int ty) {
        //空きマスの列番号と行番号を求める(6)
        int fx = 0;
        int fy = 0;
        for (int i = 0; i < 16; i++) {
            if (data[i] == 15) {
                fx = i%4;
                fy = i/4;
                break;
            }
        }
        if ((fx == tx && fy == ty) || (fx != tx && fy != ty)) {
            return false;
        }

        //ピースを上にスライド(7)
        if (fx == tx && fy < ty) {
            for (int i = fy; i < ty; i++) {
                data[fx+i*4] = data[fx+i*4+4];
            }
            data[tx+ty*4] = 15;
        }
        //ピースを下にスライド(7)
        else if (fx == tx && fy > ty) {
            for (int i = fy; i > ty; i--) {
                data[fx+i*4] = data[fx+i*4-4];
            }
            data[tx+ty*4] = 15;
        }
        //ピースを左にスライド(7)
        else if (fy == ty && fx < tx) {
            for (int i = fx; i < tx; i++) {
                data[i+fy*4] = data[i+fy*4+1];
            }
            data[tx+ty*4] = 15;
        }
        //ピースを右にスライド(7)
        else if (fy == ty && fx > tx) {
            for (int i = fx; i > tx; i--) {
                data[i+fy*4] = data[i+fy*4-1];
            }
            data[tx+ty*4] = 15;
        }

        //シャッフル時はクリアチェックは行わない
        if (shuffle > 0) {
            return true;
        }

        //ゲームのクリア判定(8)
        int clearCheck = 0;
        for (int i = 0; i < 16; i++) {
            if (data[i] == i) clearCheck++;
        }
        if (clearCheck == 16) {
            scene = S_CLEAR;
        }

        //画面の更新
        invalidate();
        return true;
    }
}