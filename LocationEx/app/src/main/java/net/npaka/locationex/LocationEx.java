package net.npaka.locationex;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

//位置情報の取得
public class LocationEx extends Activity implements LocationListener {
    private final static String BR = System.getProperty("line.separator");
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static String[] PERMISSIONS = {
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION};
    private final static int REQUEST_PERMISSONS = 1;

    private TextView textView;//テキストビュー
    private LocationManager manager; //ロケーションマネージャ

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
        textView.setText("LocationEx");
        textView.setTextSize(24);
        textView.setTextColor(Color.BLACK);
        textView.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        layout.addView(textView);

        //ロケーションマネージャの取得(1)
        manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        //ユーザーの利用許可のチェック(2)
        checkPermissions();
    }

    //アクティビティ再開時に呼ばれる
    @Override
    public void onResume() {
        super.onResume();

        //位置情報更新の開始
        setLocationUpdateEnabled(true);
    }

    //アクティビティ一時停止時に呼ばれる
    @Override
    public void onPause() {
        super.onPause();

        //位置情報更新の開始
        setLocationUpdateEnabled(false);
    }

    //位置情報変更を通知する時に呼ばれる(4)
    public void onLocationChanged(Location location) {
        //緯度と経度の取得(5)
        textView.setText("LocationEx>"+BR+
            "緯度:"+location.getLatitude()+BR+
            "経度:"+location.getLongitude());
    }

    //位置情報取得有効化を通知する時に呼ばれる(4)
    public void onProviderEnabled(String provider) {
    }

    //位置情報取得無効化を通知する時に呼ばれる(4)
    public void onProviderDisabled(String provider) {
    }

    //位置情報状態変更を通知する時に呼ばれる(4)
    public void onStatusChanged(String provider,
        int status, Bundle extras) {
    }

    //位置情報更新の開始・停止
    private void setLocationUpdateEnabled(boolean enabled) {
        //パーミッションのチェック
        if (!isGranted()) {
            return;
        }

        //ロケーションマネージャの登録と解除
        try {
            if (enabled) {
                manager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 0, 0, this);
            } else {
                manager.removeUpdates(this);
            }
        } catch (SecurityException e) {
        }
    }

    //ユーザーの利用許可のチェック(2)
    private void checkPermissions() {
        //未許可
        if (!isGranted()) {
            //許可ダイアログの表示
            ActivityCompat.requestPermissions(this, PERMISSIONS,
                REQUEST_PERMISSONS);
        }
    }

    //ユーザーの利用許可が済かどうかの取得(2)
    private boolean isGranted() {
        for (int i  = 0; i < PERMISSIONS.length; i++) {
            if (PermissionChecker.checkSelfPermission(
                LocationEx.this, PERMISSIONS[i]) !=
                PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    //許可ダイアログ選択時に呼ばれる(2)
    @Override
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] results) {
        if (requestCode == REQUEST_PERMISSONS) {
            //未許可
            if (!isGranted()) {
                textView.setText("LocationEx>"+BR+
                    "位置情報の取得が未許可です");
            }
        } else {
            super.onRequestPermissionsResult(
                requestCode, permissions, results);
        }
    }

}