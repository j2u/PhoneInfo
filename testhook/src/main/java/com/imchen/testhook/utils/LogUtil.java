package com.imchen.testhook.utils;

import android.util.Log;

/**
 * Created by imchen on 2017/8/2.
 */

public class LogUtil {

    private static final String TAG="imchen";
    public LogUtil() {
    }

    public static void log(Object log){
        if (log==null){
            log="no value for object!";
        }
        Log.d(TAG,"imchen:-->>"+log.toString());
    }
}
