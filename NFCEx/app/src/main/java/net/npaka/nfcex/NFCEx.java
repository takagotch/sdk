package net.npaka.nfcex;
import android.app.Activity;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

//NFCの利用
public class NFCEx extends Activity {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private TextView textView;
    
    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //レイアウトの生成
        LinearLayout layout = new LinearLayout(this);
        layout.setBackgroundColor(Color.WHITE);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout); 

        //テキストビューの生成
        textView = new TextView(this);
        textView.setTextSize(24);
        textView.setTextColor(Color.BLACK);
        textView.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        layout.addView(textView);
        
    }
    
    //アプリ開始時に呼ばれる
    @Override
    public void onResume() {
        super.onResume();
        try {
            //FeliCa IDmの取得(1)
            byte[] mID = getIntent().getByteArrayExtra(NfcAdapter.EXTRA_ID);
            
            //バイト配列を16進数文字列に変換(2)
            String hexStr = data2hex(mID);
            textView.setText("NFCEx>"+hexStr);
        } catch (Exception e) {
            textView.setText("NFCEx>");
        }
    }
    
    //バイト配列を16進数文字列に変換(2)
    private String data2hex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for(byte b: data) {
            sb.append(String.format("%02x", b&0xff));
        }
        return sb.toString();
    }
}