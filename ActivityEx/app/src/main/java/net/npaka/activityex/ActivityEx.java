package net.npaka.activityex;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

//アクティビティの起動
public class ActivityEx extends Activity 
    implements View.OnClickListener {
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static String TAG_WEB   = "web";
    private final static String TAG_MAP   = "map";
    private final static String TAG_CALL  = "call";
    private final static String TAG_DIAL  = "dial";
    private final static String TAG_SETUP = "setup";
    private final static String TAG_HELLO = "hello";
        
    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //レイアウトの生成
        LinearLayout layout = new LinearLayout(this);
        layout.setBackgroundColor(Color.WHITE);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);            

        //ボタンの生成
        layout.addView(makeButton("Webページの表示:npaka.net", TAG_WEB));
        layout.addView(makeButton("地図の表示:Tokyo", TAG_MAP));
        layout.addView(makeButton("通話の開始 tel:117", TAG_CALL));
        layout.addView(makeButton("電話の表示", TAG_DIAL));
        layout.addView(makeButton("設定画面の表示", TAG_SETUP));
        layout.addView(makeButton("HelloWorldの起動", TAG_HELLO));
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
        
        try {
            //Webページの表示(1)
            if (TAG_WEB.equals(tag)) {
                Intent intent = new Intent("android.intent.action.VIEW",
                    Uri.parse("http://npaka.net"));
                startActivity(intent);
            } 
            //地図の表示(2)
            else if (TAG_MAP.equals(tag)) {
                Intent intent = new Intent("android.intent.action.VIEW",
                    Uri.parse("geo:0,0?q=Tokyo"));
                startActivity(intent);
            }
            //通話の開始(3)
            else if (TAG_CALL.equals(tag)) {
                Intent intent = new Intent("android.intent.action.CALL",
                    Uri.parse("tel:117"));
                startActivity(intent);
            } 
            //電話の表示(4)
            else if (TAG_DIAL.equals(tag)) {
                Intent intent = new Intent("android.intent.action.DIAL",
                    Uri.parse("tel:117"));
                startActivity(intent);
            }
            //設定画面の表示(5)
            else if (TAG_SETUP.equals(tag)) {
                Intent intent = new Intent("android.settings.SETTINGS");
                startActivity(intent);
            }
            //HelloWorldの起動(6)
            else if (TAG_HELLO.equals(tag)) {
                startActivity(this, "net.npaka.helloworld",
                    "net.npaka.helloworld.HelloWorld");
            }
        } catch (Exception e) {
            toast(e.getMessage());
        }
    }
    
    //自作アクティビティの起動(6)
    private static void startActivity(Activity activity,
        String packageName, String className) throws Exception {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName(packageName, className));
        intent.removeCategory(Intent.CATEGORY_DEFAULT);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }
    
    //トーストの表示
    private void toast(String text) {
        if (text == null) text = "";
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}