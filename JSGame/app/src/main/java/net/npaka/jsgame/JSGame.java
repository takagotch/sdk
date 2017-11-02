package net.npaka.jsgame;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

//JavaScriptゲーム
public class JSGame extends Activity implements
    View.OnClickListener {
    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //画面サイズ
        Display display = getWindowManager().
            getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);

        //レイアウトの生成
        RelativeLayout layout = new RelativeLayout(this);
        layout.setBackgroundColor(Color.rgb(218,245,254));
        setContentView(layout);

        //Webビューの生成
        WebView webView = new WebView(this);
        RelativeLayout.LayoutParams params0 =
            new RelativeLayout.LayoutParams(screenSize.x,screenSize.x);
        webView.setLayoutParams(params0);
        layout.addView(webView);

        //JavaScript実行のためのWebビュー設定(1)
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        //Webビューの通知リクエストの処理
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
                WebResourceError error) {
                toast("通信エラーです。");
            }
        });

        //ボタンの生成
        Button button = new Button(this);
        button.setBackgroundResource(R.drawable.banner);
        RelativeLayout.LayoutParams params1 =
            new RelativeLayout.LayoutParams(screenSize.x,screenSize.x*140/640);
        params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        button.setLayoutParams(params1);
        button.setOnClickListener(this);
        layout.addView(button);

        //assetsのHTMLの読み込み(2)
        webView.loadUrl("file:///android_asset/index.html");
    }

    //ボタン押下時に呼ばれる
    public void onClick(View view) {
        launchBrowser("http://medakanomori.com");
    }

    //ブラウザでWebサイトを開く(3)
    private void launchBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    //トーストの表示
    private void toast(String text) {
        if (text == null) text = "";
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}