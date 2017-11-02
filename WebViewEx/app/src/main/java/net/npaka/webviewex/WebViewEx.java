package net.npaka.webviewex;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

//Webビュー
public class WebViewEx extends Activity {
    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Webビューの生成(1)
        WebView webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSaveFormData(false);
        settings.setSupportZoom(false);

        //Webビューの通知リクエストの処理(3)
        webView.setWebViewClient(new WebViewClient() {
            //エラー時に呼ばれる(API23以前)
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode,
                String description, String url) {
                toast("通信エラーです。");
            }

            //エラー時に呼ばれる(API23以降)
            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req,
                WebResourceError rerr) {
                toast("通信エラーです。");
            }
        }); 
        setContentView(webView); 
        
        //HTMLの読み込み(2)
        webView.loadUrl("https://www.google.co.jp/intl/ja_jp/nexus/");
    }
    
    //トーストの表示
    private void toast(String text) {
        if (text == null) text = "";
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}