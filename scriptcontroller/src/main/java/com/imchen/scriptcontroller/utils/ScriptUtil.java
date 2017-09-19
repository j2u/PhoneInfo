package com.imchen.scriptcontroller.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

/**
 * Created by imchen on 2017/9/8.
 */

public class ScriptUtil {

    public static final int INSTALL_REPLACE_EXISTING = 0x00000002;
    public static final int INSTALL_INTERNAL = 0x00000010;
    private static boolean isPackageInstalled = false;
    private static boolean isScriptFileExists;
    private static boolean isDownloading = false;
    private static int tryTime = 0;
    private final static String SCRIPT_PATH = "/data/local/rom/";
    private final static String SCRIPT_CONTROLLER_DOWNLOAD_PATH = "/sdcard/ScriptController/";
    private final static String TAG = "imchen";

    public final static int DOWNLOAD_THREAD_NUM = 3;
    public static int mCurrentPosition = 0;
    private static int mCancelNum = 0;
    private static int mStopNum = 0;
    private static int mCompleteThreadNum = 0;
    private static boolean isNewTask = false;
    private static boolean isCancel = false;
    private static boolean isStop = false;

    private static DownloadListener mListener;

    public static Context mContext;
    public static JSONObject configJsonObj = null;

    public interface IDownloadStateListener {
        void success(String hint);

        void updateProgress(int max, int progress);

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

    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x001d:
                    isDownloading = true;
                    Bundle bundle = msg.getData();
                    String downloadLink = bundle.getString("downloadLink");
                    final String packageName = bundle.getString("packageName");
                    final String filePath = bundle.getString("filePath");
                    final boolean isProgram = bundle.getBoolean("isProgram");

