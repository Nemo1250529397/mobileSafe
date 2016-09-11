package nemo.com.mobilesafe;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by nemo on 16-9-11.
 */
public class CleanCacheActivity extends Activity {
    private ProgressBar pb;
    private TextView tvScanStatus;
    private PackageManager pm;
    private LinearLayout llContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cache);
        tvScanStatus = (TextView) findViewById(R.id.tv_scan_status);
        pb = (ProgressBar) findViewById(R.id.pb);
        llContainer = (LinearLayout) findViewById(R.id.ll_container);
        scanCache();
    }

    private void scanCache() {
        pm = getPackageManager();
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        Method getPackageSizeInfoMethod = null;
                        Method[] methods = PackageManager.class.getMethods();
                        for(Method method : methods) {
                            if("getPackageSizeInfo".equals(method.getName())) {
                                getPackageSizeInfoMethod = method;
                            }
                        }
                        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
                        pb.setMax(packageInfos.size());
                        int progress = 0;
                        for(PackageInfo packageInfo : packageInfos) {
                            try {
                                getPackageSizeInfoMethod.invoke(pm, packageInfo.packageName, new MyDataObserver());
                                SystemClock.sleep(50);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            progress++;
                            pb.setProgress(progress);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvScanStatus.setText("正在扫描......");
                            }
                        });
                    }
                }
        ).start();
    }

    private class MyDataObserver extends IPackageStatsObserver.Stub {

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            final long cache = pStats.cacheSize;
            long code = pStats.codeSize;
            long data = pStats.dataSize;
            final String packageName = pStats.packageName;
            final ApplicationInfo applicationInfo;
            try {
                applicationInfo = pm.getApplicationInfo(packageName, 0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvScanStatus.setText("正在扫描程序： " + applicationInfo.loadLabel(pm));
                        if(cache > 0) {
                            View view = View.inflate(getApplicationContext(), R.layout.list_item_cacheinfo, null);
                            TextView tvCache = (TextView) view.findViewById(R.id.tv_cache_size);
                            tvCache.setText("缓存文件大小： " + Formatter.formatFileSize(getApplicationContext(), cache));
                            TextView tvName = (TextView) view.findViewById(R.id.tv_app_name);
                            tvName.setText(applicationInfo.loadLabel(pm));
                            ImageView ivDelete = (ImageView) view.findViewById(R.id.iv_delete);
                            ivDelete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        Method method = PackageManager.class.getMethod("deleteApplicationCacheFiles", String.class, IPackageDataObserver.class);
                                        method.invoke(pm, packageName, new MyPackDataObserver());
                                    } catch (NoSuchMethodException e) {
                                        e.printStackTrace();
                                    } catch (InvocationTargetException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            llContainer.addView(view, 0);
                        }
                    }
                });
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private class MyPackDataObserver extends IPackageDataObserver.Stub {

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
            System.out.println(packageName + succeeded);
        }
    }

    public void clearAll(View view) {
        Method[] methods = PackageManager.class.getMethods();
        for(Method method : methods) {
            if("freeStorageAndNotify".equals(method.getName())) {
                try {
                    method.invoke(pm, Integer.MAX_VALUE, new MyPackDataObserver());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }
}
