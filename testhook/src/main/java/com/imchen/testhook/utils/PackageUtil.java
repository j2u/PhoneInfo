package com.imchen.testhook.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.imchen.testhook.myobserver.MyPackageDeleteObserver;
import com.imchen.testhook.myobserver.MyPackageInstallObserver;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by imchen on 2017/8/15.
 */

public class PackageUtil {

    public static final int INSTALL_REPLACE_EXISTING = 0x00000002;
    public static final int INSTALL_INTERNAL = 0x00000010;

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
        String appName = null;
        try {
            appInfo = pm.getApplicationInfo(packageName, 0);
            appName = (String) pm.getApplicationLabel(appInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }

    /**
     * 卸载app
     * @param context
     * @param packageName
     * @param onDeleteListener
     */
    public static void uninstallPackage(Context context, String packageName, MyPackageDeleteObserver.OnDeleteListener onDeleteListener) {
        PackageManager mPm = context.getPackageManager();
        try {
//            Class IPackageDeleteObserverCls = Class.forName("android.content.mPm.IPackageDeleteObserver");
            Method method = mPm.getClass().getDeclaredMethod("deletePackage", new Class[]{String.class, IPackageDeleteObserver.class, int.class});
            method.invoke(mPm, packageName, new MyPackageDeleteObserver(onDeleteListener), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 安装apk
     * @param context
     * @param uri
     * @param onInstallListener
     */
    public static void installPackage(Context context, Uri uri,MyPackageInstallObserver.OnInstallListener onInstallListener) {
        PackageManager mPm = context.getPackageManager();
        try {
            Method method=mPm.getClass().getDeclaredMethod("installPackage",new Class[]{Uri.class, IPackageInstallObserver.class,int.class,String.class});
            method.invoke(mPm,new Object[]{uri,new MyPackageInstallObserver(onInstallListener),INSTALL_REPLACE_EXISTING | INSTALL_INTERNAL,null});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getApkName(Context context,String filePath){
        PackageManager mPm=context.getPackageManager();
        PackageInfo pkgInfo=mPm.getPackageArchiveInfo(filePath,PackageManager.GET_ACTIVITIES);
        ApplicationInfo appInfo=pkgInfo.applicationInfo;
        String appName=mPm.getApplicationLabel(appInfo).toString();
        return appName;
    }
}
