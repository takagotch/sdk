package net.npaka.inappex;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//アプリ内課金
public class InAppEx extends Activity implements
    View.OnClickListener {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static String TAG_GET_SKU_DETAILS = "getSkuDetails";//購入可能アイテム情報の取得
    private final static String TAG_GET_PURCHASES = "getPurchases";   //購入済みアイテム情報の取得
    private final static String TAG_BUY = "buy";                      //アイテムの購入
    
    private IInAppBillingService mService;//サービス
    private TextView textView;            //テキストビュー

    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        //レイアウトの生成
        LinearLayout layout = new LinearLayout(this);
        layout.setBackgroundColor(Color.WHITE);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);    
        
        //UI
        layout.addView(makeButton("購入可能アイテム情報の取得", TAG_GET_SKU_DETAILS));
        layout.addView(makeButton("購入済みアイテム情報の取得", TAG_GET_PURCHASES));
        layout.addView(makeButton("アイテムの購入", TAG_BUY));
        textView = new TextView(this);
        textView.setTextSize(16.0f);
        textView.setTextColor(Color.rgb(0,0,0));        
        layout.addView(textView);
        
        //サービスのバインド(2)
        bindService(getExplicitIapIntent(),
            mServiceConn, Context.BIND_AUTO_CREATE);
    }

    //明示的なインテントの生成
    private Intent getExplicitIapIntent() {
        PackageManager pm = getPackageManager();
        Intent implicitIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        implicitIntent.setPackage("com.android.vending");
        List<ResolveInfo> resolveInfos = pm.queryIntentServices(implicitIntent, 0);
        ResolveInfo serviceInfo = resolveInfos.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        Intent iapIntent = new Intent();
        iapIntent.setComponent(component);
        return iapIntent;
    }
    
    //アクティビティ破棄時に呼ばれる
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        //サービスのアンバインド(2)
        unbindService(mServiceConn);
    }
    
    //ボタンの生成
    private Button makeButton(String text, String tag) {
        Button button = new Button(this);
        button.setText(text);
        button.setTag(tag);
        button.setOnClickListener(this);
        button.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        return button;
    }    
    
    //ボタン押下時に呼ばれる
    public void onClick(View view) {
        String tag = (String)view.getTag();

        //購入可能アイテム情報の取得(3)
        if (TAG_GET_SKU_DETAILS.equals(tag)) {
            new Thread(new Runnable() {public void run() {
                try {
                    //アイテムIDリストの生成
                    ArrayList<String> itemIdList = new ArrayList<String>();
                    itemIdList.add("test");
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("ITEM_ID_LIST", itemIdList);
                        
                    //購入可能アイテム情報の取得
                    Bundle skuDetails = mService.getSkuDetails(
                        3, getPackageName(), "inapp", bundle);
                    int response = skuDetails.getInt("RESPONSE_CODE");
                    
                    //成功時
                    if (response == 0) {
                        //情報文字列の生成
                        String str = "";
                        List<String> detailsList =
                            skuDetails.getStringArrayList("DETAILS_LIST");
                        for (String details : detailsList) {
                            JSONObject object = new JSONObject(details);
                            String itemId = object.getString("productId");//アイテムID
                            String price = object.getString("price");  //価格
                            str += itemId+":"+price+"\n";
                        }
                        
                        //テキストビューの表示
                        final String text = str;
                        runOnUiThread(new Runnable() {public void run() {
                            textView.setText(text);
                        }});
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }}).start();            
        }
        //購入済みアイテム情報の取得(4)
        else if (TAG_GET_PURCHASES.equals(tag)) {
            new Thread(new Runnable() {public void run() {
                String str = "";
                int flag = readUnlockFlag();

                //プリファレンスに情報なし
                if (flag == 0) {
                    try {
                        //購入済みアイテム情報の取得
                        Bundle ownedItems = mService.getPurchases(3,
                            getPackageName(), "inapp", null);
                        int response = ownedItems.getInt("RESPONSE_CODE");

                        //成功時
                        if (response == 0) {
                            List<String> itemIdList = ownedItems.getStringArrayList(
                                "INAPP_PURCHASE_ITEM_LIST");
                            if (itemIdList.size() != 0 && itemIdList.get(0).equals("test")) {
                                str = "購入済み";
                                writeUnlockFlag(1);
                            } else {
                                str = "未購入";
                                writeUnlockFlag(2);
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                //購入済み
                else if (flag == 1) {
                    str = "購入済み";
                }
                //未購入
                else if (flag == 2) {
                    str = "未購入";
                }
                        
                //テキストビューの表示
                final String text = str;
                runOnUiThread(new Runnable() {public void run() {
                    textView.setText(text);
                }});
            }}).start();
        }
        //アイテムの購入(5)
        else if (TAG_BUY.equals(tag)) {
            try {
                Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
                    "test", "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(),
                    Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
    }    

    //アクティビティ結果の取得
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //アイテムの購入の結果取得
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            int response = data.getIntExtra("RESPONSE_CODE", 0);
           
            //成功時
            if (response == 0) {
                String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");//購入データ
                try {
                    JSONObject json = new JSONObject(purchaseData);
                    String sku = json.getString("productId");
                    if (sku.equals("test")) {
                        textView.setText("購入成功");
                        textView.invalidate();
                        writeUnlockFlag(1);
                    }
                } catch (JSONException e) {
                    textView.setText("購入失敗");
                    textView.invalidate();
                    e.printStackTrace();
                }
            }
       }
    }
    
    //アンロックフラグの書き込み
    private void writeUnlockFlag(int unlockFlag) {
        SharedPreferences pref = PreferenceManager.
            getDefaultSharedPreferences(this);
        Editor editor = pref.edit();
        editor.putInt("unLockFlag", unlockFlag);
        editor.commit();
    }
    
    //アンロックフラグの読み込み
    private int readUnlockFlag() {
        SharedPreferences pref = PreferenceManager.
            getDefaultSharedPreferences(this);
        return pref.getInt("unLockFlag", 0);
    }
    
    //サービスコネクション(1)
    private ServiceConnection mServiceConn = new ServiceConnection() {
        //サービス接続時に呼ばれる
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            textView.setText("接続完了");
            textView.invalidate();
            
            //サービスの取得
            mService = IInAppBillingService.Stub.asInterface(service);
        }
        
        //サービス切断時に呼ばれる
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };    
}