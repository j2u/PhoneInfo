package com.imchen.testhook.utils;

import android.content.Context;
import android.os.IBinder;

import java.lang.reflect.Method;

/**
 * Created by imchen on 2017/9/5.
 */

public class MyScriptUtil {

    public static IBinder getScriptBinder(String serviceName){
        try {
            Class<?> cls= Context.class.getClassLoader().loadClass("android.os.ServiceManager");
            Method md=cls.getDeclaredMethod("getService",new Class[]{String.class});
            IBinder bpBinder= (IBinder) md.invoke(cls,serviceName);
            return bpBinder;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
