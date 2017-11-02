package net.npaka.homeex;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

//ホームスクリーン
public class HomeEx extends AppCompatActivity
    implements AdapterView.OnItemClickListener { 
    private final static int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private ArrayList<AppInfo> appInfos;   //アプリ情報群
    private BroadcastReceiver  appReceiver;//アプリ登録解除レシーバー
    private GridView           gridView;   //グリッドビュー
    private int                iconSize;   //アイコンサイズ
        
    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        //アイコンサイズの計算
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        iconSize = (int)(48.0f*metrics.density);
        
        //レイアウトの生成
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout); 

        //グリッドビューの生成
        gridView = new GridView(this);
        gridView.setBackgroundColor(Color.argb(200, 0, 0, 0));
        gridView.setLayoutParams(new LinearLayout.LayoutParams(MP, MP));
        gridView.setNumColumns(4);
        gridView.setOnItemClickListener(this);
        gridView.setVisibility(View.INVISIBLE);
        layout.addView(gridView);
        
        //アプリ情報群の読み込み
        appInfos = AppInfo.readAppInfos(this);
        gridView.setAdapter(new GridAdapter());

        //アプリ登録解除レシーバーの開始(3)
        IntentFilter filter =
            new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        appReceiver = new AppReceiver();
        registerReceiver(appReceiver, filter);
    }   

    //グリッドアイテムのクリックイベント処理
    public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
        try {
            //アクティビティの起動(5)
            AppInfo appInfo = appInfos.get(pos);
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setComponent(new ComponentName(
                appInfo.packageName, appInfo.className));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(intent);
        } catch (Exception e) {
        }
    }    

    //アクティビティ解放時に呼ばれる
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        //アプリ登録解除レシーバーの解放
        unregisterReceiver(appReceiver);
    }    
    
    //バックキーの無効化(4)
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                gridView.setVisibility(View.INVISIBLE);
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    //グリッドアダプタの生成
    public class GridAdapter extends BaseAdapter {
        //要素数の取得
        @Override
        public int getCount() {
            return appInfos.size();
        }
        
        //要素の取得
        @Override
        public Object getItem(int pos) {
            return appInfos.get(pos);
        }
        
        //要素IDの取得
        @Override
        public long getItemId(int pos) {
            return pos;
        }   

        //ビューの取得
        public View getView(int pos, View convertView, ViewGroup parent) {
            Context context = HomeEx.this;
            AppInfo appInfo = appInfos.get(pos);
            
            //レイアウトの生成
            if (convertView == null) {
                //レイアウト
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);
                layout.setPadding(4, 4, 4, 4);
                convertView = layout;
            
                //アイコン
                ImageView imageView=new ImageView(context);
                imageView.setTag("icon");
                imageView.setLayoutParams(
                    new LinearLayout.LayoutParams(iconSize, iconSize));
                layout.addView(imageView);
            
                //テキストビュー
                TextView textView = new TextView(context);
                textView.setTag("name");
                textView.setLines(2);
                textView.setTextSize(12);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                textView.setTextColor(Color.WHITE);
                layout.addView(textView);
            }
            
            //値の指定
            ImageView imageView = (ImageView)
                convertView.findViewWithTag("icon");
            imageView.setImageDrawable(appInfo.icon);
            TextView textView = (TextView)
            convertView.findViewWithTag("name");
            textView.setText(appInfo.name);
            return convertView;
        }
    }

    //アプリ登録解除レシーバー(3)
    private class AppReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //アプリリストの読み込み
            appInfos = AppInfo.readAppInfos(HomeEx.this);
            gridView.setAdapter(new GridAdapter());
        }
    }
    
    //オプションメニューの生成
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item0 = menu.add(0, 0, 0, "設定");
        item0.setIcon(android.R.drawable.ic_menu_preferences);
        MenuItem item1 = menu.add(0, 1, 0, "壁紙");
        item1.setIcon(android.R.drawable.ic_menu_gallery);
        MenuItem item2 = menu.add(0, 2, 0, "ランチャー");
        item2.setIcon(android.R.drawable.ic_menu_today);
        return true;
    } 
    
    //オプションメニュー押下時に呼ばれる
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == 0) {
            Intent intent = new Intent("android.settings.SETTINGS");
            startActivity(intent);
        } else if (itemId == 1) {
            //壁紙の変更(6)
            Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
            startActivity(Intent.createChooser(intent, "壁紙を選択"));
        } else if (itemId == 2) {
            gridView.setVisibility(View.VISIBLE);
        }
        return true;
    }
}