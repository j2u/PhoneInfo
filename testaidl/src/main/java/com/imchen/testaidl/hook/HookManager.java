package com.imchen.testaidl.hook;

import android.content.Context;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Created by imchen on 2017/7/27.
 */

public class HookManager {

    private static final String TAG="imchen";

    public static boolean hookLocation(){
        try {
            // 1. 获取系统自己的Binder
            Class serviceManager=Class.forName("android.os.ServiceManager");
            Method getService=serviceManager.getDeclaredMethod("getService",String.class);
            IBinder binder= (IBinder) getService.invoke(null, Context.LOCATION_SERVICE);
            // 2. 创建我们自己的Binder
            ClassLoader classLoader=binder.getClass().getClassLoader();
            Class[] interfaces={IBinder.class};
            BinderHookHandler handler=new BinderHookHandler(binder);
            IBinder myBinder= (IBinder) Proxy.newProxyInstance(classLoader,interfaces,handler);
            // 3. 获取ServiceManager中的sCache
            Field sCache=serviceManager.getDeclaredField("sCache");
            sCache.setAccessible(true);
            Map<String,IBinder> cache= (Map<String, IBinder>) sCache.get(null);
            // 4. 将自定义的Binder替换掉旧的系统Binder
            cache.put(Context.LOCATION_SERVICE,myBinder);
            Log.d(TAG, "hookLocation: "+cache.get(Context.LOCATION_SERVICE));
            sCache.setAccessible(false);
            return  true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
