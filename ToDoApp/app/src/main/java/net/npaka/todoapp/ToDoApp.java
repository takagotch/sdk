package net.npaka.todoapp;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//ToDoアプリ
public class ToDoApp extends AppCompatActivity {
    //定数
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static int REQUEST_EDIT = 0;
    private final static int MENU_ITEM0 = 0;

    //UI
    private ListView listView;//リストビュー
    private ArrayList<ToDoItem> items;//要素群

    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.setTitle("ToDoアプリ");

        //要素群の読み込み
        items = new ArrayList<ToDoItem>();
        loadItems();

        //リストビューの生成(1)
        listView = new ListView(this);
        listView.setScrollingCacheEnabled(false);
        listView.setAdapter(new MyAdapter());
        setContentView(listView);
    }

    //アクティビティ停止時に呼ばれる
    @Override
    public void onStop() {
        super.onStop();

        //要素群の書き込み
        saveItems();
    }

    //オプションメニューの生成
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        //追加アイテムの追加
        MenuItem item0 = menu.add(0, MENU_ITEM0, 0, "追加");
        item0.setIcon(android.R.drawable.ic_menu_add);
        item0.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }    

    //メニューアイテム選択イベントの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == MENU_ITEM0) {
            startEditActivity(null);
        }
        return true;
    }

    //編集アクティビティの起動(3)
    private void startEditActivity(ToDoItem item) {
        Intent intent = new Intent(this, EditActivity.class);
        if (item == null) {
            intent.putExtra("pos", -1);
            intent.putExtra("title", "");
            intent.putExtra("checked", false);
        } else {
            intent.putExtra("pos", items.indexOf(item));
            intent.putExtra("title", item.title);
            intent.putExtra("checked", item.checked);
        }
        startActivityForResult(intent, REQUEST_EDIT);
    }

    //編集アクティビティ呼び出し結果の取得(4)
    @Override
    protected void onActivityResult(int requestCode,
        int resultCode, Intent intent) {
        if (requestCode == REQUEST_EDIT &&
            resultCode == RESULT_OK) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String result = extras.getString("result");
                int pos = extras.getInt("pos");
                String title = extras.getString("title");
                boolean checked = extras.getBoolean("checked");

                //追加
                if (result.equals("add")) {
                    ToDoItem item = new ToDoItem();
                    item.title = title;
                    item.checked = checked;
                    items.add(item);
                }
                //編集
                else if (result.equals("edit")) {
                    ToDoItem item = items.get(pos);
                    item.title = title;
                    item.checked = checked;
                }
                //削除
                else if (result.equals("delete")) {
                    items.remove(pos);
                }

                //リストビューの更新(1)
                ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
            }
        }
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
        public ToDoItem getItem(int pos) {
            return items.get(pos);
        }

        //要素IDの取得
        @Override
        public long getItemId(int pos) {
            return pos;
        }

        //セルのビューの生成(2)
        @Override
        public View getView(int pos,View view,ViewGroup parent) {
            ToDoItem item = items.get(pos);

            //レイアウトの生成
            if (view == null) {
                //レイアウトの生成
                LinearLayout layout = new LinearLayout(ToDoApp.this);
                layout.setBackgroundColor(Color.WHITE);
                layout.setPadding(
                    Util.dp2px(ToDoApp.this, 10),
                    Util.dp2px(ToDoApp.this, 10),
                    Util.dp2px(ToDoApp.this, 10),
                    Util.dp2px(ToDoApp.this, 10));
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View sender) {
                        //編集アクティビティの起動
                        int pos = Integer.parseInt((String)sender.getTag());
                        ToDoItem item = items.get(pos);
                        startEditActivity(item);
                    }
                });

                //チェックボックスの追加
                CheckBox checkBox = new CheckBox(ToDoApp.this);
                checkBox.setTextColor(Color.BLACK);
                checkBox.setId(R.id.cell_checkbox);
                checkBox.setChecked(true);
                checkBox.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View sender) {
                        //ToDo情報の更新
                        int pos = Integer.parseInt((String)sender.getTag());
                        ToDoItem item = items.get(pos);
                        item.checked = ((CheckBox)sender).isChecked();
                    }
                });
                layout.addView(checkBox);
                view = layout;
            }

            //値の指定
            CheckBox checkBox = (CheckBox)view.findViewById(R.id.cell_checkbox);
            checkBox.setChecked(item.checked);
            checkBox.setText(item.title);
            checkBox.setTag(""+pos);
            view.setTag(""+pos);
            return view;
        }
    }

    //要素群の書き込み
    private void saveItems() {
        //ArrayListをJSONに変換
        String json = list2json(items);

        //プリファレンスへの書き込み
        SharedPreferences pref = getSharedPreferences(
            "ToDoApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("items", json);
        editor.commit();
    }

    //要素群の読み込み
    private void loadItems() {
        //プリファレンスからの読み込み
        SharedPreferences pref = getSharedPreferences(
            "ToDoApp", MODE_PRIVATE);
        String json = pref.getString("items","");

        //JSONをArrayListに変換
        items = items2list(json);
    }

    //ArrayListをJSONに変換(5)
    private String list2json(ArrayList<ToDoItem> items) {
        try {
            JSONArray array = new JSONArray();
            for (ToDoItem item : items) {
                JSONObject obj = new JSONObject();
                obj.put("title", item.title);
                obj.put("checked", item.checked);
                array.put(obj);
            }
            return array.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    //JSONをArrayListに変換(6)
    private ArrayList<ToDoItem> items2list(String json) {
        ArrayList<ToDoItem> items = new ArrayList<ToDoItem>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                ToDoItem item = new ToDoItem();
                item.title = obj.getString("title");
                item.checked = obj.getBoolean("checked");
                items.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;
    }
}