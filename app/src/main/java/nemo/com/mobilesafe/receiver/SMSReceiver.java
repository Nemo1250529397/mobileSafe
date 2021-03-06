package nemo.com.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import nemo.com.mobilesafe.R;
import nemo.com.mobilesafe.service.GPSService;

/**
 * Created by nemo on 16-6-25.
 */
public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = "SMSReceiver";
    private SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        Object []objects = (Object[]) intent.getExtras().get("pdus");
        sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);

        for(Object object:objects) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);

            String sender = sms.getOriginatingAddress();
            String safeNumber = sharedPreferences.getString("safeNumber", null);

            if(sender.contains(safeNumber)) {
                String smsBody = sms.getMessageBody();
                if("#*location*#".equals(smsBody)) {
                    startLocation(context, sender);
                } else if("#*alarm*#".equals(smsBody)) {
                    startAlarm(context);
                } else if("#*wipedata*#".equals(smsBody)) {

                } else if("#*lockscree*#".equals(smsBody)) {

                }
                abortBroadcast();
            }
        }
    }

    public void startLocation(Context context, String sender) {
        Log.i(TAG, "GPS location!");
        Intent i = new Intent(context, GPSService.class);
        context.startService(i);
        sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String lastlocation = sharedPreferences.getString("lastlocation", "");
        if(TextUtils.isEmpty(lastlocation)) {
            SmsManager.getDefault().sendTextMessage(sender, null, "geting location ......", null, null);
        } else if(!TextUtils.isEmpty(sender)){
            SmsManager.getDefault().sendTextMessage(sender, null, lastlocation, null, null);
        } else {
            Toast.makeText(context, "sender message is null!", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(lastlocation)) {
            Log.i(TAG, "geting location ......");
        } else {
            Log.i(TAG, lastlocation);
            Toast.makeText(context, lastlocation, Toast.LENGTH_LONG).show();
        }
    }

    public static void startAlarm(Context context) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
        mediaPlayer.setLooping(false);
        mediaPlayer.setVolume(1.0f, 1.0f);
        mediaPlayer.start();
    }

    public void startWipeDate() {

    }
}
