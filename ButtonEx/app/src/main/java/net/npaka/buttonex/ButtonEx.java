package net.npaka.buttonex;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

//ボタンとダイアログ
public class ButtonEx extends Activity implements
    View.OnClickListener {//(2)
    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final static String TAG_MESSAGE = "0";
    private final static String TAG_YESNO   = "1";
    private final static String TAG_TEXT    = "2";
    private final static String TAG_IMAGE   = "3";

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

        //ボタンの生成(1)
        layout.addView(makeButton("メッセージダイアログの表示", TAG_MESSAGE));
        layout.addView(makeButton("Yes/Noダイアログの表示", TAG_YESNO));
        layout.addView(makeButton("テキスト入力ダイアログの表示", TAG_TEXT));
        layout.addView(makeButton(res2bmp(this, R.drawable.sample), TAG_IMAGE));
    }

    //ボタンの生成(1)
    private Button makeButton(String text, String tag) {
        Button button = new Button(this);
        button.setText(text);
        button.setTag(tag);
        button.setOnClickListener(this);//(2)
        button.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        return button;
    }

    //イメージボタンの生成(3)
    private ImageButton makeButton(Bitmap bmp, String tag) {
        ImageButton button = new ImageButton(this);
        button.setTag(tag);
        button.setOnClickListener(this);
        button.setImageBitmap(bmp);
        button.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        return button;
    }

    //リソース→ビットマップ
    public Bitmap res2bmp(Context context, int resID) {
        return BitmapFactory.decodeResource(
            context.getResources(), resID);
    }

    //ボタン押下時に呼ばれる(2)
    public void onClick(View view) {
        String tag = (String)view.getTag();

        if (TAG_MESSAGE.equals(tag)) {
            MessageDialog.show(this, "メッセージダイアログ", "ボタンを押した");
        } else if (TAG_YESNO.equals(tag)) {
            YesNoDialog.show(this, "Yes/Noダイアログ", "Yes/Noを選択");
        } else if (TAG_TEXT.equals(tag)) {
            TextDialog.show(this, "テキスト入力ダイアログ", "テキストを入力");
        } else if (TAG_IMAGE.equals(tag)) {
            MessageDialog.show(this, "", "イメージボタンを押した ");
        }
    }

    //メッセージダイアログの定義(4)
    public static class MessageDialog extends DialogFragment {
        //ダイアログの表示(5)
        public static void show(
            Activity activity, String title, String text) {
            MessageDialog f = new MessageDialog();
            Bundle args = new Bundle();
            args.putString("title", title);
            args.putString("text", text);
            f.setArguments(args);
            f.show(activity.getFragmentManager(), "messageDialog");
        }

        //ダイアログの生成(6)
        @Override
        public Dialog onCreateDialog(Bundle bundle) {
            AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
            ad.setTitle(getArguments().getString("title"));
            ad.setMessage(getArguments().getString("text"));
            ad.setPositiveButton("OK", null);
            return ad.create();
        }
    }

    //Yes/Noダイアログの定義
    public static class YesNoDialog extends DialogFragment {
        //ダイアログの表示
        public static void show(
            Activity activity, String title, String text) {
            YesNoDialog f = new YesNoDialog();
            Bundle args = new Bundle();
            args.putString("title", title);
            args.putString("text", text);
            f.setArguments(args);
            f.show(activity.getFragmentManager(), "yesNoDialog");
        }

        //Yes/Noダイアログの生成(7)
        @Override
        public Dialog onCreateDialog(Bundle bundle) {
            //リスナーの生成
            DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                //ダイアログボタン押下時に呼ばれる
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        MessageDialog.show(getActivity(), "", "YES");
                    } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                        MessageDialog.show(getActivity(), "", "NO");
                    }
                }
            };

            //Yes/Noダイアログの生成
            AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
            ad.setTitle(getArguments().getString("title"));
            ad.setMessage(getArguments().getString("text"));
            ad.setPositiveButton("Yes", listener);
            ad.setNegativeButton("No", listener);
            return ad.create();
        }
    }

    //テキスト入力ダイアログの定義
    public static class TextDialog extends DialogFragment {
        private EditText editText;

        //ダイアログの表示
        public static void show(
            Activity activity, String title, String text) {
            TextDialog f = new TextDialog();
            Bundle args = new Bundle();
            args.putString("title", title);
            args.putString("text", text);
            f.setArguments(args);
            f.show(activity.getFragmentManager(), "textDialog");
        }

        //テキスト入力ダイアログの生成(8)
        @Override
        public Dialog onCreateDialog(Bundle bundle) {
            //リスナーの生成
            DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                //ダイアログボタン押下時に呼ばれる
                public void onClick(DialogInterface dialog, int which) {
                    MessageDialog.show(getActivity(), "",
                        editText.getText().toString());
                }
            };

            //エディットテキストの生成
            editText = new EditText(getActivity());

            //テキスト入力ダイアログの生成
            AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
            ad.setTitle(getArguments().getString("title"));
            ad.setMessage(getArguments().getString("text"));
            ad.setView(editText);
            ad.setPositiveButton("OK", listener);

            //フラグメントの状態復帰
            if (bundle != null) editText.setText(bundle.getString("editText", ""));
            return ad.create();
        }

        //フラグメントの状態保存(8)
        @Override
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putString("editText", editText.getText().toString());
        }
    }
}