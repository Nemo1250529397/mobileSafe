package nemo.com.mobilesafe.engine;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

import nemo.com.mobilesafe.domain.AppInfo;

/**
 * Created by nemo on 16-7-24.
 */
public class AppInfoProvider {
    public static List<AppInfo> getAppInfos(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);

        List<AppInfo> appInfos = new ArrayList<AppInfo>();
        for(PackageInfo packageInfo : packageInfos) {
            AppInfo appInfo = new AppInfo();
            String packageName = packageInfo.packageName;
            Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
            String name = packageInfo.applicationInfo.loadLabel(pm).toString();
            int flags = packageInfo.applicationInfo.flags;

            if((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                appInfo.setUserApp(true);
            } else {
                appInfo.setUserApp(false);
            }

            if((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
                appInfo.setInRom(true);
            } else {
                appInfo.setInRom(false);
            }
            appInfo.setPackageName(packageName);
            appInfo.setIcon(icon);
            appInfo.setName(name);
            appInfos.add(appInfo);
        }
        return appInfos;
    }
}
