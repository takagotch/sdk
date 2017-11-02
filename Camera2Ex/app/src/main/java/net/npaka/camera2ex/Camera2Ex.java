package net.npaka.camera2ex;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.view.Window;
import android.view.WindowManager;

//カメラとフォト
public class Camera2Ex extends Activity {
    private final static int REQUEST_PERMISSONS = 0;
    private final static String[] PERMISSIONS = {
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE};

    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        //フルスクリーンの指定(1)
        getWindow().clearFlags(
            WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //ユーザーの利用許可のチェック(2)
        checkPermissions();
    }

    //ユーザーの利用許可のチェック(2)
    private void checkPermissions() {
        //許可
        if (isGranted()) {
            initContentView();
        }
        //未許可
        else {
            //許可ダイアログの表示
            ActivityCompat.requestPermissions(this, PERMISSIONS,
                REQUEST_PERMISSONS);
        }
    }

    //ユーザーの利用許可が済かどうかの取得(2)
    private boolean isGranted() {
        for (int i  = 0; i < PERMISSIONS.length; i++) {
            if (PermissionChecker.checkSelfPermission(
                    Camera2Ex.this, PERMISSIONS[i]) !=
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
            //許可
            if (isGranted()) {
                initContentView();
            }
        } else {
            super.onRequestPermissionsResult(
                requestCode, permissions, results);
        }
    }

    //コンテンツビューの初期化
    private void initContentView() {
        setContentView(new Camera2View(this));
    }
}