package nemo.com.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import nemo.com.mobilesafe.ui.SettingItemView;

/**
 * Created by nemo on 16-6-11.
 */
public class SettingsActivity extends Activity implements View.OnClickListener {
    private SettingItemView siv = null;
    private SharedPreferences sp = null;
    public final static String SETTINGS = "settings";
    public final static String AUTOUPDATE = "autoupdate";

    public void onCreate(Bundle savedInstatnceState) {
        super.onCreate(savedInstatnceState);
        setContentView(R.layout.settings_activity);

        sp = getSharedPreferences(SETTINGS, MODE_PRIVATE);

        siv = (SettingItemView) findViewById(R.id.settingitemview);

        siv.setOnClickListener(this);

        if(sp.getBoolean(AUTOUPDATE,false)) {
            siv.setStatus(true);
        } else {
            siv.setStatus(false);
        }
    }

    @Override
    public void onClick(View v) {
        SharedPreferences.Editor editor = sp.edit();

        if(siv.checkStatus()) {
            siv.setStatus(false);
            editor.putBoolean(AUTOUPDATE, false);
        } else {
            siv.setStatus(true);
            editor.putBoolean(AUTOUPDATE, true);
        }
        editor.commit();
    }
}
