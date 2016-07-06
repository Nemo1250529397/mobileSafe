package nemo.com.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by nemo on 16-7-6.
 */
public class ServiceUtils {
    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfoList = am.getRunningServices(100);
        for(ActivityManager.RunningServiceInfo runningServiceInfo : runningServiceInfoList) {
            if(serviceName.equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
