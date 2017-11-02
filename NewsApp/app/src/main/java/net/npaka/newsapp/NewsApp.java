package net.npaka.newsapp;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.util.Xml;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

//ニュースアプリ
public class NewsApp extends AppCompatActivity {
    //定数
    private final static int REQUEST_EDIT = 0;
    private final static int MENU_ITEM = 0;

    //RSS(1)
    private final static String URL =
        "http://news.yahoo.co.jp/pickup/computer/rss.xml";

    //情報
    private ArrayList<NewsItem> items;       //要素群
    private Handler handler = new Handler(); //ハンドラ

    //UI
    private ListView       listView;      //リストビュー
    private ProgressDialog progressDialog;//プログレスダイアログ

    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.setTitle("ニュースアプリ");

        //情報
        items = new ArrayList<NewsItem>();

        //リストビューの生成
        listView = new ListView(this);
        listView.setScrollingCacheEnabled(false);
        listView.setAdapter(new MyAdapter());
        setContentView(listView);

        //ニュースの読み込み
        loadNews();
    }

    //オプションメニューの生成
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        //オプションメニューへのアイテムの追加
        MenuItem item = menu.add(0, MENU_ITEM, 0, "更新");
        item.setIcon(android.R.drawable.ic_menu_rotate);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    //メニューアイテム選択イベントの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == MENU_ITEM) {
            loadNews();
        }
        return true;
    }

    //WEBビューアクティビティの開始
    private void startWebViewActivity(String url) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("url", url);
        startActivityForResult(intent, REQUEST_EDIT);
    }

    //自作アダプタ
    private class MyAdapter extends BaseAdapter {
        //要素数の取得
        @Override
        public int getCount() {
            return items.size();
        }

        //要素の取得
        @Override
        public NewsItem getItem(int pos) {
            return items.get(pos);
        }

        //要素IDの取得
        @Override
        public long getItemId(int pos) {
            return pos;
        }

        //セルのビューの生成
        @Override
        public View getView(int pos,View view,ViewGroup parent) {
            Context context = NewsApp.this;
            NewsItem item = items.get(pos);

            //レイアウトの生成
            if (view == null) {
                //レイアウトの生成
                LinearLayout layout = new LinearLayout(context);
                layout.setBackgroundColor(Color.WHITE);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(
                    Util.dp2px(NewsApp.this,10),
                    Util.dp2px(NewsApp.this,10),
                    Util.dp2px(NewsApp.this,10),
                    Util.dp2px(NewsApp.this,10));
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = Integer.parseInt((String)v.getTag());
                        NewsItem item = items.get(pos);
                        startWebViewActivity(item.link);
                    }
                });
                view = layout;

                //タイトルの生成
                TextView tvTitle = new TextView(context);
                tvTitle.setId(R.id.cell_title);
                tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                tvTitle.setTextColor(Color.BLACK);
                layout.addView(tvTitle);

                //日付の生成
                TextView tvDate = new TextView(context);
                tvDate.setId(R.id.cell_date);
                tvDate.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                tvDate.setTextColor(Color.DKGRAY);
                tvDate.setGravity(Gravity.RIGHT);
                layout.addView(tvDate);
            }

            //値の指定
            TextView tvTitle = (TextView)view.findViewById(R.id.cell_title);
            tvTitle.setText(item.title);
            TextView tvDate = (TextView)view.findViewById(R.id.cell_date);
            tvDate.setText(Util.pubDate2ymdhm(item.pubDate));
            view.setTag(""+pos);
            return view;
        }
    }

    //ニュースの読み込み
    private void loadNews() {
        //プログレスの表示
        progressDialog = Util.showProgressDialog(this);

        //スレッドの生成
        Thread thread = new Thread(new Runnable() {public void run() {
            //HTTP通信
            byte[] data = Util.http2data(URL);

            //通信失敗
            if (data == null) {
                handler.post(new Runnable() {public void run() {
                    //プログレスの非表示
                    progressDialog.dismiss();

                    //ダイアログの表示
                    Util.showMessageDialog(NewsApp.this,
                            "通信エラーです。");
                }});
                return;

            }
            //XML→要素群
            loadItems(new String(data));

            //UI更新
            handler.post(new Runnable() {public void run() {
                //プログレスの非表示
                progressDialog.dismiss();

                //リストビューの更新
                ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
            }});
        }});
        thread.start();
    }

    //XMLのパース処理(2)
    private void loadItems(String xml) {
        try {
            this.items.clear();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(xml));
            int type = parser.getEventType();
            String tagName = null;
            NewsItem item = null;
            while (type != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                if (type == XmlPullParser.START_TAG) {
                    tagName = name;
                    if (name.equals("item")) {
                        item = new NewsItem();
                    }
                } else if (type == XmlPullParser.END_TAG) {
                    tagName = null;
                    if (name.equals("item")) {
                        this.items.add(item);
                        item = null;
                    }
                } else if (type == XmlPullParser.TEXT) {
                    if (item != null && tagName != null) {
                        if (tagName.equals("title")) {
                            item.title = parser.getText();
                        } else if (tagName.equals("link")) {
                            item.link = parser.getText();
                        } else if (tagName.equals("pubDate")) {
                            item.pubDate = parser.getText();
                        }
                    }
                }
                type = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}