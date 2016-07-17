package nemo.com.mobilesafe;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import java.util.List;

import nemo.com.mobilesafe.service.AddressService;
import nemo.com.mobilesafe.service.CallSmsSafeService;
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
        }

        editor.commit();
    }
}
