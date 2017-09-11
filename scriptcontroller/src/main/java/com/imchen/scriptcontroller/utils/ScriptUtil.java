package com.imchen.scriptcontroller.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

/**
 * Created by imchen on 2017/9/8.
 */

public class ScriptUtil {

    public static final int INSTALL_REPLACE_EXISTING = 0x00000002;
    public static final int INSTALL_INTERNAL = 0x00000010;
    private static boolean isPackageInstalled = false;
    private static boolean isScriptFileExists;
    private static boolean isDownlaoding = false;
    private static int tryTime = 0;
    private final static String SCRIPT_PATH = "/data/local/rom/";
    private final static String SCRIPT_CONTROLLER_DOWNLOAD_PATH = "/sdcard/ScriptController/";
    private final static String TAG = "imchen";

    public static Context mContext;
    public static JSONObject configJsonObj = null;

    public interface IDownloadStateListener {
        void success(String hint);

        void failed(String warning);
    }

    static {
        try {
            Object activityThread = getActivityThread();
            Method method = activityThread.getClass().getDeclaredMethod("getApplication");
            mContext = (Context) method.invoke(activityThread);
            String configFile = SCRIPT_CONTROLLER_DOWNLOAD_PATH + "config/config.json";
            readConfigFromSDCard(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startScript(@NonNull final String packageName) {
        JSONObject config = null;
        try {
            Log.d(TAG, "startScript: " + configJsonObj.toString());
            String tmp = configJsonObj.get("config").toString();
            config = new JSONObject(tmp);
            List<PackageInfo> listInfo = getAllInstalledPackage();
            for (PackageInfo info : listInfo
                    ) {
                if ((info.packageName).equals(packageName)) {
                    isPackageInstalled = true;
                    break;
                }
            }
            if (!isPackageInstalled) {

                try {
                    String downLink = (String) config.get("installApkUrl");
                    final String filePath = SCRIPT_CONTROLLER_DOWNLOAD_PATH + packageName + "/install/" + packageName + ".apk";
                    isDownlaoding = true;
                    download(downLink + packageName + ".apk", filePath, new IDownloadStateListener() {
                        @Override
                        public void success(String hint) {
                            isDownlaoding = false;
                            installApkFile(filePath, new MyPackageInstallObserver.OnInstallListener() {
                                @Override
                                public void success(int returnCode) {
                                    Log.d(TAG, "success: install");
                                    isPackageInstalled = true;
                                }

                                @Override
                                public void fail(int returnCode) {
                                    Log.d(TAG, "fail: install");
                                    isPackageInstalled = false;
                                }
                            });
                        }

                        @Override
                        public void failed(String warning) {
                            if (tryTime < 3) {
                                tryTime++;
                                startScript(packageName);
                            } else {
                                return;
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            int time = 1;
            while (isDownlaoding) {
                Log.d(TAG, "startScript: sleeping " + time + " S");
                Thread.sleep(1000);
                time++;
            }
            String scriptFilePath = SCRIPT_PATH + packageName + ".apk";
            String scriptFileDownloadPath = SCRIPT_CONTROLLER_DOWNLOAD_PATH + packageName + "/script/" + packageName + ".apk";
            File scriptFile = new File(scriptFilePath);
            isScriptFileExists = scriptFile.exists();
            if (!isScriptFileExists) {
                if (!new File(scriptFileDownloadPath).exists()) {
                    String scriptDownLink = config.get("scriptApkUrl").toString();
                    isDownlaoding = true;
                    download(scriptDownLink + packageName + ".apk", SCRIPT_CONTROLLER_DOWNLOAD_PATH + packageName + "/script/" + packageName + ".apk", new IDownloadStateListener() {
                        @Override
                        public void success(String hint) {
                            isDownlaoding = false;
                            isScriptFileExists = true;
                        }

                        @Override
                        public void failed(String warning) {
                            isDownlaoding = false;
                            isScriptFileExists = false;
                        }
                    });
                } else {
                    copyFile(scriptFileDownloadPath, SCRIPT_PATH);
                    isScriptFileExists = true;
                }

            }
            while (isDownlaoding) {
                Log.d(TAG, "startScript: wait download ");
                Thread.sleep(1000);
            }
            if (isScriptFileExists) {
                launcherApplication(packageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void stopScript(String packageName) {

    }

    public static void updateScript(File newScriptFile) {
    }

    public static void deleteScript(String scriptPath) {
    }

    public static void installAPK(String apkPath) {

    }

    public static void uninstallApplication(String packageName) {
    }

    public static void download(final String url, final String savePath, final IDownloadStateListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!checkSDCardAvailable()) {
                    if (listener != null) {
                        listener.failed("SDCard is not available!!");
                    }
                    return;
                }
                try {
                    URL downLink = new URL(url);
                    InputStream ins = null;
                    OutputStream ops = null;
                    String fileName = url.substring(url.lastIndexOf("/") + 1);
                    ins = downLink.openStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    File file = new File(savePath);
                    if (file.exists()) {
                        file.renameTo(new File(file.getAbsolutePath() + System.currentTimeMillis() + "_old_" + fileName));
                    }
                    ops = new FileOutputStream(savePath);
                    while ((length = ins.read(buffer)) > 0) {
                        ops.write(buffer);
                    }
                    ops.flush();
                    ins.close();
                    ops.close();
                } catch (IOException e) {
                    if (listener != null) {
                        listener.failed(e.toString());
                    }
                    e.printStackTrace();
                } finally {
                }
            }
        }).start();
    }

    public static boolean checkSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static JSONObject readConfigFromSDCard(@NonNull String filePath) {
        if (configJsonObj != null) {
            return configJsonObj;
        }
        File file = new File(filePath);
        String jsonStr = null;
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
                jsonStr = new String(buffer, "UTF-8");
                configJsonObj = new JSONObject(jsonStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return configJsonObj;
    }

    public static void upload() {
    }

    public static void fileRename(String filePath, String newName) {
    }

    public static void copyFile(String oldFile, String targetPath) throws Exception {
        File file = new File(oldFile);
        String fileName = oldFile.substring(oldFile.lastIndexOf("/") + 1);
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(targetPath + fileName);
        byte[] buffer = new byte[1024];
        while (fis.read(buffer) > 0) {
            fos.write(buffer);
        }
        fos.flush();
        fis.close();
        fos.close();
    }

    public static int scriptScanner() {
        return 0;
    }

    public static void launcherApplication(String packageName) {
        Intent intent = new Intent();
        intent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            mContext.startActivity(intent);
        }
    }

    public static List<PackageInfo> getAllInstalledPackage() {
        StringBuffer strbuff = new StringBuffer();
        PackageManager pm = mContext.getPackageManager();
        List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
        return packageInfoList;
    }

    public static String getApplicationName(String packageName) {
        PackageManager pm = mContext.getPackageManager();
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
     * 卸载app，无回调
     *
     * @param packageName
     */
    public static void uninstallPackage(Context context, String packageName) {
        uninstallPackage(packageName, null);
    }

    /**
     * 卸载app，有回调
     *
     * @param packageName
     * @param onDeleteListener
     */
    public static void uninstallPackage(String packageName, MyPackageDeleteObserver.OnDeleteListener onDeleteListener) {
        PackageManager mPm = mContext.getPackageManager();
        try {
//            Class IPackageDeleteObserverCls = Class.forName("android.content.mPm.IPackageDeleteObserver");
            Method method = mPm.getClass().getDeclaredMethod("deletePackage", new Class[]{String.class, IPackageDeleteObserver.class, int.class});
            method.invoke(mPm, packageName, new MyPackageDeleteObserver(onDeleteListener), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 安装apk，无回调
     *
     * @param uri
     */
    public static void installPackage(Uri uri) {
        installApkFile(uri, null);
    }

    /**
     * 指定路径安装
     *
     * @param apkPath
     */
    public static void installPackage(String apkPath) {
        installApkFile(apkPath, null);
    }

    /**
     * 指定路径安装
     *
     * @param apkPath
     * @param onInstallListener
     */
    public static void installApkFile(String apkPath, MyPackageInstallObserver.OnInstallListener onInstallListener) {
        installApkFile(Uri.fromFile(new File(apkPath)), onInstallListener);
    }

    /**
     * 安装apk，有回调
     *
     * @param uri
     * @param onInstallListener
     */
    public static void installApkFile(Uri uri, MyPackageInstallObserver.OnInstallListener onInstallListener) {
        PackageManager mPm = mContext.getPackageManager();
        try {
            Method method = mPm.getClass().getDeclaredMethod("installPackage", new Class[]{Uri.class, IPackageInstallObserver.class, int.class, String.class});
            method.invoke(mPm, new Object[]{uri, new MyPackageInstallObserver(onInstallListener), INSTALL_REPLACE_EXISTING | INSTALL_INTERNAL, null});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object getActivityThread() {
        Object activityThread;
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method method = cls.getDeclaredMethod("currentActivityThread");
            activityThread = method.invoke(null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return activityThread;
    }

    public static void initDir(String packageName) {
        String installPath = SCRIPT_CONTROLLER_DOWNLOAD_PATH + packageName + "/install";
        String scriptPath = SCRIPT_CONTROLLER_DOWNLOAD_PATH + packageName + "/script";
        File installDir = new File(installPath);
        File scriptDir = new File(scriptPath);
        if (!installDir.exists()) {
            installDir.mkdirs();
        }
        if (!scriptDir.exists()) {
            scriptDir.mkdirs();
        }
    }

}