                    break;
                default:
                    break;
            }
        }
    };

    public static void startScript(@NonNull final String packageName) {
        JSONObject config;
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
                String downloadLink = (String) config.get("installApkUrl");
                final String filePath = SCRIPT_CONTROLLER_DOWNLOAD_PATH + packageName + "/install/" + packageName + ".apk";
                Message msg = new Message();
                msg.what = 0x001d;
                Bundle bundle = new Bundle();
                bundle.putString("downloadLink", downloadLink);
                bundle.putString("filePath", filePath);
                bundle.putString("packageName", packageName);
                bundle.putBoolean("isProgram", true);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
            int time = 1;
            while (isDownloading) {
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
                    isDownloading = true;
                    download(scriptDownLink + packageName + ".apk", SCRIPT_CONTROLLER_DOWNLOAD_PATH + packageName + "/script/" + packageName + ".apk", new IDownloadStateListener() {
                        @Override
                        public void success(String hint) {
                            isDownloading = false;
                            isScriptFileExists = true;
                        }

                        @Override
                        public void failed(String warning) {
                            isDownloading = false;
                            isScriptFileExists = false;
                        }

                        @Override
                        public void updateProgress(int max, int progress) {

                        }
                    });
                } else {
                    copyFile(scriptFileDownloadPath, SCRIPT_PATH);
                    isScriptFileExists = true;
                }

            }
            while (isDownloading) {
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
        if (!checkSDCardAvailable()) {
            if (listener != null) {
                listener.failed("SDCard is not available!!");
            }
            return;
        }

    }

    private class myAsyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(Object o) {
            super.onCancelled(o);
        }
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

    public class DownloadTask1 extends Thread {

        private String downloadLink;
        private String savePath;
        private IDownloadStateListener downStatusListener;

        public DownloadTask1(String downloadLink, String savePath, @NonNull IDownloadStateListener downStatusListener) {
            this.downloadLink = downloadLink;
            this.savePath = savePath;
            this.downStatusListener = downStatusListener;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(downloadLink);
                InputStream ins;
                OutputStream ops;
                String fileName = downloadLink.substring(downloadLink.lastIndexOf("/") + 1);
                ins = url.openStream();
                byte[] buffer = new byte[1024];
                File file = new File(savePath);
                if (file.exists()) {
                    file.renameTo(new File(file.getAbsolutePath() + System.currentTimeMillis() + "_old_" + fileName));
                }
                int length;
                ops = new FileOutputStream(savePath);
                int max = ins.available();
                int curDownload;
                while ((length = ins.read(buffer)) != -1) {
                    ops.write(buffer);
                    curDownload = length;
                    downStatusListener.updateProgress(max, curDownload);
                }
                ops.flush();
                ins.close();
                ops.close();
            } catch (IOException e) {
                if (downStatusListener != null) {
                    downStatusListener.failed(e.toString());
                }
                e.printStackTrace();
            } finally {
            }
        }
    }


    //重写下载模块

    private static class DownloadEntity {
        long fileSize;
        String downloadUrl;
        int threadId;
        long startPosition;
        long endPosition;
        File tempFile;

        public DownloadEntity(String downloadUrl, File tempFile, long fileSize, long startPosition, long endPosition, int threadId) {
            this.fileSize = fileSize;
            this.downloadUrl = downloadUrl;
            this.threadId = threadId;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
            this.tempFile = tempFile;
        }
    }

    private static class DownloadTask implements Runnable {

        private DownloadEntity downloadInfo;
        private String configFilePath;

        public DownloadTask(DownloadEntity downloadInfo) {
            this.downloadInfo = downloadInfo;
            configFilePath = mContext.getFilesDir() + "/temp/" + downloadInfo.tempFile.getName() + ".properties";
        }

        @Override
        public void run() {
            try {
                Log.d(TAG, "run: Thread_" + downloadInfo.threadId + "_正在下载【开始位置：" + downloadInfo.startPosition + "结束位置：" + downloadInfo.endPosition + "】");
                URL url = new URL(downloadInfo.downloadUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Range", "bytes=" + downloadInfo.startPosition + "-" + downloadInfo.endPosition);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Charset", "UTF-8");
                conn.setConnectTimeout(10000);
                conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT " +
                        "5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
                conn.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, " +
                        "application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument," +
                        " application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
                conn.setReadTimeout(2000);
                conn.connect();
                int contentLength = conn.getContentLength();
                if (contentLength < 0) {
                    mListener.onFail();
                    return;
                }
                int respCode = conn.getResponseCode();
                InputStream is = conn.getInputStream();
                //创建可设置位置的文件
                RandomAccessFile file = new RandomAccessFile(downloadInfo.tempFile, "rwd");
                file.seek(downloadInfo.startPosition);
                byte[] buffer = new byte[1024];
                int len;
                long currentPosition = downloadInfo.startPosition;
                while ((len = is.read(buffer)) != -1) {
                    if (isCancel) {
                        Log.d(TAG, "++++++++++ thread_" + downloadInfo.threadId + "_cancel ++++++++++");
                        break;
                    }

                    if (isStop) {
                        Log.d(TAG, "++++++++++ thread_" + downloadInfo.threadId + "_stop ++++++++++");
                        break;
                    }
                    file.write(buffer, 0, len);
                    synchronized (ScriptUtil.class) {
                        mCurrentPosition += len;
                    }
                }
                file.close();
                is.close();

                if (isCancel) {
                    synchronized (ScriptUtil.class) {
                        mCancelNum++;
                        if (mCancelNum == DOWNLOAD_THREAD_NUM) {
                            File configFile = new File(configFilePath);
                            if (configFile.exists()) {
                                configFile.delete();
                            }
                            if (downloadInfo.tempFile.exists()) {
                                downloadInfo.tempFile.delete();
                            }
                            Log.d(TAG, "++++++++++++++++ onCancel +++++++++++++++++");
                            isDownloading = false;
                            mListener.onCancel();
                            System.gc();
                        }
                    }

                }

                if (isStop) {
                    synchronized (ScriptUtil.class) {
                        mStopNum++;
                        String position = String.valueOf(currentPosition);
                        Log.i(TAG, "thread_" + downloadInfo.threadId + "_stop, stop location ==> " + position);
                        writeConfig(configFilePath, downloadInfo.tempFile.getName() + "_recode_" + downloadInfo.threadId, position);
                        if (mStopNum == DOWNLOAD_THREAD_NUM) {
                            Log.d(TAG, "run: +++++++++++++++++download stop+++++++++++++++");
                            isDownloading = false;
                            mListener.onStop(mCurrentPosition);
                            System.gc();
                        }
                    }
                }
                Log.i(TAG, "线程【" + downloadInfo.threadId + "】下载完毕");
                writeConfig(configFilePath, downloadInfo.tempFile.getName() + "_state_" + downloadInfo.threadId, 1 + "");
                mListener.onChildComplete(downloadInfo.endPosition);
                mCompleteThreadNum++;
                if (mCompleteThreadNum == DOWNLOAD_THREAD_NUM) {
                    File configFile = new File(configFilePath);
                    if (configFile.exists()) {
                        configFile.delete();
                    }
                    mListener.onComplete();
                    isDownloading = false;
                    System.gc();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                isDownloading = false;
                mListener.onFail();
            } catch (IOException e) {
                e.printStackTrace();
                isDownloading = false;
                mListener.onFail();
            }
        }
    }

    public static void download(@NonNull final String downloadUrl, @NonNull final String filePath, @NonNull final DownloadListener downloadListener) {

        final File downloadFile = new File(filePath);
        final String configPath = mContext.getFilesDir().getPath() + "/temp/" + downloadFile.getName() + ".properties";
        final File configFile = new File(configPath);
        if (!configFile.exists()) {
            isNewTask = true;
            boolean isCreate =createFile(configPath);
            Log.d(TAG, "download: "+configPath+" "+isCreate);
        } else {
            isNewTask = false;
            Log.d(TAG, "download: isNewTask"+isNewTask+" "+configPath);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                mListener = downloadListener;
                URL url = null;
                HttpURLConnection conn = null;
                try {
                    url = new URL(downloadUrl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Charset", "UTF-8");
                    conn.setConnectTimeout(10000);
                    conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
                    conn.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
                    conn.connect();
                    int len = conn.getContentLength();
                    if (len < 0) {  //网络被劫持时会出现这个问题
                        mListener.onFail();
                        return;
                    }
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        boolean isCreate=createFile(filePath);
                        Log.d(TAG, "run: isCreate:"+filePath+" "+isCreate);
                        int fileLength = conn.getContentLength();
                        RandomAccessFile file = new RandomAccessFile(filePath, "rwd");
                        file.setLength(fileLength);
                        mListener.onPreDownload(conn);

                        Properties properties = readConfig(configPath);
                        int blockSize = fileLength / DOWNLOAD_THREAD_NUM;
                        SparseArray<Thread> tasks = new SparseArray<>();
                        for (int i = 0; i < DOWNLOAD_THREAD_NUM; i++) {
                            long startPosition = i * blockSize;
                            long endPosition = (i + 1) * blockSize;
                            String status = properties.getProperty(downloadFile.getName() + "_status_" + i);
                            if (status != null && Integer.parseInt(status) == 1) {
                                mCurrentPosition += endPosition - startPosition;
                                Log.d(TAG, "++++++++++ 线程_" + i + "_已经下载完成 ++++++++++");
                                mCompleteThreadNum++;
                                if (mCompleteThreadNum == DOWNLOAD_THREAD_NUM) {
                                    if (configFile.exists()) {
                                        configFile.delete();
                                    }
                                    isDownloading = false;
                                    mListener.onComplete();
                                    System.gc();
                                    return;
                                }
                            }
                            String record = properties.getProperty(downloadFile.getName() + "_record_" + i);
                            if (!isNewTask && record != null && Integer.parseInt(record) > 0) {
                                long rec = Integer.parseInt(record);
                                mCurrentPosition += rec - startPosition;
                                Log.d(TAG, "++++++++++ 线程_" + i + "_恢复下载 ++++++++++");
                                mListener.onChildResume(mCurrentPosition);
                                startPosition = rec;
                            }
                            if (i == (DOWNLOAD_THREAD_NUM - 1)) {
                                endPosition = fileLength;//如果整个文件的大小不为线程个数的整数倍，则最后一个线程的结束位置即为文件的总长度
                            }
                            DownloadEntity entity = new DownloadEntity(downloadUrl, downloadFile, fileLength, startPosition, endPosition, i);
                            DownloadTask task = new DownloadTask(entity);
                            tasks.put(i, new Thread(task));
                            if (mCurrentPosition > 0) {
                                mListener.onResume(mCurrentPosition);
                            } else {
                                mListener.onStart(mCurrentPosition);
                            }
                        }
                        for (int i = 0, count = tasks.size(); i < count; i++) {
                            Thread task = tasks.get(i);
                            if (task != null) {
                                task.start();
                            }
                        }
                    } else {
                        isDownloading = false;
                        mListener.onFail();
                        Log.d(TAG, "run: 下载失败：返回码 " + code);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    isDownloading = false;
                    mListener.onFail();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                    isDownloading = false;
                    mListener.onFail();
                } catch (IOException e) {
                    e.printStackTrace();
                    isDownloading = false;
                    mListener.onFail();
                }
            }
        }).start();
    }

    public static boolean createFile(String filePath) {
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                File dir = new File(file.getParent());
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                return file.createNewFile();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }


    public static Properties readConfig(String filePath) {
        Properties properties = null;
        try {
            properties = new Properties();
            FileInputStream is = new FileInputStream(filePath);
            properties.load(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static boolean writeConfig(String filePath, String key, String value) {
        File file = new File(filePath);
        Properties properties = new Properties();
        properties.setProperty(key, value);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            properties.store(fos, "");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public interface IDownloadListener {

        public void onPreDownload(HttpURLConnection connection);

        public void onStart(long startPosition);

        public void onProgress(long currentPosition);

        public void onChildComplete(long finishPosition);

        public void onChildResume(long resumePosition);

        public void onResume(long resumePosition);

        public void onComplete();

        public void onCancel();

        public void onFail();

        public void onStop(long stopPosition);
    }

    public static class DownloadListener implements IDownloadListener {

        @Override
        public void onPreDownload(HttpURLConnection connection) {

        }

        @Override
        public void onStart(long startPosition) {

        }

        @Override
        public void onProgress(long currentPosition) {

        }

        @Override
        public void onChildComplete(long finishPosition) {

        }

        @Override
        public void onChildResume(long resumePosition) {

        }

        @Override
        public void onResume(long resumePosition) {

        }

        @Override
        public void onComplete() {

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onFail() {

        }

        @Override
        public void onStop(long stopPosition) {

        }
    }

}



