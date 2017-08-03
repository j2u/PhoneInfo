package com.imchen.testaidl.hook;

import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by imchen on 2017/7/27.
 */

public class BinderHookHandler implements InvocationHandler {

    private static final String TAG="imchen";

    private IBinder mOriginBinder;
    private Class ILocationManager;

    public BinderHookHandler(IBinder binder) {
        this.mOriginBinder=binder;
        try {
            ILocationManager=Class.forName("android.location.ILocationManager");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.d(TAG, "invoke: BinderHookHandler");
        switch (method.getName()){
            case "queryLocalInterface":
                ClassLoader classLoader=mOriginBinder.getClass().getClassLoader();
                Class[] interfaces=new Class[]{IInterface.class,IBinder.class,ILocationManager};
                ServiceHookHandler handler=new ServiceHookHandler(this.mOriginBinder);
                return Proxy.newProxyInstance(classLoader,interfaces,handler);
            default:
                return method.invoke(mOriginBinder, args);
        }
    }
}
