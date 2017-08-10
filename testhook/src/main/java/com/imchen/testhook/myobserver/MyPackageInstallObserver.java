package com.imchen.testhook.myobserver;

import android.content.pm.IPackageInstallObserver;
import android.os.IBinder;
import android.os.RemoteException;

import com.imchen.testhook.utils.LogUtil;

/**
 * Created by imchen on 2017/8/10.
 */

public class MyPackageInstallObserver extends IPackageInstallObserver.Stub {
    @Override
    public void packageInstalled(String packageName, int returnCode) throws RemoteException {
        if (returnCode == 1) {
            LogUtil.log("安装：" + packageName + " 成功！" + returnCode);
        } else {
            LogUtil.log("安装：" + packageName + " 失败！" + returnCode);
        }
    }
}
