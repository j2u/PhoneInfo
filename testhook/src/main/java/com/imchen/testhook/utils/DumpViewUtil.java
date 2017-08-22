package com.imchen.testhook.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import java.util.List;

/**
 * Created by imchen on 2017/8/22.
 */

public class DumpViewUtil {

    public  static void dumpAllView(Context context){
        ActivityManager manager= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= 21) {
            List<ActivityManager.RunningAppProcessInfo> pis = manager.getRunningAppProcesses();
            ActivityManager.RunningAppProcessInfo topAppProcess = pis.get(0);
            if (topAppProcess != null && topAppProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                info.packageName = topAppProcess.processName;
                info.topActivityName = "";
            }
        } else {
            //getRunningTasks() is deprecated since API Level 21 (Android 5.0)
            List localList = manager.getRunningTasks(1);
            ActivityManager.RunningTaskInfo localRunningTaskInfo = (ActivityManager.RunningTaskInfo)localList.get(0);
            info.packageName = localRunningTaskInfo.topActivity.getPackageName();
            info.topActivityName = localRunningTaskInfo.topActivity.getClassName();
        }
        return info;
    }
}
