package net.npaka.dbproviderex;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

//DBを提供するコンテンツプロバイダ
public class DBProviderEx extends Activity 
    implements View.OnClickListener {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private final static String TAG_WRITE = "write";
    private final static String TAG_READ  = "read";
    private EditText editText;
    
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
        
        //エディットテキストの生成
        editText = new EditText(this);
        editText.setLayoutParams(new LinearLayout.LayoutParams(MP, WC));
        layout.addView(editText);

        //ボタンの生成
        layout.addView(makeButton("書き込み", TAG_WRITE));
        layout.addView(makeButton("読み込み", TAG_READ));
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

    //ボタンクリックイベントの処理
    public void onClick(View v) {
        String tag = (String)v.getTag();
        if (TAG_WRITE.equals(tag)) {
            try {
                writeDB(editText.getText().toString());
            } catch (Exception e) {
                toast("書き込み失敗しました。");
            }            
        } else if (TAG_READ.equals(tag)) {
            try {
                editText.setText(readDB());               
            } catch (Exception e) {
                toast("読み込み失敗しました。");
            }            
        }
    }    
    
    //DBへの書き込み
    private void writeDB(String info) throws Exception {
        //コンテンツプロバイダが提供するデータベースを示すURI(1)
        Uri uri = Uri.parse("content://net.npaka.dbprovider/");
       
        //コンテンツプロバイダが提供するデータベースへのアクセス(2)
        ContentValues values = new ContentValues();
        values.put("id", "0");
        values.put("info", info);
        int num = getContentResolver().update(uri, values, null, null);
        if (num == 0) getContentResolver().insert(uri, values);
    }

    //DBからの読み込み
    private String readDB() throws Exception {
        //コンテンツプロバイダが提供するデータベースを示すURI(1)
        Uri uri = Uri.parse("content://net.npaka.dbprovider/");
        
        //コンテンツプロバイダが提供するデータベースへのアクセス(2)
        Cursor c = this.getContentResolver().query(uri,
            new String[]{"id", "info"}, "id='0'", null, null);
        if (c.getCount() == 0) throw new Exception();
        c.moveToFirst();
        String str = c.getString(1);
        c.close();
        return str;
    }
    
    //トーストの表示
    private void toast(String text) {
        if (text == null) text = "";
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    } 
}