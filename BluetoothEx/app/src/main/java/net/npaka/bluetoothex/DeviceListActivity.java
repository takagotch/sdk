package net.npaka.bluetoothex;
import java.util.Set;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED;
import static android.bluetooth.BluetoothDevice.ACTION_FOUND;
import static android.bluetooth.BluetoothDevice.ACTION_NAME_CHANGED;

//端末検索アクティビティ
public class DeviceListActivity extends Activity 
    implements AdapterView.OnItemClickListener {
    private final static String BR = System.getProperty("line.separator");
    private final static int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

    private BluetoothAdapter     btAdapter;//Bluetoothアダプタ
    private ArrayAdapter<String> adapter;  //リストビューのアダプタ

    //アクティビティ起動時に呼ばれる
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setResult(Activity.RESULT_CANCELED);

        //レイアウトの生成
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout); 

        //リストビューのアダプタの生成
        adapter = new ArrayAdapter<String>(this, R.layout.rowdata);
        
        //リストビューの生成
        ListView listView = new ListView(this);
        listView.setLayoutParams(new LinearLayout.LayoutParams(MP, WC));
        listView.setAdapter(adapter);
        layout.addView(listView);
        listView.setOnItemClickListener(this);
        
        //ブロードキャストレシーバーの追加(3)
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DISCOVERY_STARTED);
        filter.addAction(ACTION_FOUND);
        filter.addAction(ACTION_NAME_CHANGED);
        filter.addAction(ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);

        //登録済みBluetooth端末情報の取得(1)
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();//(1)
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device:pairedDevices) {
                adapter.add(device.getName()+BR+device.getAddress());
            }
        }

        //Bluetooth端末の検索開始(2)
        if (btAdapter.isDiscovering()) btAdapter.cancelDiscovery();
        btAdapter.startDiscovery();
    }

    //アクティビティ破棄時に呼ばれる
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (btAdapter != null) btAdapter.cancelDiscovery();
        this.unregisterReceiver(receiver);
    }

    //クリック時に呼ばれる
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        //Bluetooth端末の検索のキャンセル
        btAdapter.cancelDiscovery();

        //戻り値の指定
        String info = ((TextView)view).getText().toString();
        String address = info.substring(info.length()-17);
        Intent intent = new Intent();
        intent.putExtra("device_address", address);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
    
    //ブロードキャストレシーバー(3)
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        //受信時に呼ばれる
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //Bluetooth端末発見
            if (ACTION_NAME_CHANGED.equals(action) ||
                ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.
                    getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    adapter.add(device.getName()+BR+device.getAddress());
                }
            } 
        }
    };
}