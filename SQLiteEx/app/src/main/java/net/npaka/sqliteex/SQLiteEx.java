package net.npaka.sqliteex;
import android.app.Activity;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

//データベースの読み書き
public class SQLiteEx extends Activity
    implements View.OnClickListener {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private final static String TAG_WRITE  = "write";
    private final static String TAG_READ   = "read";
    private final static String DB_NAME    = "test.db";//DB名
    private final static String DB_TABLE   = "test";   //テーブル名
    private final static int    DB_VERSION = 1;        //バージョン

    private EditText       editText;//エディットテキスト
    private SQLiteDatabase db;      //データベースオブジェクト

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
        editText.setText("これはテストです。");
        editText.setLayoutParams(new LinearLayout.LayoutParams(MP, WC));
        layout.addView(editText);

        //ボタンの生成
        layout.addView(makeButton("書き込み", TAG_WRITE));
        layout.addView(makeButton("読み込み", TAG_READ));

        //データベースオブジェクトの取得(5)
        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
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
        //DBへの書き込み
        if (TAG_WRITE.equals(tag)) {
            try {
                String str = editText.getText().toString();
                writeDB(str);
            } catch (Exception e) {
                editText.setText("書き込み失敗しました。");
            }
        }
        //DBからの読み込み
        else if (TAG_READ.equals(tag)) {
            try {
                String str = readDB();
                editText.setText(str);
            } catch (Exception e) {
                editText.setText("読み込み失敗しました。");
            }
        }
    }

    //データベースへの書き込み(6)
    private void writeDB(String info) throws Exception {
        ContentValues values = new ContentValues();
        values.put("id", "0");
        values.put("info", info);
        int colNum = db.update(DB_TABLE, values, null, null);
        if (colNum == 0) db.insert(DB_TABLE, "", values);
    }

    //データベースからの読み込み(7)
    private String readDB() throws Exception {
        Cursor c = db.query(DB_TABLE, new String[]{"id", "info"},
                "id='0'", null, null, null, null);
        if (c.getCount() == 0) throw new Exception();
        c.moveToFirst();
        String str = c.getString(1);
        c.close();
        return str;
    }

    //データベースヘルパーの定義(1)
    private static class DBHelper extends SQLiteOpenHelper {
        //データベースヘルパーのコンストラクタ(2)
        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        //データベースの生成(3)
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table if not exists "+
                DB_TABLE+"(id text primary key,info text)");
        }

        //データベースのアップグレード(4)
        @Override
        public void onUpgrade(SQLiteDatabase db,
            int oldVersion, int newVersion) {
            db.execSQL("drop talbe if exists "+DB_TABLE);
            onCreate(db);
        }
    }
}