package nemo.com.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;

import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import nemo.com.mobilesafe.R;
import nemo.com.mobilesafe.utils.TelephoneNumberQueryUtils;

/**
 * Created by nemo on 16-7-6.
 */
public class AddressService extends Service {
    private TelephonyManager tm = null;
    private MyPhoneStateListener myPhoneStateListener = null;
    private WindowManager wm = null;
    private View view = null;
    private MyReceiver myReceiver = null;

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String telephoneNumber = getResultData();
            myToast(telephoneNumber);
        }
    }

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

        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(myReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE: {
                    if(view != null) {
                        wm.removeView(view);
                        view = null;
                    }
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
        view = View.inflate(this, R.layout.address_show, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_address);
        String location = TelephoneNumberQueryUtils.dividleQueryLocation(this, telephoneName);
        textView.setText(location);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;

        wm.addView(view, params);
    }
}
