package net.npaka.bluetoothex;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

//チャットマネージャ
public class ChatManager {
    //設定定数
    private static final String NAME = "BluetoothEx";
    private static final UUID   MY_UUID =
        UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    //状態定数
    public static final int STATE_NONE       = 0;
    public static final int STATE_LISTEN     = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED  = 3;

    //変数
    private BluetoothEx      parent;    //本体
    private Handler          handler;   //ハンドラ
    private BluetoothAdapter btAdapter; //Bluetoothアダプタ
    private int              state;     //状態
    private AcceptThread     acceptT;   //サーバの接続待ちスレッド
    private ConnectThread    connectT;  //クライアントの接続要求スレッド
    private ConnectedThread  connectedT;//接続中の処理スレッド
    
    //コンストラクタ
    public ChatManager(BluetoothEx parent) {
        this.parent    = parent;
        this.handler   = new Handler();
        this.btAdapter = BluetoothAdapter.getDefaultAdapter();
        this.state     = STATE_NONE;
    }

    //状態の指定
    private synchronized void setState(final int state) {
        this.state = state;
        handler.post(new Runnable() {public void run() {
            if (state == STATE_CONNECTED) {
                parent.setTitle("BluetoothEx - 接続完了");
            } else if (state == STATE_CONNECTING) {
                parent.setTitle("BluetoothEx - 接続中");
            } else if (state == STATE_LISTEN) {
                parent.setTitle("BluetoothEx - 接続待ち");
            } else if (state == STATE_NONE) {
                parent.setTitle("BluetoothEx - 未接続");
            }
        }});
    }

    //状態の取得
    public synchronized int getState() {
        return state;
    }

    //サーバの接続待ちスレッドの開始
    public synchronized void accept() {
        if (connectT   != null) {connectT.cancel();  connectT   = null;}
        if (connectedT != null) {connectedT.cancel();connectedT = null;}
        if (acceptT == null) {
            acceptT = new AcceptThread();
            acceptT.start();
            setState(STATE_LISTEN);
        }
    }

    //クライアントの接続要求スレッドの開始
    public synchronized void connect(BluetoothDevice device) {
        if (acceptT  != null) {acceptT.cancel(); acceptT  = null;}
        if (connectT != null) {connectT.cancel();connectT = null;}
        if (connectT == null) {
            connectT = new ConnectThread(device);
            connectT.start();
            setState(STATE_CONNECTING);
        }
    }

    //接続中の処理スレッドの開始
    public synchronized void connected(BluetoothSocket socket,
        BluetoothDevice device) {
        if (acceptT    != null) {acceptT.cancel(); acceptT  = null;}
        if (connectT   != null) {connectT.cancel();connectT = null;}
        if (connectedT == null) {
            connectedT = new ConnectedThread(socket);
            connectedT.start();
            setState(STATE_CONNECTED);
        }
    }

    //スレッドの停止
    public synchronized void stop() {
        if (acceptT    != null) {acceptT.cancel();   acceptT    = null;}
        if (connectT   != null) {connectT.cancel();  connectT   = null;}
        if (connectedT != null) {connectedT.cancel();connectedT = null;}
        setState(STATE_NONE);
    }

    //送信データの書き込み
    public synchronized void write(byte[] out) {
        if (state != STATE_CONNECTED) return;
        connectedT.write(out);
    }

    //サーバの接続待ちスレッド(5)
    private class AcceptThread extends Thread {
        private BluetoothServerSocket serverSocket;
        
        //コンストラクタ
        public AcceptThread() {
            try {
                serverSocket = btAdapter.
                    listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (Exception e) {
                android.util.Log.e("debug", "AcceptThread>>"+e.getMessage());
                setState(STATE_NONE);
            }
        }

        //処理
        public void run() {
            BluetoothSocket socket=null;
            while (state != STATE_CONNECTED) {
                try {
                    socket = serverSocket.accept();
                } catch (Exception e) {
                    break;
                }
                if (socket != null) {
                    if (state == STATE_LISTEN || state == STATE_CONNECTING) {
                        connected(socket, socket.getRemoteDevice());
                    } else if (state == STATE_NONE || state == STATE_CONNECTED) {
                        try {
                            socket.close();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }

        //キャンセル
        public void cancel() {
            try {
                serverSocket.close();
            } catch (Exception e) {
            }
        }
    }

    //クライアントの接続要求スレッド(6)
    private class ConnectThread extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;
        private boolean         cancel;

        //コンストラクタ
        public ConnectThread(BluetoothDevice device) {
            try {
                this.device = device;
                this.socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (Exception e) {
                android.util.Log.e("debug", "ConnectThread>>"+e.getMessage());
                setState(STATE_NONE);
            }
        }

        //処理
        public void run() {
            btAdapter.cancelDiscovery();
            try {
                socket.connect();
                connectT = null;
                connected(socket, device);
            } catch (Exception e) {
                //停止
                try {
                    socket.close();
                } catch (Exception e2) {
                }
                setState(STATE_NONE);
                
                //サーバの再開
                if (!cancel) accept();
            }
        }

        //キャンセル
        public void cancel() {
            cancel = true;
            try {
                socket.close();
            } catch (Exception e) {
            }
        }
    }

    //接続中の処理スレッド(7)
    private class ConnectedThread extends Thread {
        private BluetoothSocket socket;
        private InputStream     in;
        private OutputStream    out;
        private boolean         cancel;

        //コンストラクタ
        public ConnectedThread(BluetoothSocket socket) {
            try {
                this.socket = socket;
                this.in     = socket.getInputStream();
                this.out    = socket.getOutputStream();
            } catch (Exception e) {
                android.util.Log.e("debug", "ConnectedThread>>"+e.getMessage());
                setState(STATE_NONE);
            }
        }

        //処理
        public void run() {
            byte[] buf = new byte[1024];
            while (true) {
                try {
                    int size = in.read(buf);
                    final String str = new String(buf, 0, size);
                    handler.post(new Runnable() {public void run() {
                        parent.addText(str);
                    }});
                } catch (Exception e) {
                    //停止
                    try {
                        socket.close();
                    } catch (Exception e2) {
                    }                    
                    setState(STATE_NONE);
                    
                    //サーバの再開
                    if (!cancel) accept();
                    break;
                }
            }
        }

        //書き込み
        public void write(byte[] buf) {
            try {
                out.write(buf);
            } catch (Exception e) {
            }
        }

        //キャンセル
        public void cancel() {
            cancel = true;
            try {
                socket.close();
            } catch (Exception e) {
            }
        }
    }
}
