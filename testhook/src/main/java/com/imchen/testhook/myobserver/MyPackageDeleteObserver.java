package com.imchen.testhook.myobserver;

import android.content.Context;
import android.content.pm.IPackageDeleteObserver;
import android.os.Looper;
import android.os.RemoteException;
import android.widget.Toast;

import com.imchen.testhook.utils.ContextUtil;
import com.imchen.testhook.utils.LogUtil;
import com.imchen.testhook.utils.RootUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by imchen on 2017/8/10.
 */

public class MyPackageDeleteObserver extends IPackageDeleteObserver.Stub {

    private OnDeleteListener deleteListener;

    public MyPackageDeleteObserver(OnDeleteListener listener) {
        deleteListener=listener;
    }

    public interface OnDeleteListener{
        void success(int returnCode);
        void fail(int returnCode);
    }

    @Override
    public void packageDeleted(String packageName, int returnCode) throws RemoteException {
//        getCodePath();
        Context context=ContextUtil.getContext();
        if (returnCode == 1) {
            LogUtil.log("Delete：" + packageName + " Success！" + returnCode);
            if (deleteListener!=null){
                deleteListener.success(returnCode);
            }
        } else {
            LogUtil.log("Delete：" + packageName + " Fail！" + returnCode);
            if (RootUtil.replyRootPermission(context.getPackageCodePath())) {
                LogUtil.log("retry to delete package!");
            }else {
                LogUtil.log("can not apply permission!");
            }
            if (deleteListener!=null){
                deleteListener.fail(returnCode);
            }
        }
    }


//    public String getCodePath(){
//        String packageCodePath;
//        try {
////            Class<?> cls=Class.forName("com.imchen.testhook.MainActivity");
////            Method method=cls.getDeclaredMethod("getApplicationContext");
////            Context context= (Context) method.invoke(cls.newInstance());
////            Field field=cls.getDeclaredField("mContext");
////            field.setAccessible(true);
////            Context context= (Context) field.get(cls.newInstance());
//            packageCodePath=context.getPackageCodePath();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//        return packageCodePath;
//    }
}
