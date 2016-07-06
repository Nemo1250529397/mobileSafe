package nemo.com.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.view.WindowManager;

/**
 * Created by nemo on 16-7-6.
 */
public class AddressService extends Service {
    private TelephonyManager tm = null;
    private MyPhoneStateListener myPhoneStateListener = null;
    private WindowManager wm = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tm = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
        myPhoneStateListener = new MyPhoneStateListener();



        tm.listen(myPhoneStateListener  , PhoneStateListener.LISTEN_CALL_STATE);

        wm = (WindowManager) getSystemService(this.WINDOW_SERVICE);
        /**
         * init the windowManager value
         */

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE: {

                    break;
                }
                case TelephonyManager.CALL_STATE_RINGING: {
                    myToast(incomingNumber);
                    break;
                }
                case TelephonyManager.CALL_STATE_OFFHOOK: {
                    break;
                }
            }
        }
    }

    private void myToast(String telephoneName) {

    }
}
