package net.npaka.mapex;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

//マップ
public class MapEx extends FragmentActivity //(1)
    implements OnMapReadyCallback { //(3)

    //アクティビティ起動時に呼ばれる
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //レイアウトファイルの指定(2)
        setContentView(R.layout.main);

        //GoogleMapオブジェクトの取得(3)
        ((SupportMapFragment)getSupportFragmentManager().
            findFragmentById(R.id.map)).getMapAsync(this);
    }

    //マップの準備完了時に呼ばれる
    public void onMapReady(GoogleMap googleMap) {
        //緯度経度とズームの指定(4)
        LatLng coordinate = new LatLng(35.706671, 139.759914);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
            coordinate, 16));
    }
}