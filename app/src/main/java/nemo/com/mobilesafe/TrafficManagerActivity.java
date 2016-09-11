package nemo.com.mobilesafe;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Bundle;

import java.util.List;

/**
 * Created by nemo on 16-9-8.
 */
public class TrafficManagerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> applicationInfoList = packageManager.getInstalledApplications(0);
        for(ApplicationInfo info : applicationInfoList) {
            int uid = info.uid;
            long tx = TrafficStats.getUidTxBytes(uid);
            long rx = TrafficStats.getUidRxBytes(uid);
        }

        TrafficStats.getMobileRxBytes();
        TrafficStats.getMobileRxBytes();

        TrafficStats.getTotalTxBytes();
        TrafficStats.getTotalRxBytes();

        setContentView(R.layout.activity_traffic_manager);
    }
}
