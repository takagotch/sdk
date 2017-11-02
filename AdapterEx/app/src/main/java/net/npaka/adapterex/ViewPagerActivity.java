package net.npaka.adapterex;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

//ビューページャーアクティビティ
public class ViewPagerActivity extends Activity {
    private ArrayList<AdapterItem> items;
    
    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //要素の情報群の取得
        items = AdapterEx.items;

        //ビューページャーの生成(7)
        ViewPager viewPager = new ViewPager(this);
        viewPager.setAdapter(new MyAdapter());
        setContentView(viewPager);
    }
    
    //自作アダプタ
    private class MyAdapter extends PagerAdapter {
        //ページ数の取得
        @Override
        public int getCount() {
            return items.size();
        }
    	 
        //ページ生成時に呼ばれる
        @Override
        public Object instantiateItem(View view, int pos) {
            ViewPager pager = (ViewPager)view;
            Context context = ViewPagerActivity.this;
            AdapterItem item = items.get(pos);
          
            //レイアウトの生成
            LinearLayout layout = new LinearLayout(context);
            layout.setBackgroundColor(Color.WHITE);
            layout.setPadding(10, 10, 10, 10);//(4)
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setGravity(Gravity.CENTER);//(5)

            //アイコン
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(240, 240));
            imageView.setImageBitmap(item.icon);
            layout.addView(imageView);
              
            //テキストの指定
            TextView textView = new TextView(context);
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);//(5)
            textView.setText(item.text);
            layout.addView(textView);
            
            //ページャー
            pager.addView(layout, 0);
            return layout;        	
        }
        
        //ページ破棄時に呼ばれる
        @Override
        public void destroyItem(View collection, int position, Object view) {
            ((ViewPager)collection).removeView((View)view);
        }
     
        //ページを構成するView判定時に呼ばれる
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (LinearLayout)object;
        }
    }
}