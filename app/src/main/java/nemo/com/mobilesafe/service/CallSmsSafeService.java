package nemo.com.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import nemo.com.mobilesafe.CallSmsSafeActivity;
import nemo.com.mobilesafe.db.dao.BlackNumberDao;

/**
 * Created by nemo on 16-7-17.
 */
public class CallSmsSafeService extends Service {
    private BlackNumberDao dao;
    private TelephonyManager tm;
    private MyListener myListener;
    private InnerSmsReceiver receiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dao = new BlackNumberDao(this);
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        myListener = new MyListener();
        tm.listen(myListener, PhoneStateListener.LISTEN_CALL_STATE);

        receiver = new InnerSmsReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        receiver = null;
        tm.listen(myListener, PhoneStateListener.LISTEN_NONE);
    }

    private class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING: {
                    String result = dao.findMode(incomingNumber);
                    if("2".equals(result) || "3".equals(result)) {
                        endCall();
                    }
                    break;
                }
            }
        }
    }

    public void endCall() {
        try {
            Class clazz = CallSmsSafeActivity.class.getClassLoader().loadClass("android.os.ServiceManager");
            Method method = clazz.getDeclaredMethod("getService", String.class);

            IBinder ibinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
            ITelephony.Stub.asInterface(ibinder).endCall();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private class InnerSmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for(Object obj : objs) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);

                String sender = smsMessage.getOriginatingAddress();
                String result = dao.findMode(sender);
                if("1".equals(result) || "2".equals(result)) {
                    abortBroadcast();
                }

//                String body = smsMessage.getMessageBody();
//                if(body.contains("xxx")) {
//                    abortBroadcast();
//                }
            }
        }
    }




}
