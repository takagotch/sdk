package net.npaka.adapterex;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

//リストビューとグリッドビューとビューページャー
public class AdapterEx extends Activity 
    implements View.OnClickListener {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static String TAG_LISTVIEW = "listview";
    private final static String TAG_GRIDVIEW = "gridview";
    private final static String TAG_VIEWPAGER = "viewpager";
    public static ArrayList<AdapterItem> items;
    
    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //要素の情報群の生成(1)
        items = new ArrayList<AdapterItem>();
        for (int i = 0; i < 30; i++) {
            AdapterItem item = new AdapterItem();
            item.icon = res2bmp(this, R.drawable.sample);
            item.text = "項目"+i;
            items.add(item);
        }        

        //レイアウトの生成
        LinearLayout layout = new LinearLayout(this);
        layout.setBackgroundColor(Color.WHITE);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);       
        
        //ボタンの生成
        layout.addView(makeButton("リストビュー", TAG_LISTVIEW));
        layout.addView(makeButton("グリッドビュー", TAG_GRIDVIEW));
        layout.addView(makeButton("ビューページャー", TAG_VIEWPAGER));
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
    
    //ボタンクリック時に呼ばれる
    public void onClick(View v) {
        String tag = (String)v.getTag();
        if (TAG_LISTVIEW.equals(tag)) {
            Intent intent = new Intent(this, ListViewActivity.class);
            startActivity(intent);
        } else if (TAG_GRIDVIEW.equals(tag)) {
            Intent intent = new Intent(this, GridViewActivity.class);
            startActivity(intent);
        } else if (TAG_VIEWPAGER.equals(tag)) {
            Intent intent = new Intent(this, ViewPagerActivity.class);
            startActivity(intent);
        }
    }    
    
    //リソース→ビットマップ
    private Bitmap res2bmp(Context context, int resID) {
        return BitmapFactory.decodeResource(
            context.getResources(), resID);
    }
}