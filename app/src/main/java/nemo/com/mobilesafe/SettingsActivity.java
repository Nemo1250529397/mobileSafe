package nemo.com.mobilesafe;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import nemo.com.mobilesafe.service.AddressService;
import nemo.com.mobilesafe.service.CallSmsSafeService;
import nemo.com.mobilesafe.service.WatchDogService;
import nemo.com.mobilesafe.ui.SettingCallView;
import nemo.com.mobilesafe.ui.SettingItemView;
import nemo.com.mobilesafe.utils.ServiceUtils;

/**
 * Created by nemo on 16-6-11.
 */
public class SettingsActivity extends Activity implements View.OnClickListener {
    private SettingItemView siv = null;
    private SettingCallView scv = null;

    private SharedPreferences sp = null;
    private Intent showAddress = null;
    public final static String SETTINGS = "settings";
    public final static String AUTOUPDATE = "autoupdate";
    public final static String LOCATIONQUERY = "locationquery";

    private SettingItemView siv_callsms_safe;
    private Intent callSmsSafeIntent;

    private SettingItemView siv_watchdog;
    private Intent watchDogIntent;

    public void onCreate(Bundle savedInstatnceState) {
        super.onCreate(savedInstatnceState);
        setContentView(R.layout.settings_activity);

        sp = getSharedPreferences(SETTINGS, MODE_PRIVATE);
        showAddress = new Intent(SettingsActivity.this, AddressService.class);

        siv = (SettingItemView) findViewById(R.id.settingitemview);
        scv = (SettingCallView) findViewById(R.id.settingcallview);

        siv.setOnClickListener(this);
        scv.setOnClickListener(this);

        if(sp.getBoolean(AUTOUPDATE,false)) {
            siv.setStatus(true);
        } else {
            siv.setStatus(false);
        }

        if(ServiceUtils.isServiceRunning(this, AddressService.class.getName())) {
            scv.setStatus(true);
        } else {
            scv.setStatus(false);
        }

        siv_callsms_safe = (SettingItemView) findViewById(R.id.siv_callsms_safe);
        callSmsSafeIntent = new Intent(SettingsActivity.this, CallSmsSafeService.class);

        if(ServiceUtils.isServiceRunning(this, CallSmsSafeService.class.getName())) {
            siv_callsms_safe.setStatus(true);
        } else {
            siv_callsms_safe.setStatus(false);
        }
        siv_callsms_safe.setOnClickListener(this);

        siv_watchdog = (SettingItemView) findViewById(R.id.siv_watchdog);
        siv_watchdog.setOnClickListener(this);
        watchDogIntent = new Intent(SettingsActivity.this, WatchDogService.class);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        showAddress = new Intent(this, AddressService.class);
        boolean isServiceRunning = ServiceUtils.isServiceRunning(
                SettingsActivity.this,
                AddressService.class.getName());

//        if(isServiceRunning){
//            //¼àÌýÀ´µçµÄ·þÎñÊÇ¿ªÆôµÄ
//            siv_show_address.setChecked(true);
//        }else{
//            siv_show_address.setChecked(false);
//        }


        boolean iscallSmsServiceRunning = ServiceUtils.isServiceRunning(
                SettingsActivity.this,
                CallSmsSafeService.class.getName());
        siv_callsms_safe.setStatus(iscallSmsServiceRunning);

        boolean iswatchdogServiceRunning = ServiceUtils.isServiceRunning(
                SettingsActivity.this,
                WatchDogService.class.getName());
        siv_watchdog.setStatus(iswatchdogServiceRunning);

    }

    @Override
    public void onClick(View v) {
        SharedPreferences.Editor editor = sp.edit();

        switch(v.getId()) {
            case R.id.settingitemview: {
                if(siv.checkStatus()) {
                    siv.setStatus(false);
                    editor.putBoolean(AUTOUPDATE, false);
                } else {
                    siv.setStatus(true);
                    editor.putBoolean(AUTOUPDATE, true);
                }
                break;
            }
            case R.id.settingcallview: {
                if(ServiceUtils.isServiceRunning(this, AddressService.class.getName())) {
                    scv.setStatus(false);
                    stopService(showAddress);
                } else {
                    scv.setStatus(true);
                    startService(showAddress);
                }
                break;
            }
            case R.id.siv_callsms_safe: {
                if(ServiceUtils.isServiceRunning(this, CallSmsSafeService.class.getName())) {
                    siv_callsms_safe.setStatus(false);
                    stopService(callSmsSafeIntent);
                } else {
                    siv_callsms_safe.setStatus(true);
                    startService(callSmsSafeIntent);
                    Toast.makeText(this, "Start Service of Call Sms Safe!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.siv_watchdog: {
                if(ServiceUtils.isServiceRunning(this, WatchDogService.class.getName())) {
                    siv_watchdog.setStatus(false);
                    stopService(watchDogIntent);
                } else {
                    siv_watchdog.setStatus(true);
                    startService(watchDogIntent);
                }
                break;
            }
        }

        editor.commit();
    }
}
