package net.npaka.sensorgame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.util.List;

//センサーゲーム
public class SensorGame extends Activity implements
    SensorEventListener {
    //システム
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

    //UI
    private SensorView sensorView;

    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Util.setActivity(this);

        //フルスクリーンの指定
        getWindow().clearFlags(
            WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //センサービューの生成
        RelativeLayout layout = new RelativeLayout(this);
        setContentView(layout);
        sensorView = new SensorView(this);
        layout.addView(sensorView);

        //画面サイズの調整(1)
        Point displaySize = Util.getDisplaySize();
        int dstW = displaySize.x;
        int dstH = displaySize.x*SensorView.H/SensorView.W;
        RelativeLayout.LayoutParams params;
        params = new RelativeLayout.LayoutParams(dstW, dstH);
        params.setMargins(0, (displaySize.y-dstH)/2,
            0, (displaySize.y-dstH)/2);
        sensorView.setLayoutParams(params);

        //センサーマネージャの取得
        manager = (SensorManager)getSystemService(
            Context.SENSOR_SERVICE);
    }

    //アクティビティ再開時に呼ばれる
    @Override
    protected void onResume() {
        super.onResume();

        //センサーの取得
        List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor:sensors) {
            //加速度センサーのリスナー登録
            int type = sensor.getType();
            if (type == Sensor.TYPE_ACCELEROMETER) {
                manager.registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_UI);
                accEnabled = true;
            }
            //地磁気センサーのリスナー登録
            if (type == Sensor.TYPE_MAGNETIC_FIELD) {
                manager.registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_UI);
                magEnabled = true;
            }
        }
    }

    //アクティビティ一時停止時に呼ばれる
    @Override
    protected void onPause() {
        super.onPause();

        //センサーのリスナー解除
        if (accEnabled || magEnabled) {
            manager.unregisterListener(this);
            accEnabled = false;
            magEnabled = false;
        }
    }

    //センサーイベント受信時に呼ばれる
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
            //回転行列の計算
            SensorManager.getRotationMatrix(inR, null, accValues, magValues);

            //端末向きに応じた軸の変更
            SensorManager.remapCoordinateSystem(inR,
                SensorManager.AXIS_X, SensorManager.AXIS_Y, outR);

            //端末の傾きの計算
            SensorManager.getOrientation(outR, oriValues);

            //ラジアンを度に変換
            oriValues[0] = (float)Math.toDegrees(oriValues[0]);
            oriValues[1] = (float)Math.toDegrees(oriValues[1]);
            oriValues[2] = (float)Math.toDegrees(oriValues[2]);
            sensorView.setOriValues(oriValues);
        }
    }

    //センサー精度の変更時に呼ばれる
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}