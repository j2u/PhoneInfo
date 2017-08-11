package com.imchen.testhook.utils;

import android.content.Context;

import java.lang.reflect.Method;

/**
 * Created by imchen on 2017/8/11.
 */

public class ContextUtil {

    private static Context mContext;

    static {
        try {
            Object activityThread = getActivityThread();
            Method method = activityThread.getClass().getDeclaredMethod("getApplication");
            mContext = (Context) method.invoke(activityThread);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Context getContext() {
        return mContext;
    }

    private static Object getActivityThread() {
        Object activityThread ;
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method method = cls.getDeclaredMethod("currentActivityThread");
            activityThread = method.invoke(null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return activityThread;
    }
}
