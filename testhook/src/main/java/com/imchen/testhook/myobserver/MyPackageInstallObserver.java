package com.imchen.testhook.myobserver;

import android.content.pm.IPackageInstallObserver;
import android.os.IBinder;
import android.os.RemoteException;

import com.imchen.testhook.utils.LogUtil;

/**
 * Created by imchen on 2017/8/10.
 */

public class MyPackageInstallObserver extends IPackageInstallObserver.Stub {

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
            LogUtil.log("Install ********************" + packageName + "*************** success!" + returnCode);
            if (mOnInstallListener != null) {
                mOnInstallListener.success(returnCode);
            }
        } else {
            LogUtil.log("Install ********************" + packageName + "*************** fail!" + returnCode);
            if (mOnInstallListener != null) {
                mOnInstallListener.fail(returnCode);
            }
        }
    }
}
