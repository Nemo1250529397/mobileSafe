package nemo.com.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import java.util.Formatter;
import java.util.Timer;
import java.util.TimerTask;

import nemo.com.mobilesafe.R;
import nemo.com.mobilesafe.receiver.MobileSafeAppWidgetProvider;
import nemo.com.mobilesafe.utils.SystemInfoUtils;

/**
 * Created by nemo on 16-8-19.
 */
public class UpdateWidgetService extends Service {
    private ScreenOffReceiver offreceiver;
    private ScreenOnReceiver onreceiver;

    private Timer timer;
    private TimerTask timerTask;

    private AppWidgetManager awm;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class ScreenOffReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopTimer();
        }
    }

    private class ScreenOnReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            startTimer();
        }
    }

    @Override
    public void onCreate() {
        onreceiver = new ScreenOnReceiver();
        offreceiver = new ScreenOffReceiver();
        registerReceiver(onreceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(offreceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        awm = AppWidgetManager.getInstance(this);
        startTimer();
        super.onCreate();
    }

    private void startTimer() {
        if(timer == null && timerTask == null) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    ComponentName provider = new ComponentName(UpdateWidgetService.this, MobileSafeAppWidgetProvider.class);
                    RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
                    views.setTextViewText(R.id.process_count, "正在运行的process : " +
                            SystemInfoUtils.getRunningProcessCount(getApplicationContext()) + "个");
                    views.setTextViewText(R.id.process_memory, "正在运行的memory : " +
                            android.text.format.Formatter.formatFileSize(getApplicationContext(),
                                    SystemInfoUtils.getAvailableMemory(getApplicationContext())));

                    Intent intent = new Intent();
                    intent.setAction("nemo.com.mobilesafe.receiver.killall");

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
                    awm.updateAppWidget(provider, views);
                }
            };
            timer.schedule(timerTask, 0, 3000);
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(offreceiver);
        offreceiver = null;
        unregisterReceiver(onreceiver);
        onreceiver = null;
        stopTimer();
        super.onDestroy();
    }

    private void stopTimer() {
        if(timerTask != null && timer != null) {
            timer.cancel();
            timerTask.cancel();
            timer = null;
            timerTask = null;
        }
    }
}
