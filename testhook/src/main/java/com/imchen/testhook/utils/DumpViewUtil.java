package com.imchen.testhook.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by imchen on 2017/8/22.
 */

public class DumpViewUtil {

    private static LinkedList<Object> objsearch = new LinkedList<Object>();
//    public  static void dumpAllView(Context context){
//        ActivityManager manager= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        if (Build.VERSION.SDK_INT >= 21) {
//            List<ActivityManager.RunningAppProcessInfo> pis = manager.getRunningAppProcesses();
//            ActivityManager.RunningAppProcessInfo topAppProcess = pis.get(0);
//            if (topAppProcess != null && topAppProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                info.packageName = topAppProcess.processName;
//                info.topActivityName = "";
//            }
//        } else {
//            //getRunningTasks() is deprecated since API Level 21 (Android 5.0)
//            List localList = manager.getRunningTasks(1);
//            ActivityManager.RunningTaskInfo localRunningTaskInfo = (ActivityManager.RunningTaskInfo)localList.get(0);
//            info.packageName = localRunningTaskInfo.topActivity.getPackageName();
//            info.topActivityName = localRunningTaskInfo.topActivity.getClassName();
//        }
//        return info;
//    }

    public static Activity getCurrentActivity() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(
                    null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map activities = (Map) activitiesField.get(activityThread);
            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    Activity activity = (Activity) activityField.get(activityRecord);
                    return activity;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int enumView() throws Exception {

        Class<?> clsWindowManagerImpl;
        Method mgetDefault;
        try {
            clsWindowManagerImpl = Context.class.getClassLoader().loadClass(
                    "android.view.WindowManagerGlobal");
            mgetDefault = clsWindowManagerImpl.getMethod("getInstance",
                    new Class[]{});
        } catch (Exception e) {
            clsWindowManagerImpl = Context.class.getClassLoader().loadClass(
                    "android.view.WindowManagerImpl");
            mgetDefault = clsWindowManagerImpl.getMethod("getDefault",
                    new Class[]{});
        }

        Object objWindowManagerImpl = mgetDefault.invoke(clsWindowManagerImpl,
                new Object[]{});

        Field fView = clsWindowManagerImpl.getDeclaredField("mViews");
        fView.setAccessible(true);

        ArrayList<View> mViews = null;

        try {
            View[] mViews1 = (View[]) fView.get(objWindowManagerImpl);
            if (mViews1 != null) {
                mViews = new ArrayList<View>();
                for (int i = 0; i < mViews1.length; i++) {
                    mViews.add(mViews1[i]);
                }
            }
        } catch (Exception ex) {

        }

        if (mViews == null) {
            try {
                mViews = (ArrayList<View>) fView.get(objWindowManagerImpl);
            } catch (Exception ex) {

            }
        }

        if (mViews != null && mViews.size() > 0) {

            for (int i = mViews.size() - 1; i >= 0; i--) {

                View v = mViews.get(i);

                if (v.isShown() == false) {
                    continue;
                }

                try {

                    Class<?> clsTest = Context.class
                            .getClassLoader()
                            .loadClass(
                                    "com.android.internal.policy.impl.PhoneWindow$DecorView");

                    if (clsTest.isInstance(v)) {
                        Field fd = clsTest.getDeclaredField("this$0");
                        fd.setAccessible(true);
                        Object obj = fd.get(v);

                        clsTest = Context.class.getClassLoader().loadClass(
                                "com.android.internal.policy.impl.PhoneWindow");
                        Method md = clsTest.getMethod("getCallback",
                                new Class[]{});
                        md.setAccessible(true);

                        Object cbobj = md.invoke(obj, new Object[]{});

                        if ((Object) cbobj instanceof Dialog) {
                            LogUtil.log(cbobj);
//                            return onDialog((Dialog) cbobj);
                        } else if ((Object) cbobj instanceof Activity) {
                            LogUtil.log(cbobj.toString());
//                            return onActivity((Activity) cbobj);
                        } else {
//                            cbobj = testView(cbobj);
                            if ((Object) cbobj instanceof Dialog) {
                                LogUtil.log(cbobj.toString());
//                                return onDialog((Dialog) cbobj);
                            } else if ((Object) cbobj instanceof Activity) {
//                                return onActivity((Activity) cbobj);
                            }
                        }
                    }

                } catch (Exception e) {
                    LogUtil.log(e);
                }
            }

        }
        // }

        return 1;

    }

    private static Object testView2(Object obj) {

        LogUtil.log("obj=" + obj.toString());
        objsearch.add(obj);

        try {
            Field[] fds = obj.getClass().getDeclaredFields();

            if (fds != null) {
                for (int i = 0; i < fds.length; i++) {
                    fds[i].setAccessible(true);
                    Object objfd = fds[i].get(obj);

                    if (objsearch.indexOf(objfd) == -1) {

                        if (objfd instanceof Activity) {
                            return objfd;
                        }
                        if (objfd instanceof Dialog) {
                            return objfd;
                        }
                        Object r = testView2(objfd);
                        if (r != null) {
                            return r;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

}
