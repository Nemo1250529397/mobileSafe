package nemo.com.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import nemo.com.mobilesafe.BaseStepActivity;
import nemo.com.mobilesafe.Step02Activity;

/**
 * Created by nemo on 16-6-21.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    private SharedPreferences sp = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        sp = context.getSharedPreferences(BaseStepActivity.CONFIG, Context.MODE_PRIVATE);
        String saveSimSerialNumber = sp.getString(Step02Activity.SIMSERIALNUMBER, "");

        if(TextUtils.isEmpty(saveSimSerialNumber)) {
            Toast.makeText(context, "It has not been enabled the saved the sim serial number!", Toast.LENGTH_SHORT).show();
        } else {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
            String nowSimSerialNumber = tm.getSimSerialNumber();

            if(TextUtils.isEmpty(nowSimSerialNumber)) {
                nowSimSerialNumber = "111111111111";
            }

            if(saveSimSerialNumber.equals(nowSimSerialNumber)) {
                Toast.makeText(context, "The sim card serial number is: " + nowSimSerialNumber, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "The sim card have been changed and the newest sim serial number is: " + nowSimSerialNumber,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
