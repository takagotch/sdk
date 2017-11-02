package net.npaka.newsapp;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

//WebViewアクティビティ
public class WebViewActivity extends AppCompatActivity {
    //UI
    private ProgressDialog progressDialog;//プログレスダイアログ

    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.setTitle("");

        //パラメータの取得
        String url = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = extras.getString("url");
        }

        //Webビューの生成
        WebView webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        //Webビューの通知リクエストの処理
        webView.setWebViewClient(new WebViewClient() {
            //読み込み完了時に呼ばれる
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressDialog.dismiss();
                WebViewActivity.this.setTitle(view.getTitle());
            }

            //エラー時に呼ばれる(API22以前)
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode,
                String description, String url) {
                Util.showMessageDialog(WebViewActivity.this,
                    "通信エラーです。");
            }

            //エラー時に呼ばれる(API23以降)
            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req,
                WebResourceError rerr) {
                Util.showMessageDialog(WebViewActivity.this,
                    "通信エラーです。");
            }
        });
        setContentView(webView);

        //プログレスの表示
        progressDialog = Util.showProgressDialog(this);

        //HTMLの読み込み
        webView.loadUrl(url);
    }
}
