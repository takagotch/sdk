package net.npaka.cameraex;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

//カメラとフォト
public class CameraView extends TextureView {
    private int               cameraId;  //カメラID
    private boolean           active;    //アクティブ
    private Handler           uiHandler; //UIハンドラ
    private Camera.CameraInfo cameraInfo;//カメラ情報
    private Camera            camera;    //カメラ

    //コンストラクタ
    public CameraView(Context context) {
        super(context);
        active = true;

        //ハンドラの生成
        uiHandler = new Handler();

        //テクスチャービューのリスナーの指定
        setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            //テクスチャ有効化時に呼ばれる
            @Override
            public void onSurfaceTextureAvailable(
                SurfaceTexture surface, int width, int height) {
                startCamera(surface);
            }

            //テクスチャサイズ変更時に呼ばれる
            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
                int width, int height) {
            }

            //テクスチャ更新時に呼ばれる
            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }

            //テクスチャ破棄時に呼ばれる
            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                stopCamera();
                return true;
            }
        });
    }
    
    //カメラの開始
    private void startCamera(SurfaceTexture surface) {
        //カメラの初期化
        try {
            //カメラの取得
            cameraId = getCameraId();
            cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, cameraInfo);
            camera = Camera.open(cameraId);
            camera.setPreviewTexture(surface);
            rotateCamera();

            //プレビューサイズと写真サイズの指定
            Camera.Parameters params = camera.getParameters();
            Camera.Size previewSize = getPreviewSize(params);
            params.setPreviewSize(previewSize.width, previewSize.height);
            Camera.Size pictureSize = getPictureSize(params);
            params.setPictureSize(pictureSize.width, pictureSize.height);

            //プレビューの開始
            camera.startPreview();
        } catch (Exception e) {
            toast(e.toString());
        }
    }

    //カメラIDの取得
    private int getCameraId() {
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            //背面カメラ
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return i;
            }
        }
        return 0;
    }

    //写真向きの取得
    private int getPhotoOrientation() {
        int displayRotation = 0;
        int rotation = ((Activity)getContext()).
            getWindowManager().getDefaultDisplay().getRotation();
        if (rotation == Surface.ROTATION_0)   displayRotation = 0;
        if (rotation == Surface.ROTATION_90)  displayRotation = 90;
        if (rotation == Surface.ROTATION_180) displayRotation = 180;
        if (rotation == Surface.ROTATION_270) displayRotation = 270;
        return (cameraInfo.orientation-displayRotation+360)%360;
    }

    //カメラを端末向きにあわせて回転
    private void rotateCamera() {
        camera.setDisplayOrientation(getPhotoOrientation());
    }

    //カメラの停止
    private void stopCamera() {
        try {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
        } catch (Exception e) {
            toast(e.toString());
        }
        camera = null;
    }

    //タッチ時に呼ばれる
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!active) return true;
            active = false;

            //撮影
            camera.takePicture(null, null, new Camera.PictureCallback() {
                //写真撮影完了時に呼ばれる
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    //写真の保存
                    savePhoto(data);
                    toast("撮影完了");

                    //プレビュー再開
                    active = true;
                    camera.startPreview();
                }
            });
        }
        return true;
    }

    //プレビューサイズの取得
    private Camera.Size getPreviewSize(Camera.Parameters parameters) {
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        for (Camera.Size size : sizes) {
            //サイズ2000x2000以下
            if (size.width < 2000 && size.height < 2000) {
                return size;
            }
        }
        return sizes.get(0);
    }

    //写真サイズの取得
    private Camera.Size getPictureSize(Camera.Parameters parameters) {
        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
        for (Camera.Size size : sizes) {
            //サイズ2000x2000以下
            if (size.width < 2000 && size.height < 2000) {
                return size;
            }
        }
        return sizes.get(0);
    }

    //写真の保存
    private void savePhoto(byte[] data) {
        try {
            //写真データの回転
            data = rotatePhotoData(data);

            //保存先のパスの生成
            SimpleDateFormat format = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss'.jpg'", Locale.getDefault());
            String fileName = format.format(
                new Date(System.currentTimeMillis()));
            String path = Environment.getExternalStorageDirectory()+
                "/"+fileName;

            //バイト配列の保存
            saveData(data, path);

            //フォトへの登録
            MediaScannerConnection.scanFile(getContext(),
                new String[]{path}, null, null);
        } catch (Exception e) {
            toast(e.toString());
        }
    }

    //写真データを端末向きにあわせて回転
    private byte[] rotatePhotoData(byte[] data) {
        Matrix m = new Matrix();
        m.setRotate(getPhotoOrientation());
        Bitmap original = data2bmp(data);
        Bitmap rotated = Bitmap.createBitmap(original, 0, 0,
            original.getWidth(), original.getHeight(), m, true);
        return bmp2data(rotated, Bitmap.CompressFormat.JPEG, 100);
    }

    //バイト配列→ビットマップ
    private Bitmap data2bmp(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    //ビットマップ→バイト配列
    private byte[] bmp2data(Bitmap src,
        Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);
        return os.toByteArray();
    }
    
    //バイト配列の保存
    private void saveData(byte[] w, String path) throws Exception {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            out.write(w);
            out.close();
        } catch (Exception e) {
            if (out != null) out.close();
            throw e;
        }
    }
    
    //トーストの表示　
    private void toast(final String text) {
        uiHandler.post(new Runnable() {
            public void run() {
                Toast.makeText(getContext(), text,
                    Toast.LENGTH_LONG).show();
            }
        });
    }
}