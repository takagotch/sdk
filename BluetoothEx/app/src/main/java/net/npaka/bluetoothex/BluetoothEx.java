package net.npaka.bluetoothex;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

//Bluetooth通信
public class BluetoothEx extends AppCompatActivity
    implements View.OnClickListener {
    private final static String BR = System.getProperty("line.separator");
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static int MP = ViewGroup.LayoutParams.MATCH_PARENT;

    //リクエスト定数
    private static final int RQ_CONNECT_DEVICE = 1;
    private static final int RQ_ENABLE_BT      = 2;

    //Bluetooth
    private BluetoothAdapter btAdapter;  //Bluetoothアダプタ
    private ChatManager      chatManager;//チャットマネージャ

    //UI
    private EditText edtSend;   //送信エディットテキスト
    private Button   btnSend;   //送信ボタン
    private TextView lblReceive;//受信ラベル
    
    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        //レイアウトの生成
        LinearLayout layout = new LinearLayout(this);
        layout.setBackgroundColor(Color.WHITE);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout); 
        
        //送信エディットテキストの生成
        edtSend = new EditText(this);
        edtSend.setText("", TextView.BufferType.NORMAL);
        edtSend.setLayoutParams(new LinearLayout.LayoutParams(MP, WC));
        layout.addView(edtSend);
        
        //送信ボタンの生成
        btnSend = new Button(this);
        btnSend.setText("送信");
        btnSend.setOnClickListener(this);
        btnSend.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        layout.addView(btnSend);

        //受信ラベルの生成
        lblReceive = new TextView(this);
        lblReceive.setText("");
        lblReceive.setTextSize(16.0f);
        lblReceive.setLayoutParams(new LinearLayout.LayoutParams(MP, WC));
        layout.addView(lblReceive);    
        
        //Bluetoothアダプタ
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        
        //チャットマネージャ
        chatManager = new ChatManager(this);
    }

    //アクティビティ開始時に呼ばれる
    @Override
    public void onStart() {
        super.onStart();
        
        //Bluetoothの有効化
        if (!btAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, RQ_ENABLE_BT);
        }
        
        //サーバの接続待ちスレッドの開始
        if (chatManager.getState() == ChatManager.STATE_NONE) {
            chatManager.accept();
        }
    }

    //アクティビティ破棄時に呼ばれる
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        //サーバの接続待ちスレッドの停止
        chatManager.stop();
    }

    //他のBluetooth端末からの発見を有効化(4)
    private void ensureDiscoverable() {
        if (btAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(intent);
        }
    }

    //オプションメニュー生成時に呼ばれる
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item0 = menu.add(0, 0, 0, "端末検索");
        item0.setIcon(android.R.drawable.ic_search_category_default);
        MenuItem item1 = menu.add(0, 1, 0, "発見有効");
        item1.setIcon(android.R.drawable.ic_menu_view);
        return true;
    }

    //オプションメニュー選択時に呼ばれる
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            //端末検索
            Intent intent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(intent, RQ_CONNECT_DEVICE);
            return true;
        } else if (item.getItemId() == 1) {
            //発見有効
            ensureDiscoverable();
            return true;
        }
        return false;
    }
    

    //アクティビティ復帰時に呼ばれる
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //端末検索
        if (requestCode == RQ_CONNECT_DEVICE ){
            if (resultCode == Activity.RESULT_OK) {
                //クライアントの接続要求スレッドの開始
                String address = data.getExtras().getString("device_address");
                chatManager.connect(btAdapter.getRemoteDevice(address));
            }
        }
        //発見有効
        else if (requestCode == RQ_ENABLE_BT) {
            if (resultCode != Activity.RESULT_OK) {
                addText("Bluetoothが有効ではありません");
            }
        }
    }

    //受信テキストの追加
    public void addText(final String text) {
        lblReceive.setText(text+BR+lblReceive.getText());
    }
    
    //ボタンクリックイベントの処理
    public void onClick(View v) {
        try {
            //メッセージの送信
            String message = edtSend.getText().toString();
            if (message.length() > 0) chatManager.write(message.getBytes());
            addText(message);
            edtSend.setText("");
        } catch (Exception e) {
        }           
    }  
}
