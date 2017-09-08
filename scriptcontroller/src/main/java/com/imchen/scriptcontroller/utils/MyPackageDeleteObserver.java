package com.imchen.scriptcontroller.utils;

import android.content.pm.IPackageDeleteObserver;
import android.os.RemoteException;
import android.util.Log;

/**
 * Created by imchen on 2017/8/10.
 */

public class MyPackageDeleteObserver extends IPackageDeleteObserver.Stub {

    private final static String TAG="MyPackageDeleteObserver";

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
        if (returnCode == 1) {
            Log.d(TAG, "packageDeleted: *************** Delete：" + packageName + " Success！" + returnCode+" ****************");
            if (deleteListener!=null){
                deleteListener.success(returnCode);
            }
        } else {
            Log.d(TAG, "packageDeleted: *************** Delete：" + packageName + " Fail!" + returnCode+" *******************");
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
