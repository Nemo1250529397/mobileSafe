package nemo.com.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import java.util.ArrayList;
import java.util.List;

import nemo.com.mobilesafe.domain.TaskInfo;

/**
 * Created by nemo on 16-8-14.
 */
public class TaskInfoProvider {
    public static List<TaskInfo> getTaskInfos(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
        for(ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            TaskInfo taskInfo = new TaskInfo();

            String packageName = processInfo.processName;
            taskInfo.setPackageName(packageName);
            Debug.MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{processInfo.pid});
            long memorySize = memoryInfos[0].getTotalPrivateDirty()*1024l;
            taskInfo.setMemorySize(memorySize);

            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
                Drawable icon = applicationInfo.loadIcon(pm);
                taskInfo.setIcon(icon);
                String name = applicationInfo.loadLabel(pm).toString();
                taskInfo.setName(name);
                if((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM) == 0) {
                    taskInfo.setUserTask(true);
                } else {
                    taskInfo.setUserTask(false);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            taskInfos.add(taskInfo);
        }
        return taskInfos;
    }
}
