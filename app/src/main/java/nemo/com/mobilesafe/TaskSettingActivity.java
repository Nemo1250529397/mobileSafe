package nemo.com.mobilesafe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import nemo.com.mobilesafe.utils.ServiceUtils;

/**
 * Created by nemo on 16-8-14.
 */
public class TaskSettingActivity extends Activity {
    private CheckBox cbShowSystem;
    private CheckBox cbAutoClean;
    private SharedPreferences sp;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_setting_activity);
        sp = getSharedPreferences("config", Context.MODE_PRIVATE);

        cbShowSystem = (CheckBox) findViewById(R.id.cb_show_system);
        cbAutoClean = (CheckBox) findViewById(R.id.cb_auto_clean);
        cbShowSystem.setChecked(sp.getBoolean("showsystem", false));
        cbShowSystem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("showsystem", isChecked);
                editor.commit();
            }
        });

        cbAutoClean.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(TaskSettingActivity.this, AutoCloseable.class);
                if(isChecked) {
                    startService(intent);
                } else {
                    stopService(intent);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        boolean running = ServiceUtils.isServiceRunning(this, "com.itheima.mobilesafe.service.AutoCleanService");
        cbAutoClean.setChecked(running);
        super.onStart();
    }
}
