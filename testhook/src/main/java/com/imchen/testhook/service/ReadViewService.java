package com.imchen.testhook.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;

import com.imchen.testhook.MainActivity;
import com.imchen.testhook.utils.DumpViewUtil;
import com.imchen.testhook.utils.LogUtil;
import com.imchen.testhook.utils.Script;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ReadViewService extends Service {

    private static String currentActivity;

    public ReadViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
//                List<ActivityManager.RunningTaskInfo> infoList = getRunningTask(getApplicationContext());
                Activity activity=DumpViewUtil.getCurrentActivity();
//                ViewGroup viewGroup= (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);
////                ViewGroup mRootView= (ViewGroup) viewGroup.getChildAt(0);
//                for (int i=0;i<viewGroup.getChildCount();i++){
//                    View view= viewGroup.getChildAt(i);
//                    LogUtil.log("view: id: "+view.getId()+" view: "+view.toString()+" act: "+activity.toString());
//                }
//
//                for (ActivityManager.RunningTaskInfo info : infoList
//                        ) {
//                    if (!(info.topActivity.getClassName()).equals(currentActivity)) {
//                        Message msg = new Message();
//                        msg.what = 0x123;
//                        msg.obj = info.topActivity.getClassName();
//                        MainActivity.mHandler.sendMessage(msg);
//                        currentActivity = info.topActivity.getClassName();
//                    }
//                    LogUtil.log("*********************Top Activity: " + info.topActivity.getClassName() + " ***********************");
//                }
                Script.printallActivityView(activity,true);
            }
        };
        Timer timer = new Timer();
//        timer.schedule(timerTask, 0, 10*1000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public List<ActivityManager.RunningTaskInfo> getRunningTask(Context context) {
        if (context != null) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> mList = activityManager.getRunningTasks(1);
            return mList;
        }
        return null;
    }

}
