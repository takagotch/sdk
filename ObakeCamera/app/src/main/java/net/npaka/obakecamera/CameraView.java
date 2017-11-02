package net.npaka.obakecamera;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

//カメラビュー
public class CameraView extends TextureView {
    //システム
    private Activity      activity;    //アクティビティ
    private Handler       uiHandler;   //UIハンドラ
    private Handler       workHandler; //ワークハンドラ
    private boolean       active;      //アクティブ
    private CameraManager manager;     //カメラマネージャ
    private int           viewW;       //カメラ映像表示領域幅
    private int           viewH;       //カメラ映像表示領域高さ
    private boolean       front;       //前面カメラかどうか(3)

    //カメラ
    private String                 cameraId;       //カメラID
    private CameraCharacteristics  cameraInfo;     //カメラ情報
    private Size                   previewSize;    //プレビューサイズ
    private Size                   pictureSize;    //写真サイズ
    private CameraDevice           cameraDevice;   //カメラデバイス
    private CaptureRequest.Builder previewBuilder; //プレビュービルダー
    private CameraCaptureSession   previewSession; //プレビューセッション

    //コンストラクタ
    public CameraView(Context context) {
        super(context);
        //システム
        activity = (Activity)context;
        active = false;

        //ハンドラの生成
        uiHandler = new Handler();
        HandlerThread thread = new HandlerThread("work");
        thread.start();
        workHandler = new Handler(thread.getLooper());

        //カメラマネージャーの取得
        manager = (CameraManager)activity.
            getSystemService(Context.CAMERA_SERVICE);

        //テクスチャービューのリスナーの指定
        setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            //テクスチャ有効化時に呼ばれる(1)
            @Override
            public void onSurfaceTextureAvailable(
                SurfaceTexture surface, int width, int height) {
                if (viewW == 0 && viewH == 0) {
                    viewW = width;
                    viewH = height;
                }
                startCamera();
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
                //カメラの停止
                stopCamera();
                return true;
            }
        });
    }

    //フリップ
    public void flip() {
        if (cameraDevice == null) return;
        front = !front;
        startCamera();
    }

    //カメラの開始
    private void startCamera() {
        try {
            //カメラの停止
            stopCamera();

            //カメラの取得
            cameraId = getCameraId();
            cameraInfo = manager.getCameraCharacteristics(cameraId);

            //プレビューサイズと写真サイズの取得
            previewSize = getPreviewSize(cameraInfo);//(1)
            pictureSize = getPictureSize(cameraInfo);//(2)

            //カメラのオープン
            manager.openCamera(cameraId, new CameraDevice.StateCallback() {
                //接続時に呼ばれる
                @Override
                public void onOpened(CameraDevice camera) {
                    cameraDevice = camera;

                    //レイアウトの調整
                    updateLayoutParams();

                    //プレビュー開始
                    startPreview();
                }

                //切断時に呼ばれる
                @Override
                public void onDisconnected(CameraDevice camera) {
                    camera.close();
                    cameraDevice = null;
                }

                //エラー時に呼ばれる
                @Override
                public void onError(CameraDevice camera, int error) {
                    camera.close();
                    cameraDevice = null;
                    toast("カメラのオープンに失敗しました");
                }
            }, null);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            toast(e.toString());
        }
    }

    //カメラの停止
    private void stopCamera() {
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    //プレビューサイズの調整(1)
    private void updateLayoutParams() {
        int srcW = Math.min(             //描画元幅
            previewSize.getWidth(),
            previewSize.getHeight());
        int srcH = Math.max(             //描画元高さ
            previewSize.getWidth(),
            previewSize.getHeight());
        int dstW = viewW;                //描画先幅
        int dstH = viewW*srcH/srcW;      //描画先高さ
        RelativeLayout.LayoutParams params;
        params = new RelativeLayout.LayoutParams(dstW, dstH);
        params.setMargins(0, (viewH-dstH)/2, 0, 0);
        setLayoutParams(params);
    }

    //カメラIDの取得(3)
    private String getCameraId() {
        try {
            int facing = (front)?
                CameraCharacteristics.LENS_FACING_FRONT:
                CameraCharacteristics.LENS_FACING_BACK;

            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics cameraInfo =
                    manager.getCameraCharacteristics(cameraId);
                //前面・背面カメラ
                if (cameraInfo.get(CameraCharacteristics.LENS_FACING) == facing) {
                    return cameraId;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //プレビューサイズの取得
    private Size getPreviewSize(CameraCharacteristics characteristics) {
        StreamConfigurationMap map = characteristics.get(
                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        Size[] sizes = map.getOutputSizes(SurfaceTexture.class);
        for (int i = 0; i < sizes.length; i++) {
            //サイズ2000x2000以下
            if (sizes[i].getWidth() < 2000 && sizes[i].getHeight() < 2000) {
                return sizes[i];
            }
        }
        return sizes[0];
    }

    //写真サイズの取得
    private Size getPictureSize(CameraCharacteristics characteristics) {
        Size[] sizes = characteristics.
            get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).
            getOutputSizes(ImageFormat.JPEG);
        for (int i = 0; i < sizes.length; i++) {
            //サイズ2000x2000以下
            if (sizes[i].getWidth() < 2000 && sizes[i].getHeight() < 2000) {
                return sizes[i];
            }
        }
        return null;
    }

    //プレビューの開始
    private void startPreview() {
        if (cameraDevice == null) return;
        active = true;

        //出力先となるテクスチャの準備
        SurfaceTexture texture = getSurfaceTexture();
        if (texture == null) return;
        texture.setDefaultBufferSize(
            previewSize.getWidth(),
            previewSize.getHeight());
        Surface surface = new Surface(texture);

        //プレビューセッションの生成
        try {
            previewBuilder = cameraDevice.
                createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface),
                new CameraCaptureSession.StateCallback() {
                //成功時に呼ばれる
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    previewSession = session;
                    updatePreview();
                }

                //失敗時に呼ばれる
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    toast("プレビューセッションの生成に失敗しました");
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //プレビューの更新
    protected void updatePreview() {
        if (cameraDevice == null) return;

        //オートフォーカスの指定
        previewBuilder.set(CaptureRequest.CONTROL_AF_MODE,
            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

        //カメラ映像をテクスチャに表示
        try {
            previewSession.setRepeatingRequest(
                previewBuilder.build(), null, workHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //撮影
    public void takePicture() {
        if (cameraDevice == null || !active) return;
        active = false;

        try {
            //出力先となるイメージリーダーの準備
            ImageReader reader = ImageReader.newInstance(
                pictureSize.getWidth(), pictureSize.getHeight(),
                ImageFormat.JPEG, 2);
            reader.setOnImageAvailableListener(
                new ImageReader.OnImageAvailableListener() {
                //イメージ利用可能時に呼ばれる
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        //画像のバイト配列の取得
                        image = reader.acquireLatestImage();
                        byte[] data = image2data(image);

                        //写真サイズの調整(2)
                        int pw = previewSize.getWidth(); //写真幅
                        int ph = pictureSize.getHeight();//写真高さ
                        int sw = Math.min(pw, ph);       //切取元幅
                        int dw = sw;                     //切取先幅
                        int dh = sw*viewH/viewW;         //切取先高さ
                        Bitmap bmp = Util.data2bmp(data);
                        bmp = Util.cutBitmap(bmp, dw, dh);

                        //写真の反転(3)
                        if (front) bmp = Util.flipBitmap(bmp);

                        //おばけの追加
                        bmp = addObake(bmp);
                        data = Util.bmp2data(bmp);

                        //写真の保存
                        savePhoto(data);
                    } catch (Exception e) {
                        if (image != null) image.close();
                    }
                }
            }, workHandler);

            //キャプチャセッションの開始
            final CaptureRequest.Builder captureBuilder =
                cameraDevice.createCaptureRequest(
                CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE,
                CameraMetadata.CONTROL_MODE_AUTO);
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION,
                getPhotoOrientation());
            List<Surface> outputSurfaces = new LinkedList<>();
            outputSurfaces.add(reader.getSurface());
            cameraDevice.createCaptureSession(outputSurfaces,
                new CameraCaptureSession.StateCallback() {
                //成功時に呼ばれる
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(),
                            new CameraCaptureSession.CaptureCallback() {
                            //完了時に呼ばれる
                            @Override
                            public void onCaptureCompleted(CameraCaptureSession session,
                                    CaptureRequest request, TotalCaptureResult result) {
                                super.onCaptureCompleted(session, request, result);
                                toast("撮影完了");

                                //プレビュー再開
                                startPreview();
                            }
                        }, workHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                //失敗時に呼ばれる
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    toast("キャプチャーセッションの生成に失敗しました");

                    //プレビュー再開
                    startPreview();
                }
            }, workHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //Image→バイト配列
    private byte[] image2data(Image image) {
        Image.Plane plane = image.getPlanes()[0];
        ByteBuffer buffer = plane.getBuffer();
        byte[] data = new byte[buffer.capacity()];
        buffer.get(data);
        return data;
    }


    //写真の向きの計算
    private int getPhotoOrientation() {
        int displayRotation = 0;
        int rotation = activity.getWindowManager().
            getDefaultDisplay().getRotation();
        if (rotation == Surface.ROTATION_0)   displayRotation = 0;
        if (rotation == Surface.ROTATION_90)  displayRotation = 90;
        if (rotation == Surface.ROTATION_180) displayRotation = 180;
        if (rotation == Surface.ROTATION_270) displayRotation = 270;
        int sensorOrientation = cameraInfo.get(
            CameraCharacteristics.SENSOR_ORIENTATION);
        return (sensorOrientation-displayRotation+360)%360;
    }

    //写真の保存
    private void savePhoto(byte[] data) {
        try {
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

    //バイト配列の保存
    private void saveData(byte[] w, String path)
        throws Exception {
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

    //おばけの追加
    private Bitmap addObake(Bitmap bmp) {
        Bitmap obake = null;
        int r = Util.rand(4);
        if (r == 0)  obake = Util.res2bmp(R.drawable.obake0);
        if (r == 1)  obake = Util.res2bmp(R.drawable.obake1);
        if (r == 2)  obake = Util.res2bmp(R.drawable.obake2);
        if (r == 3)  obake = Util.res2bmp(R.drawable.obake3);
        Bitmap result = Bitmap.createBitmap(
            bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bmp, 0, 0, null);
        canvas.drawBitmap(obake,
            Util.rand(viewW-obake.getWidth()),
            Util.rand(viewH-obake.getHeight()), null);
        return result;
    }
}