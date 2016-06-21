package nemo.com.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import nemo.com.mobilesafe.ui.SettingItemView;

/**
 * Created by nemo on 16-6-15.
 */
public class Step02Activity extends BaseStepActivity implements View.OnClickListener {
    private SettingItemView sivStep02 = null;
    public static final String SIMSERIALNUMBER = "simSerialNumber";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step02_activity);

        sivStep02 = (SettingItemView) findViewById(R.id.siv_step02);
        sivStep02.setOnClickListener(this);

        String simSerialNumber = sp.getString(SIMSERIALNUMBER, "");

        if(!TextUtils.isEmpty(simSerialNumber)) {
            sivStep02.setStatus(true);
        } else {
            sivStep02.setStatus(false);
        }
    }

    @Override
    public Intent showNext() {
        return new Intent(Step02Activity.this, Step03Activity.class);
    }

    @Override
    public Intent showPrevious() {
        return new Intent(Step02Activity.this, Step01Activity.class);
    }


    @Override
    public void onClick(View v) {
        SharedPreferences.Editor editor = sp.edit();

        if(sivStep02.checkStatus()) {
            sivStep02.setStatus(false);
            editor.putString(SIMSERIALNUMBER, null);
        } else {
            sivStep02.setStatus(true);
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            String simSerialNumber = tm.getSimSerialNumber();

            if(TextUtils.isEmpty(simSerialNumber)) {
                simSerialNumber = "000000000000";
            }

            editor.putString(SIMSERIALNUMBER, simSerialNumber);
        }
        editor.commit();
    }
}
