package net.npaka.fileproviderex;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

//ファイルを提供するコンテンツプロバイダ
public class FileProvider extends ContentProvider {//(2)
    //コンテンツプロバイダの初期化(3)
    @Override
    public boolean onCreate() {
        return true;
    }

    //ファイルの提供(4)
    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode)
        throws FileNotFoundException {
        Context context = getContext();
        try {
            //リソース→ファイル
            String path = context.getFilesDir()+"/"+uri.getLastPathSegment();
            int resID = context.getResources().getIdentifier(
                uri.getLastPathSegment(), "raw", context.getPackageName());
            in2file(context.getResources().openRawResource(resID), path);
            
            //パーセルオブジェクトの生成(6)
            return ParcelFileDescriptor.open(new File(path),
                ParcelFileDescriptor.MODE_READ_ONLY);            
        } catch (Exception e) {
        }
        return null;
    }    
    
    //データベースのクエリー命令(未使用)
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
        String[] selectionArgs, String sortOrder) {
        return null;
    }
    
    //データベースの挿入命令(未使用)
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }
    
    //データベースの更新命令(未使用)
    @Override
    public int update(Uri uri, ContentValues values,
        String selection, String[] selectionArgs) {
        return 0;
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
    
    //入力ストリーム→ファイル
    private static void in2file(InputStream in, String path)
        throws Exception { 
        byte[] w = new byte[1024];
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            while (true) { 
                int size = in.read(w);
                if (size <= 0) break;
                out.write(w, 0, size);
            };
            out.close();
            in.close();
        } catch (Exception e) {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (Exception e2) {
            }
            throw e;
        }
    }
}