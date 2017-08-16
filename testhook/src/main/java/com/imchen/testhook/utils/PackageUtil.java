package com.imchen.testhook.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.imchen.testhook.myobserver.MyPackageDeleteObserver;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by imchen on 2017/8/15.
 */

public class PackageUtil {

    private static List<PackageInfo> packageInfoList;

    public PackageUtil() {
    }

    public static List<PackageInfo> getAllInstallPackage(Context context) {
        StringBuffer strbuff = new StringBuffer();
        PackageManager pm = context.getPackageManager();
        packageInfoList = pm.getInstalledPackages(0);
        for (PackageInfo info : packageInfoList
                ) {
            strbuff.append("packageName:" + info.packageName + ", Location:" + info.installLocation + ", firstInstallTime" + info.firstInstallTime + ", lastUpdateTime" + info.lastUpdateTime);
            strbuff.append("\n");
        }
        LogUtil.log(strbuff.toString());
        return packageInfoList;
    }

    public static String getApplicationName(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo appInfo;
        String appName=null;
        try {
            appInfo = pm.getApplicationInfo(packageName, 0);
            appName= (String) pm.getApplicationLabel(appInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }


    public static void uninstallReflect( Context context, String packageName,MyPackageDeleteObserver.OnDeleteListener onDeleteListener) {
        Method targetMethod = null;
        PackageManager pm = context.getPackageManager();

        try {
//            Class IPackageDeleteObserverCls = Class.forName("android.content.pm.IPackageDeleteObserver");
            Method methods[] = pm.getClass().getMethods();
            for (Method method : methods
                    ) {
                if (method.getName().equals("deletePackage")) {
                    targetMethod = method;
                    targetMethod.invoke(pm, packageName, new MyPackageDeleteObserver(onDeleteListener), 0);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
