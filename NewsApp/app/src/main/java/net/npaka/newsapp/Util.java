package net.npaka.newsapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//ユーティリティ
public class Util {
    //メッセージダイアログの定義
    public static class MessageDialog extends DialogFragment {
        //ダイアログの生成
        @Override
        public Dialog onCreateDialog(Bundle bundle) {
            AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
            ad.setMessage(getArguments().getString("text"));
            ad.setPositiveButton("OK", null);
            return ad.create();
        }
    }

    //メッセージダイアログの表示
    public static void showMessageDialog(
        Activity activity, String text) {
        MessageDialog f = new MessageDialog();
        Bundle args = new Bundle();
        args.putString("text", text);
        f.setArguments(args);
        f.show(activity.getFragmentManager(), "messageDialog");
    }

    //プログレスダイアログの表示
    public static ProgressDialog showProgressDialog(Activity activity) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("通信中…");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

    //HTTP通信
    public static byte[] http2data(String path) {
        byte[] w = new byte[1024];
        HttpURLConnection c = null;
        InputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            //HTTP接続のオープン
            URL url = new URL(path);
            c = (HttpURLConnection)url.openConnection();
            c.setRequestMethod("GET");
            c.connect();
            in = c.getInputStream();

            //バイト配列の読み込み
            out = new ByteArrayOutputStream();
            while (true) {
                int size = in.read(w);
                if (size <= 0) break;
                out.write(w, 0, size);
            }
            out.close();

            //HTTP接続のクローズ
            in.close();
            c.disconnect();
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (c != null) c.disconnect();
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (Exception e2) {
            }
            return null;
        }
    }

    //dpをpxに変換
    public static int dp2px(Activity activity, float dp) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            dp, activity.getResources().getDisplayMetrics());
    }

    //日付文字列の変換
    public static String pubDate2ymdhm(String pubDate) {
        try {
            //日付文字列をDateオブジェクトに変換(3)
            SimpleDateFormat df0 = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
            Date date = df0.parse(pubDate);

            //Dateオブジェクトを日付文字列に変換(4)
            SimpleDateFormat df1 = new SimpleDateFormat(
                "yyyy/MM/dd HH:mm", Locale.US);
            return df1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        return "";
    }
}
