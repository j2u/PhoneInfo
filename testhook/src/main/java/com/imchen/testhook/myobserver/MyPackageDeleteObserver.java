package com.imchen.testhook.myobserver;

import android.content.Context;
import android.content.pm.IPackageDeleteObserver;
import android.os.RemoteException;

import com.imchen.testhook.utils.LogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by imchen on 2017/8/10.
 */

public class MyPackageDeleteObserver extends IPackageDeleteObserver.Stub {
    @Override
    public void packageDeleted(String packageName, int returnCode) throws RemoteException {
        getCodePath();
        if (returnCode == 1) {
            LogUtil.log("Delete：" + packageName + " Success！" + returnCode);
        } else {
            LogUtil.log("Delete：" + packageName + " Fail！" + returnCode);
        }
    }

    public String getCodePath(){
        String packageCodePath;
        try {
            Class<?> cls=Class.forName("com.imchen.testhook.MainActivity");
//            Method method=cls.getDeclaredMethod("getApplicationContext");
//            Context context= (Context) method.invoke(cls.newInstance());
            Field field=cls.getDeclaredField("mContext");
            field.setAccessible(true);
            Context context= (Context) field.get(cls.newInstance());
            packageCodePath=context.getPackageCodePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return packageCodePath;
    }
}
