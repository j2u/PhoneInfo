package com.imchen.hookbinder.hook;

import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Switch;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by imchen on 2017/7/27.
 */

public class ServiceHookHandler implements InvocationHandler {

    private static final String TAG="imchen";
    private Object mOriginService;
    public ServiceHookHandler(IBinder binder) {
        try {
            Class ILocationManager$Stub =Class.forName("android.location.ILocationManager$Stub");
            Method asInterface=ILocationManager$Stub.getDeclaredMethod("asInterface",IBinder.class);
            this.mOriginService=asInterface.invoke(null,binder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.d(TAG, "invoke: ServiceHookHandler");
        switch (method.getName()){
            case "getLastLocation":
                Location location=new Location(LocationManager.GPS_PROVIDER);
                location.setLongitude(10.1);
                location.setLatitude(20.33);
                return location;
            default:
                return  method.invoke(this.mOriginService,args);
        }
    }
}
