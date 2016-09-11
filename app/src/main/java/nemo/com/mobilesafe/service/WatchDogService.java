package nemo.com.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import java.util.List;

import nemo.com.mobilesafe.EnterPwdActivity;
import nemo.com.mobilesafe.db.dao.AppLockDao;

/**
 * Created by nemo on 16-8-29.
 */
public class WatchDogService extends Service {
    private ActivityManager activityManager;
    private boolean flag;
    private AppLockDao dao;
    private InnerReceiver innerReceiver;
    private String stopProtectPacakageName;
    private ScreenOffReceiver screenOffReceiver;
    private DataChangeReceiver dataChangeReceiver;

    private List<String> protectPackageName;
    private Intent intent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            stopProtectPacakageName = intent.getStringExtra("packagename");
        }
    }

    private class ScreenOffReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            stopProtectPacakageName = null;
        }
    }

    private class DataChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            protectPackageName = dao.findAll();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        screenOffReceiver = new ScreenOffReceiver();
        registerReceiver(screenOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        innerReceiver = new InnerReceiver();
        registerReceiver(innerReceiver, new IntentFilter("nemo.com.mobilesafe.service.tempstop"));
        dataChangeReceiver = new DataChangeReceiver();
        registerReceiver(dataChangeReceiver, new IntentFilter("nemo.com.mobilesafe.service.applockchange"));

        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        dao = new AppLockDao(this);
        protectPackageName = dao.findAll();
        flag = true;
        intent = new Intent(getApplicationContext(), EnterPwdActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        new Thread(){
            @Override
            public void run() {
                while(flag) {
                    List<ActivityManager.RunningTaskInfo> infos = activityManager.getRunningTasks(1);
                    String packagename = infos.get(0).topActivity.getPackageName();
                    if(protectPackageName.contains(packagename)) {
                        if(!packagename.equals(stopProtectPacakageName)) {
                            intent.putExtra("packagename", packagename);
                            startActivity(intent);
                        }
                    }

                    SystemClock.sleep(20);
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        flag = false;
        unregisterReceiver(innerReceiver);
        innerReceiver = null;
        unregisterReceiver(screenOffReceiver);
        screenOffReceiver = null;
        unregisterReceiver(dataChangeReceiver);
        dataChangeReceiver = null;
    }
}
