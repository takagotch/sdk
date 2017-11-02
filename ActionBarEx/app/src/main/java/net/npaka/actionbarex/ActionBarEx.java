package net.npaka.actionbarex;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

//アクションバー
public class ActionBarEx extends AppCompatActivity {//(1)
    //メニューアイテムID
    private static final int MENU_ITEM0 = 0;
    private static final int MENU_ITEM1 = 1;
    private static final int MENU_ITEM2 = 2;
    private static final int MENU_ITEM3 = 3;

    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        //レイアウトの生成
        LinearLayout layout = new LinearLayout(this);
        layout.setBackgroundColor(Color.WHITE);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);
    }

    //オプションメニューの生成(2)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        //オプションメニューへのアイテム0の追加(3)
        MenuItem item0 = menu.add(0, MENU_ITEM0, 0, "アイテム0");
        item0.setIcon(android.R.drawable.ic_menu_camera);
        
        //オプションメニューへのアイテム1の追加(3)
        MenuItem item1 = menu.add(0, MENU_ITEM1, 0, "アイテム1");
        item1.setIcon(android.R.drawable.ic_menu_call);
        
        //アクションバーへのメニューアイテム2の追加(3)
        MenuItem item2 = menu.add(0, MENU_ITEM2, 0, "アイテム2");
        item2.setIcon(android.R.drawable.ic_menu_add);
        item2.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        //アクションバーへのメニューアイテム3の追加(3)
        MenuItem item3 = menu.add(0, MENU_ITEM3, 0, "アイテム3");
        item3.setIcon(android.R.drawable.ic_menu_delete);
        item3.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        
        return true;
    }    

    //メニューアイテム選択イベントの処理(4)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == MENU_ITEM0) {
            toast("アイテム0を押した");
        } else if (itemId == MENU_ITEM1) {
            toast("アイテム1を押した");
        } else if (itemId == MENU_ITEM2) {
            toast("アイテム2を押した");
        } else if (itemId == MENU_ITEM3) {
            toast("アイテム3を押した");
        }
        return true;
    }    
    
    //トーストの表示
    private void toast(String text) {
        if (text == null) text = "";
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}