package com.imchen.scriptcontroller.utils;

import android.content.pm.IPackageInstallObserver;
import android.os.RemoteException;
import android.util.Log;

/**
 * Created by imchen on 2017/8/10.
 */

public class MyPackageInstallObserver extends IPackageInstallObserver.Stub {

    private final static String TAG="MyInstallObserver";

    private OnInstallListener mOnInstallListener;

    public interface OnInstallListener {
        void success(int returnCode);

        void fail(int returnCode);
    }

    public MyPackageInstallObserver(OnInstallListener listener) {
        this.mOnInstallListener = listener;
    }

    @Override
    public void packageInstalled(String packageName, int returnCode) throws RemoteException {
        if (returnCode == 1) {
            Log.d(TAG, "packageInstalled: Install ********************" + packageName + "*************** success!" + returnCode);
            if (mOnInstallListener != null) {
                mOnInstallListener.success(returnCode);
            }
        } else {
            Log.d(TAG, "packageInstalled: Install ********************" + packageName + "*************** fail!" + returnCode);
            if (mOnInstallListener != null) {
                mOnInstallListener.fail(returnCode);
            }
        }
    }
}
