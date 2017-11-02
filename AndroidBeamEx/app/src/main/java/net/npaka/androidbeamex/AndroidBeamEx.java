package net.npaka.androidbeamex;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.nio.charset.Charset;

//Androidビーム
public class AndroidBeamEx extends Activity implements 
    CreateNdefMessageCallback, OnNdefPushCompleteCallback {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private NfcAdapter nfcAdapter;
    private EditText   editText;

    //アクティビティ生成時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //レイアウトの生成
        LinearLayout layout = new LinearLayout(this);
        layout.setBackgroundColor(Color.WHITE);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);            

        //エディットテキストの生成
        editText = new EditText(this);
        editText.setText("これはテストです");
        editText.setLayoutParams(new LinearLayout.LayoutParams(MP, WC));
        editText.setTextColor(Color.BLACK);
        layout.addView(editText);
        
        //Androidビームの準備(1)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null) {
            nfcAdapter.setNdefPushMessageCallback(this, this);
            nfcAdapter.setOnNdefPushCompleteCallback(this, this);
        } else {
            toast("NFCを利用できない端末です");
        }
    }
    
    //インテント受信時に呼ばれる
    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    //アクティビティのレジューム時に呼ばれる
    @Override
    public void onResume() {
        super.onResume();
        //Androidビームの受信処理(5)
        Intent intent = getIntent();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] msgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msg = (NdefMessage)msgs[0];
            editText.setText(new String(msg.getRecords()[0].getPayload()));            
        }
    }    

    //Androidビームが可能な別端末が範囲内にある時に呼ばれる(1)
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        //Androidビームのメッセージの生成(2)
        String text = editText.getText().toString();
        NdefMessage msg = new NdefMessage(
            new NdefRecord[]{
                //MIMEタイプ含むレコードの生成(3)
                createMimeRecord("application/net.npaka.androidbeamex", text.getBytes()),
                //AAR含むレコードの生成(4)
                NdefRecord.createApplicationRecord("net.npaka.androidbeamex")
            });
        return msg;
    }

    //AAR含むレコードの生成(4)
    private NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
            mimeBytes, new byte[0], payload);
        return mimeRecord;
    }
    
    //Androidビームの送信完了時に呼ばれる(1)
    @Override
    public void onNdefPushComplete(NfcEvent event) {
        Handler handler = new Handler();
        handler.post(new Runnable() {public void run() {
            toast("送信完了");
        }});
    }
    
    //トーストの表示
    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
