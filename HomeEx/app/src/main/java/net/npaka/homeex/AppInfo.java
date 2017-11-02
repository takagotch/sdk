package net.npaka.homeex;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//アプリ情報
public class AppInfo {
    public String   name;       //名前
    public String   packageName;//パッケージ名
    public String   className;  //クラス名
    public Drawable icon;       //アイコン
    
    //アプリ情報群の読み込み
    public static ArrayList<AppInfo> readAppInfos(Context context) {
        //アプリ情報群の取得(1)
        PackageManager manager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = manager.queryIntentActivities(intent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));

        //アプリ情報群からの必要な情報の取得(2)
        ArrayList<AppInfo> appInfos = new ArrayList<AppInfo>();
        if (apps == null) return appInfos;
        for (int i = 0; i < apps.size(); i++) {
            AppInfo appInfo = new AppInfo();
            ResolveInfo info = apps.get(i);
            appInfo.name = (String)info.loadLabel(manager);
            appInfo.packageName = info.activityInfo.applicationInfo.packageName;
            appInfo.className = info.activityInfo.name;
            appInfo.icon = info.activityInfo.loadIcon(manager);
            appInfos.add(appInfo);
        }
        return appInfos;
    } 
}
