package net.npaka.graphicsex;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

//図形の描画
public class GraphicsView extends View {
    //コンストラクタ
    public GraphicsView(Context context) {
        super(context);
        setBackgroundColor(Color.WHITE);
    }

    //描画時に呼ばれる
    @Override 
    protected void onDraw(Canvas canvas) {
        //描画オブジェクトの生成
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        //ラインの描画(1)
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.argb(255, 255, 0, 0));
        canvas.drawLine(50, 10, 50, 10+80, paint);
        
        //パスの描画(2)
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.argb(255, 255, 0, 0));
        Path path = new Path();
        path.moveTo(110+ 0, 10+ 0);
        path.lineTo(110+60, 10+10);
        path.lineTo(110+20, 10+40);
        path.lineTo(110+80, 10+50);
        path.lineTo(110+ 0, 10+80);
        canvas.drawPath(path, paint);

        //四角形の描画(3)
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.argb(255, 0, 0, 255));
        canvas.drawRect(new Rect(10+0, 100+0, 10+80, 100+80), paint);
        
        //四角形の塗り潰し(3)
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(255, 0, 0, 255));
        canvas.drawRect(new Rect(110+0, 100+0, 110+80, 100+80), paint);

        //角丸矩形の描画(3)
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.argb(255, 0, 255, 0));
        canvas.drawRoundRect(new RectF(10+0, 200+0, 10+80, 200+80), 20, 20, paint);
        
        //角丸矩形の塗り潰し(3)
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(255, 0, 255, 0));
        canvas.drawRoundRect(new RectF(110+0, 200+0, 110+80, 200+80), 20, 20, paint);
        
        //円の描画(4)
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.argb(255, 255, 255, 0));
        canvas.drawCircle(50, 340, 40, paint);

        //円の塗り潰し(4)
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(255, 255, 255, 0));
        canvas.drawCircle(150, 340, 40, paint);
    }
}