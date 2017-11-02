package net.npaka.adapterex;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

//リストビューアクティビティ
public class ListViewActivity extends Activity {
    private ArrayList<AdapterItem> items;
    
    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //要素の情報群の取得
        items = AdapterEx.items;

        //リストビューの生成(2)
        ListView listView = new ListView(this);
        listView.setScrollingCacheEnabled(false);
        listView.setAdapter(new MyAdapter());
        setContentView(listView);
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
        public AdapterItem getItem(int pos) {
            return items.get(pos);
        }

        //要素IDの取得
        @Override
        public long getItemId(int pos) {
            return pos;
        }        
        
        //セルのビューの生成(3)
        @Override
        public View getView(int pos, View view, ViewGroup parent) {
            Context context = ListViewActivity.this;
            AdapterItem item = items.get(pos);
            
            //レイアウトの生成
            if (view == null) {
                LinearLayout layout = new LinearLayout(context);
                layout.setBackgroundColor(Color.WHITE);
                layout.setPadding(10, 10, 10, 10);//(4)
                layout.setGravity(Gravity.CENTER_VERTICAL);//(5)
                view = layout;

                //アイコン
                ImageView imageView = new ImageView(context);
                imageView.setTag("icon");
                imageView.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
                layout.addView(imageView);
            
                //テキストの指定
                TextView textView = new TextView(context);
                textView.setTag("text");
                textView.setTextColor(Color.BLACK);
                textView.setPadding(10, 20, 10, 20);//(4)
                layout.addView(textView);
            }
            
            //値の指定
            ImageView imageView = (ImageView)view.findViewWithTag("icon");
            imageView.setImageBitmap(item.icon);
            TextView textView = (TextView)view.findViewWithTag("text");
            textView.setText(item.text);
            return view;
        }
    }
}