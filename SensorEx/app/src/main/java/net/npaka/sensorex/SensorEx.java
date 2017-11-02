package net.npaka.sensorex;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

//センサー
public class SensorEx extends Activity 
    implements SensorEventListener {
    private final static String BR = System.getProperty("line.separator");
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private TextView      textView;//テキストビュー
    private SensorManager manager; //センサーマネージャ  
    
    //回転行列
    private float[] inR  = new float[16];
    private float[] outR = new float[16];
    
    //センサーの値
    private float[] accValues = new float[3];//加速度
    private float[] magValues = new float[3];//地磁気
    private float[] oriValues = new float[3];//端末の傾き
    private boolean accEnabled = false;      //加速度センサーの有無
    private boolean magEnabled = false;      //地磁気センサーの有無
    
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

        //テキストビューの生成
        textView = new TextView(this);
        textView.setText("SensorEx");
        textView.setTextSize(24);
        textView.setTextColor(Color.BLACK);
        textView.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        layout.addView(textView);
        
        //センサーマネージャの取得(1)
        manager = (SensorManager)getSystemService(
            Context.SENSOR_SERVICE);
    }

    //アクティビティ再開時に呼ばれる
    @Override
    protected void onResume() {
        super.onResume();
        
        //センサーの取得(2)
        List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor:sensors) {
            //加速度センサーのリスナー登録(3)
            int type = sensor.getType();
            if (type == Sensor.TYPE_ACCELEROMETER) {
                manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
                accEnabled = true;
            }
            //地磁気センサーのリスナー登録(3)
            if (type == Sensor.TYPE_MAGNETIC_FIELD) {
                manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
                magEnabled = true;
            }
        }        
    }
    
    //アクティビティ一時停止時に呼ばれる
    @Override
    protected void onPause() {
        super.onPause();

        //センサーのリスナー解除(4)
        if (accEnabled || magEnabled) {
            manager.unregisterListener(this);
            accEnabled = false;
            magEnabled = false;
        }
    }
    
    //センサーイベント受信時に呼ばれる(5)
    public void onSensorChanged(SensorEvent event) {
        //センサー種別の取得
        int type = event.sensor.getType();
        
        //加速度センサーの情報の格納
        if (type == Sensor.TYPE_ACCELEROMETER) {
            accValues=event.values.clone();
        }
        //地磁気センサーの情報の格納
        else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            magValues = event.values.clone();
        }
        
        //端末の傾きの取得
        if (accEnabled && magEnabled) {
            //回転行列の計算(6)
            SensorManager.getRotationMatrix(inR, null, accValues, magValues);
            
            //端末向きに応じた軸の変更(7)
            SensorManager.remapCoordinateSystem(inR,
                SensorManager.AXIS_X, SensorManager.AXIS_Y, outR);
            
            //端末の傾きの計算(8)
            SensorManager.getOrientation(outR, oriValues);
            
            //ラジアンを度に変換(9)
            oriValues[0] = (float)Math.toDegrees(oriValues[0]);
            oriValues[1] = (float)Math.toDegrees(oriValues[1]);
            oriValues[2] = (float)Math.toDegrees(oriValues[2]);
        }

        //文字列表示
        StringBuffer sb = new StringBuffer();
        sb.append("SensorEx>"+BR);
        if (accEnabled) {
            sb.append("加速度[X軸]:"+fm(accValues[0])+BR);
            sb.append("加速度[Y軸]:"+fm(accValues[1])+BR);
            sb.append("加速度[Z軸]:"+fm(accValues[2])+BR+BR);
        }
        if (accEnabled && magEnabled) {
            sb.append("ピッチ[X軸]:"+fm(oriValues[1])+BR);
            sb.append("ロール[Y軸]:"+fm(oriValues[2])+BR);
            sb.append("アジマス[Z軸]:"+fm(oriValues[0])+BR+BR);
        }
        textView.setText(sb.toString());
    }
    
    //センサー精度の変更時に呼ばれる(5)
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    
    //数値のフォーマット
    private String fm(float value) {
        return (value <= 0)?""+value:"+"+value;
    }
}