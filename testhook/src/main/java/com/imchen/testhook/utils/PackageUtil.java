package com.imchen.testhook.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.imchen.testhook.myobserver.MyPackageDeleteObserver;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by imchen on 2017/8/15.
 */

public class PackageUtil {

    private static Context mContext;
    private static List<PackageInfo> packageInfoList;

    public PackageUtil(Context context) {
        this.mContext = context;
    }

    public static List<PackageInfo> getAllInstallPackage() {
        StringBuffer strbuff = new StringBuffer();
        PackageManager pm = mContext.getPackageManager();
        packageInfoList = pm.getInstalledPackages(0);
        for (PackageInfo info : packageInfoList
                ) {
            strbuff.append("packageName:" + info.packageName + ", Location:" + info.installLocation + ", firstInstallTime" + info.firstInstallTime + ", lastUpdateTime" + info.lastUpdateTime);
            strbuff.append("\n");
        }
        LogUtil.log(strbuff.toString());
        return packageInfoList;
    }

    public static void uninstallReflect(String packageName) {
        Method targetMethod = null;
        PackageManager pm = mContext.getPackageManager();

        try {
//            Class IPackageDeleteObserverCls = Class.forName("android.content.pm.IPackageDeleteObserver");
            Method methods[] = pm.getClass().getMethods();
            for (Method method : methods
                    ) {
                if (method.getName().equals("deletePackage")) {
                    targetMethod = method;
                    targetMethod.invoke(pm, packageName, new MyPackageDeleteObserver(), 0);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
