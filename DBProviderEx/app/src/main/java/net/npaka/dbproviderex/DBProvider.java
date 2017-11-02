package net.npaka.dbproviderex;
import android.content.ContentProvider;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

//DBを提供するコンテンツプロバイダ
public class DBProvider extends ContentProvider {
    private final static String DB_NAME    = "test.db";//DB名
    private final static String DB_TABLE   = "test";   //テーブル名
    private final static int    DB_VERSION = 1;        //バージョン
    private SQLiteDatabase db;//データベース
    
    //コンテンツプロバイダの初期化
    @Override
    public boolean onCreate() {
        //データベースの生成(3)
        DBHelper dbHelper = new DBHelper(getContext());
        db = dbHelper.getWritableDatabase();
        return (db != null);
    }

    //データベースのクエリー命令(4)
    @Override
    public Cursor query(Uri uri, String[] columns, String selection,
        String[] selectionArgs, String sortOrder) {
        return db.query(DB_TABLE, columns, selection,
            selectionArgs, null, null, null);
    }
    
    //データベースの更新命令(4)
    @Override
    public int update(Uri uri, ContentValues values,
        String selection, String[] selectionArgs) {
        return db.update(DB_TABLE, values, null, null);
    }    
   
    //データベースの挿入命令(4)
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        db.insert(DB_TABLE, "", values);
        return uri;
    }
    
    //データベースの削除命令(未使用)
    @Override
    public int delete(Uri uri, String selection,
        String[] selectionArgs) {
        return 0;
    }
    
    //種別の取得(未使用)
    @Override
    public String getType(Uri uri) {
        return null;
    }

    //データベースヘルパーの定義
    private static class DBHelper extends SQLiteOpenHelper {
        //コンストラクタ
        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }
        
        //データベースの生成
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table if not exists "+
                DB_TABLE+"(id text primary key,info text)");
        }

        //データベースのアップグレード
        @Override
        public void onUpgrade(SQLiteDatabase db,
            int oldVersion, int newVersion) {
            db.execSQL("drop talbe if exists "+DB_TABLE);
            onCreate(db);
        }
    }  
}