package net.npaka.fcmex;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

//登録トークン取得サービス(1)
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    //登録トークン更新時に呼ばれる
    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        android.util.Log.d("debug","onTokenRefresh>>>>"+token);
    }
}