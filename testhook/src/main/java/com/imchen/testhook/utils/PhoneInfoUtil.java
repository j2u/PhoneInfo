package com.imchen.testhook.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.imchen.testhook.Entity.Battery;
import com.imchen.testhook.Entity.Bluetooth;
import com.imchen.testhook.Entity.Codes;
import com.imchen.testhook.Entity.Screen;
import com.imchen.testhook.Entity.Telephony;
import com.imchen.testhook.Entity.Version;
import com.imchen.testhook.Entity.Wifi;
import com.imchen.testhook.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.List;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by imchen on 2017/8/2.
 */


public class PhoneInfoUtil {
    private static int level;
    private static int scale;
    private static String locationProvider;
    private static Location location;
    private static Context mContext;

    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x1:
                    Location location = msg.getData().getParcelable("location");
                    MainActivity.mLocationTv.setText(location.toString());
                    break;
            }
        }
    };

    static {
        try {
            Object activityThread = getActivityThread();
            Method method = activityThread.getClass().getDeclaredMethod("getApplication");
            mContext = (Context) method.invoke(activityThread);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PhoneInfoUtil() {
    }

    public static Context getContext() {
        return mContext;
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


//    public void getAllInfo(Activity activity) {
//        getWifiInfo();
//        getAvailMemory();
//        getBluetoothAddress();
//        getCpuInfo();
//        getLocalIpAddress();
//        getLocalIpAddress2();
////        getLocation();
////        getLocationInfo();
//        getPhoneState();
//        getSDCardInfo();
//        getSimCardInfo();
//        getSystemMemory();
//        getTotalMemory();
//        getWeithAndHeight(activity);
//        getBuildInfo();
//        getTelephonyInfo();
//        getBatteryInfo();
//        getDisplayInfo(activity);
////        batteryReciver();
//        getBatteryInfo2();
//        getHttpAgent();
//        getSystemSetting();
//        getLocation();
//    }

    /**
     * 获取蓝牙地址Hook android.bluetooth.IBluetoothManager$Stub可以拦截
     *
     * @return
     */
    public static String getBluetoothAddress() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            LogUtil.log("bluetoothAdapter.getAddress(): " + bluetoothAdapter.getAddress());
            Field field = bluetoothAdapter.getClass().getDeclaredField("mService");
            // 参数值为true，禁用访问控制检查
            field.setAccessible(true);
            Object bluetoothManagerService = field.get(bluetoothAdapter);

            if (bluetoothManagerService == null) {
                return null;
            }
            Method method = bluetoothManagerService.getClass().getMethod("getAddress", new Class[]{});
            LogUtil.log("method.getName()=" + method.getName());
            method.setAccessible(true);
            Object address = method.invoke(bluetoothManagerService, new Object[]{});
            if (address != null && address instanceof String) {

                return (String) address;
            } else {
                return null;
            }
        } catch (IllegalArgumentException e) {
            LogUtil.log(e);
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            LogUtil.log(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            LogUtil.log(e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            LogUtil.log(e);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            LogUtil.log(e);
        }
        return null;
    }

    /**
     * t通过Adapter获取地址
     *
     * @return
     */
    public static Bluetooth getBluetoothInfo() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        Bluetooth bluetooth = new Bluetooth();
        String bluetoothAddress = null;
        if (adapter.isEnabled()) {
            bluetoothAddress = adapter.getAddress();
            bluetooth.setAddress(bluetoothAddress);
            LogUtil.log(bluetoothAddress);
        }
        return bluetooth;
    }

    // 获取Android手机中SD卡存储信息 获取剩余空间
    public void getSDCardInfo() {
        // 在manifest.xml文件中要添加
                    /*
                     * <uses-permission
                     * android:name="android.permission.WRITE_EXTERNAL_STORAGE">
                     * </uses-permission>
                     */
        // 需要判断手机上面SD卡是否插好，如果有SD卡的情况下，我们才可以访问得到并获取到它的相关信息，当然以下这个语句需要用if做判断
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            // 取得sdcard文件路径
            File path = Environment.getExternalStorageDirectory();
            StatFs statfs = new StatFs(path.getPath());
            // 获取block的SIZE
            long blocSize = statfs.getBlockSize();
            // 获取BLOCK数量
            long totalBlocks = statfs.getBlockCount();
            // 空闲的Block的数量
            long availaBlock = statfs.getAvailableBlocks();
            // 计算总空间大小和空闲的空间大小
            // 存储空间大小跟空闲的存储空间大小就被计算出来了。
            long availableSize = blocSize * availaBlock;
            // (availableBlocks * blockSize)/1024 KIB 单位
            // (availableBlocks * blockSize)/1024 /1024 MIB单位
            long allSize = blocSize * totalBlocks;
        }

    }

    // 获取手机ip method-1
    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        Toast.makeText(mContext, inetAddress.getHostAddress().toString(), Toast.LENGTH_SHORT).show();
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
            // Log.e("ifo", ex.toString());
        }
        return "";
    }

    // 需要权限
    // <uses-permission
    // android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
    // <uses-permission
    // android:name="android.permission.INTERNET"></uses-permission>

    // 获取手机ip method-2
    // 首先设置用户权限
    // <uses-permission
    // android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
    // <uses-permission
    // android:name="android.permission.CHANGE_WIFI_STATE"></uses-permission>
    // <uses-permission
    // android:name="android.permission.WAKE_LOCK"></uses-permission>
    public String getLocalIpAddress2() {
        // 获取wifi服务
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        // 判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        LogUtil.log("ip:" + ip);
        return ip;
    }

    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + (i >> 24 & 0xFF);
    }

    /**
     * 查看本机外网IP
     * 该方法需要设备支持上网 查看
     * System.out.println((getOutNetIp("http://fw.qq.com/ipaddress"))); 加权限
     * <uses-permission
     * android:name="android.permission.INTERNET"></uses-permission>
     * 通过获取http://fw.qq.com/ipaddress网页取得外网IP 这里有几个查看IP的网址然后提取IP试试。
     * http://ip168.com/ http://www.cmyip.com/ http://city.ip138.com/ip2city.asp
     *
     * @param ipaddr
     * @return
     */
    public String getOutNetIp(String ipaddr) {
        URL infoUrl = null;
        InputStream inStream = null;
        try {
            infoUrl = new URL(ipaddr);
            URLConnection connection = infoUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inStream, "utf-8"));
                StringBuilder strber = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                    strber.append(line + "\n");
                inStream.close();
                return strber.toString();
            }
        } catch (MalformedURLException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取手机MAC地址
     */
    public static Wifi getWifiInfo() {
        Wifi wifi = new Wifi();
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo info = wifiManager.getConnectionInfo();
            String bssid = info.getBSSID();
            String macAddress = info.getMacAddress();
            String ipAddress = intToIp(info.getIpAddress());
            wifi.setBSSID(bssid);
            wifi.setMacAddress(macAddress);
            wifi.setIp(ipAddress);
            wifi.setOutNetIp(getOutNetIp());
        }
        return wifi;
    }

    /**
     * 获取手机屏幕的尺寸
     *
     * @param activity
     * @return
     */
    private String getWeithAndHeight(Activity activity) {
        // 这种方式在service中无法使用，
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels; // 宽
        int height = dm.heightPixels; // 高
        float density = dm.density; // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
        // 在service中也能得到高和宽
        WindowManager mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        width = mWindowManager.getDefaultDisplay().getWidth();
        height = mWindowManager.getDefaultDisplay().getHeight();

        // 居中显示Toast
        Toast msg = Toast.makeText(mContext, "宽=" + width + "   高=" + height, Toast.LENGTH_LONG);
        msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2,
                msg.getYOffset() / 2);
        msg.show();
        LogUtil.log("(像素)宽:" + width + "\n" + "(像素)高:" + height + "\n"
                + "屏幕密度（0.75 / 1.0 / 1.5）:" + density + "\n"
                + "屏幕密度DPI（120 / 160 / 240）:" + densityDpi + "\n");
        return "(像素)宽:" + width + "\n" + "(像素)高:" + height + "\n"
                + "屏幕密度（0.75 / 1.0 / 1.5）:" + density + "\n"
                + "屏幕密度DPI（120 / 160 / 240）:" + densityDpi + "\n";
                    /*
                     * 下面的代码即可获取屏幕的尺寸。 在一个Activity的onCreate方法中，写入如下代码： DisplayMetrics metric
                     * = new DisplayMetrics();
                     * getWindowManager().getDefaultDisplay().getMetrics(metric); int width
                     * = metric.widthPixels; // 屏幕宽度（像素） int height = metric.heightPixels;
                     * // 屏幕高度（像素） float density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
                     * int densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
                     *
                     * 但是，需要注意的是，在一个低密度的小屏手机上，仅靠上面的代码是不能获取正确的尺寸的。
                     * 比如说，一部240x320像素的低密度手机，如果运行上述代码，获取到的屏幕尺寸是320x427。
                     * 因此，研究之后发现，若没有设定多分辨率支持的话
                     * ，Android系统会将240x320的低密度（120）尺寸转换为中等密度（160）对应的尺寸，
                     * 这样的话就大大影响了程序的编码。所以，需要在工程的AndroidManifest
                     * .xml文件中，加入supports-screens节点，具体的内容如下： <supports-screens
                     * android:smallScreens="true" android:normalScreens="true"
                     * android:largeScreens="true" android:resizeable="true"
                     * android:anyDensity="true" />
                     * 这样的话，当前的Android程序就支持了多种分辨率，那么就可以得到正确的物理尺寸了。
                     */
    }

    public static com.imchen.testhook.Entity.Location getLocationInfo() {
        final com.imchen.testhook.Entity.Location myLocation = new com.imchen.testhook.Entity.Location();
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        final Message message = new Message();
        final Bundle bundle = new Bundle();
        try {
            if (providers.contains(LocationManager.GPS_PROVIDER)) {
                //如果是GPS
                locationProvider = LocationManager.GPS_PROVIDER;
                LogUtil.log("use providers ->" + locationProvider);
            } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                //如果是Network
                locationProvider = LocationManager.NETWORK_PROVIDER;
                LogUtil.log("use providers ->" + locationProvider);
            } else {
                Toast.makeText(mContext, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
                return null;
            }
            LogUtil.log("getLocation: getProvider:" + providers.toString());
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "Permission Denied!", Toast.LENGTH_SHORT).show();
                return null;
            }
//            location = locationManager.getLastKnownLocation(locationProvider);
//            if (location == null) {
//                locationManager.requestLocationUpdates(locationProvider, 60000, 1.0f, mLocationListener);
//                LogUtil.log("getLocationInfo: \n update location ing...");
//            } else {
//                LogUtil.log("getLocationInfo: \n lat:" + location.getLatitude() + "\n lot:" + location.getLongitude());
//                myLocation.setInitLatitude(location.getLatitude());
//                myLocation.setInitLongitude(location.getLongitude());
//                return myLocation;
//            }
//            if (location != null && mLocationListener != null) {
//                locationManager.removeUpdates(mLocationListener);
//            }
            // Acquire a reference to the system Location Manager

// Define a listener that responds to location updates
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
//                    makeUseOfNewLocation(location);
                    myLocation.setInitLatitude(location.getLatitude());
                    myLocation.setInitLongitude(location.getLongitude());
                    myLocation.setAltitude(location.getAltitude());
                    myLocation.setBearing(location.getBearing());
                    myLocation.setAccuracy(location.getAccuracy());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                    LogUtil.log("getLocationInfo: \n lat:" + location.getLatitude() + "\n lot:" + location.getLongitude());
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };

// Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(locationProvider, 5000, 0, locationListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myLocation;
    }

    public static com.imchen.testhook.Entity.Location getLocation() {
        com.imchen.testhook.Entity.Location myLocation = null;
        try {
            myLocation = new com.imchen.testhook.Entity.Location();
            LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            LocationListener myGPSListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    double atitude = location.getAltitude();
                    long time = location.getTime();
                    StringBuffer sBuffer = new StringBuffer();
                    sBuffer.append("经度：" + longitude + "\n");
                    sBuffer.append("维度：" + latitude + "\n");
                    sBuffer.append("海拔：" + atitude + "\n");
                    sBuffer.append("速度：" + location.getSpeed() + "\n");
                    LogUtil.log("latitude:" + latitude);
                    LogUtil.log("longitude" + longitude);
                    LogUtil.log("atitude" + atitude);
                    LogUtil.log("time" + time);
                    LogUtil.log("location.getSpeed()=" + location.getSpeed());
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("location", location);
                    Message message = new Message();
                    message.setData(bundle);
                    message.what = 0x1;
                    MainActivity.mHandler.sendMessage(message);
                    //  Toast.makeText(thisContext, "latitude:"+latitude+ " longitude"+longitude+" location.getSpeed()="+location.getSpeed() , Toast.LENGTH_SHORT).show();
                }

                public void onStatusChanged(String provider, int status,
                                            Bundle extras) {
                    LogUtil.log("onStatusChanged provider=" + provider + " status=" + status);
                }

                public void onProviderEnabled(String provider) {
                    LogUtil.log("onProviderEnabled provider=" + provider);
                }

                public void onProviderDisabled(String provider) {
                    LogUtil.log("onProviderDisabled provider=" + provider);
                }
            };

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setAltitudeRequired(false);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_HIGH);
            String provider = locationManager.getBestProvider(criteria, true);
            List<String> providers = locationManager.getProviders(criteria, true);
            for (int i = 0; i < providers.size(); i++) {
                LogUtil.log("providers[" + i + "]=" + providers.get(i));
            }
//            LogUtil.log("provider="+provider);
            if (mContext.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, android.os.Process.myPid(), android.os.Process.myUid()) != PackageManager.PERMISSION_GRANTED) {
                LogUtil.log("Permission Denied!");
                Toast.makeText(mContext, "Permission Denied!", Toast.LENGTH_SHORT).show();
                return null;
            }
            locationManager.requestLocationUpdates(provider, 3000, 0, myGPSListener);

            locationManager.removeUpdates(myGPSListener);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.log(e);
        }
        return myLocation;
    }


//    private static LocationListener mLocationListener = new LocationListener() {
//        @Override
//        public void onLocationChanged(Location location) {
//            LogUtil.log("时间：" + location.getTime());
//            LogUtil.log("经度：" + location.getLongitude());
//            LogUtil.log("纬度：" + location.getLatitude());
//            LogUtil.log("海拔：" + location.getAltitude());
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//            LogUtil.log("onStatusChanged: ");
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//            LogUtil.log("getLocation: lat:" + location.getLatitude() + " lot:" + location.getLongitude());
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//            LogUtil.log("onProviderDisabled: ");
//        }
//    };

//    // 获取手机经纬度
//    public void getLocation() {
//        // 1. 创建一个 LocationManager对象。
//        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//
//        com.imchen.testhook.Entity.Location myLocation=new com.imchen.testhook.Entity.Location();
//        // 2. 创建一个 LocationListener对象。
//        LocationListener myGPSListener = new LocationListener() {
//            // 一旦Location发生改变就会调用这个方法
//            public void onLocationChanged(Location location) {
//                double latitude = location.getLatitude();
//                double longitude = location.getLongitude();
//                LogUtil.log("latitude:" + latitude);
//                LogUtil.log("longitude" + longitude);
//            }
//
//            public void onStatusChanged(String provider, int status,
//                                        Bundle extras) {
//            }
//
//            public void onProviderEnabled(String provider) {
//            }
//
//            public void onProviderDisabled(String provider) {
//            }
//        };
//        // 3.向LocationManager 注册一个LocationListener。
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//        criteria.setAltitudeRequired(false);
//        criteria.setCostAllowed(true);
//        criteria.setPowerRequirement(Criteria.POWER_LOW);
//        String provider = locationManager.getBestProvider(criteria, true); // 根据Criteria
//        // 的设置获取一个最佳的Provider
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            //权限检查
//            Toast.makeText(context, "Permission Denied!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        locationManager.requestLocationUpdates(provider, 5000, 0, myGPSListener);
//        // 4.移除LocationManager 注册的 LocationListener。
//        locationManager.removeUpdates(myGPSListener);
//
//    }

    /**
     * 当前网络是否连接
     */
    public boolean isNetConnecting() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            // info.setConnected(false);
            return false;
        } else {
            // info.setConnected(true);
            return true;
        }
    }

    /**
     * 获取信号强度
     */
    public static void getPhoneState() {
        // 1. 创建telephonyManager 对象。
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(TELEPHONY_SERVICE);
        // 2. 创建PhoneStateListener 对象
        PhoneStateListener MyPhoneListener = new PhoneStateListener() {
            @Override
            public void onCellLocationChanged(CellLocation location) {
                if (location instanceof GsmCellLocation) {// gsm网络
                    int CID = ((GsmCellLocation) location).getCid();
                    LogUtil.log("getPhoneState: cid--" + CID);
                } else if (location instanceof CdmaCellLocation) {// 其他CDMA等网络
                    int ID = ((CdmaCellLocation) location).getBaseStationId();
                    LogUtil.log("getPhoneState: id--" + ID);
                }
            }

            @Override
            public void onServiceStateChanged(ServiceState serviceState) {
                super.onServiceStateChanged(serviceState);
            }

            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                int asu = signalStrength.getGsmSignalStrength();
                int dbm = -113 + 2 * asu; // 信号强度
                super.onSignalStrengthsChanged(signalStrength);
            }
        };
        // 3. 监听信号改变
        telephonyManager.listen(MyPhoneListener,
                PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

                    /*
                     * 可能需要的权限 <uses-permission
                     * android:name="android.permission.WAKE_LOCK"></uses-permission>
                     * <uses-permission
                     * android:name="android.permission.ACCESS_COARSE_LOCATION"/>
                     * <uses-permission
                     * android:name="android.permission.ACCESS_FINE_LOCATION"/>
                     * <uses-permission android:name="android.permission.READ_PHONE_STATE"
                     * /> <uses-permission
                     * android:name="android.permission.ACCESS_NETWORK_STATE" />
                     */
    }

    /**
     * 获取手机可用内存和总内存
     */
    private static String getSystemMemory() {
                    /*
                     * 在Android开发中，有时候我们想获取手机的一些硬件信息，比如android手机的总内存和可用内存大小。这个该如何实现呢？
                     * 通过读取文件"/proc/meminfo"
                     * 的信息能够获取手机Memory的总量，而通过ActivityManager.getMemoryInfo
                     * (ActivityManager.MemoryInfo)方法可以获取当前的可用Memory量。
                     * "/proc/meminfo"文件记录了android手机的一些内存信息
                     * ，在命令行窗口里输入"adb shell"，进入shell环境，输入
                     * "cat /proc/meminfo"即可在命令行里显示meminfo文件的内容，具体如下所示。
                     *
                     * C:\Users\Figo>adb shell # cat /proc/meminfo cat /proc/meminfo
                     * MemTotal: 94096 kB MemFree: 1684 kB Buffers: 16 kB Cached: 27160 kB
                     * SwapCached: 0 kB Active: 35392 kB Inactive: 44180 kB Active(anon):
                     * 26540 kB Inactive(anon): 28244 kB Active(file): 8852 kB
                     * Inactive(file): 15936 kB Unevictable: 280 kB Mlocked: 0 kB *
                     * SwapTotal: 0 kB SwapFree: 0 kB Dirty: 0 kB Writeback: 0 kB AnonPages:
                     * 52688 kB Mapped: 17960 kB Slab: 3816 kB SReclaimable: 936 kB
                     * SUnreclaim: 2880 kB PageTables: 5260 kB NFS_Unstable: 0 kB Bounce: 0
                     * kB WritebackTmp: 0 kB CommitLimit: 47048 kB Committed_AS: 1483784 kB
                     * VmallocTotal: 876544 kB VmallocUsed: 15456 kB VmallocChunk: 829444 kB
                     * #
                     *
                     * 下面先对"/proc/meminfo"文件里列出的字段进行粗略解释： MemTotal: 所有可用RAM大小。 MemFree:
                     * LowFree与HighFree的总和，被系统留着未使用的内存。 Buffers: 用来给文件做缓冲大小。 Cached:
                     * 被高速缓冲存储器（cache memory）用的内存的大小（等于diskcache minus SwapCache）。
                     * SwapCached:被高速缓冲存储器（cache
                     * memory）用的交换空间的大小。已经被交换出来的内存，仍然被存放在swapfile中，
                     * 用来在需要的时候很快的被替换而不需要再次打开I/O端口。 Active:
                     * 在活跃使用中的缓冲或高速缓冲存储器页面文件的大小，除非非常必要，否则不会被移作他用。 Inactive:
                     * 在不经常使用中的缓冲或高速缓冲存储器页面文件的大小，可能被用于其他途径。 SwapTotal: 交换空间的总大小。 SwapFree:
                     * 未被使用交换空间的大小。 Dirty: 等待被写回到磁盘的内存大小。 Writeback: 正在被写回到磁盘的内存大小。
                     * AnonPages：未映射页的内存大小。 Mapped: 设备和文件等映射的大小。 Slab:
                     * 内核数据结构缓存的大小，可以减少申请和释放内存带来的消耗。 SReclaimable:可收回Slab的大小。
                     * SUnreclaim：不可收回Slab的大小（SUnreclaim+SReclaimable＝Slab）。
                     * PageTables：管理内存分页页面的索引表的大小。 NFS_Unstable:不稳定页表的大小。
                     * 要获取android手机总内存大小，只需读取"/proc/meminfo"文件的第1行，并进行简单的字符串处理即可。
                     */
        String availMemory = getAvailMemory();
        String totalMemory = getTotalMemory();
        return "可用内存=" + availMemory + "\n" + "总内存=" + totalMemory;

    }// 手机的内存信息主要在/proc/meminfo文件中，其中第一行是总内存，而剩余内存可通过ActivityManager.MemoryInfo得到。

    private static String getAvailMemory() {// 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        // mi.availMem; 当前系统的可用内存
        return Formatter.formatFileSize(mContext, mi.availMem);// 将获取的内存大小规格化
    }

    private static String getTotalMemory() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }

            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();

        } catch (IOException e) {
        }
        return Formatter.formatFileSize(mContext, initial_memory);// Byte转换为KB或者MB，内存大小规格化
    }

    // 获取手机CPU信息
    public static String getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo = {"", ""}; // 1-cpu型号 //2-cpu频率
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException e) {
        }
        LogUtil.log("cpuinfo:" + cpuInfo[0] + " cpu平率" + cpuInfo[1]);
        return "1-cpu型号:" + cpuInfo[0] + "2-cpu频率:" + cpuInfo[1];
    }// 和内存信息同理，cpu信息可通过读取/proc/cpuinfo文件来得到，其中第一行为cpu型号，第二行为cpu频率。

    /*
     * 一、Android 获取手机中已安装apk文件信息(PackageInfo、ResolveInfo)(应用图片、应用名、包名等)
     * 1、通过PackageManager可获取手机端已安装的apk文件的信息，具体代码如下: PackageManager
     * packageManager = this.getPackageManager(); List<PackageInfo>
     * packageInfoList = packageManager.getInstalledPackages(0);
     * 通过上述方法，可得到手机中安装的所有应用程序，包括手动安装的apk包的信息、、系统预装的应用软件的信息，要区分这两类软件可使用以下方法:
     * （a）从packageInfoList获取的packageInfo
     * ，再通过packageInfo.applicationInfo获取applicationInfo。
     * （b）判断(applicationInfo.flags &
     * ApplicationInfo.FLAG_SYSTEM)的值，该值大于0时，表示获取的应用为系统预装的应用，反之则为手动安装的应用。
     * (1)获取应用的代码: public static List<PackageInfo> getAllApps(Context context) {
     * List<PackageInfo> apps = new ArrayList<PackageInfo>(); PackageManager
     * pManager = context.getPackageManager(); //获取手机内所有应用 List<PackageInfo>
     * paklist = pManager.getInstalledPackages(0); for (int i = 0; i <
     * paklist.size(); i++) { PackageInfo pak = (PackageInfo) paklist.get(i);
     * //判断是否为非系统预装的应用程序 if ((pak.applicationInfo.flags &
     * pak.applicationInfo.FLAG_SYSTEM) <= 0) { apps.add(pak); } } return apps;
     * } (2)、获取图片、应用名、包名: PackageManager pManager =
     * MessageSendActivity.this.getPackageManager(); List<PackageInfo> appList =
     * Utils.getAllApps(MessageSendActivity.this); for(int
     * i=0;i<appList.size();i++) { PackageInfo pinfo = appList.get(i); shareItem
     * = new ShareItemInfo();
     * shareItem.setIcon(pManager.getApplicationIcon(pinfo.applicationInfo));
     * shareItem
     * .setLabel(pManager.getApplicationLabel(pinfo.applicationInfo).toString
     * ()); shareItem.setPackageName(pinfo.applicationInfo.packageName); }
     * 其中ShareItemInfo 类自己写的，各位可以忽略 (3)获取支持分享的应用的代码： public static
     * List<ResolveInfo> getShareApps(Context context){ List<ResolveInfo> mApps
     * = new ArrayList<ResolveInfo>(); Intent intent=new
     * Intent(Intent.ACTION_SEND,null);
     * intent.addCategory(Intent.CATEGORY_DEFAULT);
     * intent.setType("text/plain"); PackageManager pManager =
     * context.getPackageManager(); mApps =
     * pManager.queryIntentActivities(intent
     * ,PackageManager.COMPONENT_ENABLED_STATE_DEFAULT); return mApps; }
     * 由于该方法，返回的并不是PackageInfo 对象。而是ResolveInfo。因此获取图片、应用名、包名的方法不一样，如下：
     * PackageManager pManager = MessageSendActivity.this.getPackageManager();
     * List<ResolveInfo> resolveList =
     * Utils.getShareApps(MessageSendActivity.this); for(int
     * i=0;i<resolveList.size();i++) { ResolveInfo resolve = resolveList.get(i);
     * ShareItemInfo shareItem = new ShareItemInfo(); //set Icon
     * shareItem.setIcon(resolve.loadIcon(pManager)); //set Application Name
     * shareItem.setLabel(resolve.loadLabel(pManager).toString()); //set Package
     * Name shareItem.setPackageName(resolve.activityInfo.packageName); } 总结： 通过
     * PackageInfo 获取具体信息方法： 包名获取方法：packageInfo.packageName
     * icon获取获取方法：packageManager.getApplicationIcon(applicationInfo)
     * 应用名称获取方法：packageManager.getApplicationLabel(applicationInfo)
     * 使用权限获取方法：packageManager
     * .getPackageInfo(packageName,PackageManager.GET_PERMISSIONS)
     * .requestedPermissions 通过 ResolveInfo 获取具体信息方法：
     * 包名获取方法：resolve.activityInfo.packageName
     * icon获取获取方法：resolve.loadIcon(packageManager)
     * 应用名称获取方法：resolve.loadLabel(packageManager).toString()
     */
    public static String getSimCardInfo() {
        // 在manifest.xml文件中要添加
        // <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
                    /*
                     * TelephonyManager类主要提供了一系列用于访问与手机通讯相关的状态和信息的get方法。其中包括手机SIM的状态和信息
                     * 、电信网络的状态及手机用户的信息。
                     * 在应用程序中可以使用这些get方法获取相关数据。TelephonyManager类的对象可以通过Context
                     * .getSystemService(Context.TELEPHONY_SERVICE)
                     * 方法来获得，需要注意的是有些通讯信息的获取对应用程序的权限有一定的限制
                     * ，在开发的时候需要为其添加相应的权限。以下列出TelephonyManager类所有方法及说明：
                     * TelephonyManager提供设备上获取通讯服务信息的入口。 应用程序可以使用这个类方法确定的电信服务商和国家
                     * 以及某些类型的用户访问信息。 应用程序也可以注册一个监听器到电话收状态的变化。不需要直接实例化这个类
                     * 使用Context.getSystemService (Context.TELEPHONY_SERVICE)来获取这个类的实例。
                     */

        // 解释：
        // IMSI是国际移动用户识别码的简称(International Mobile Subscriber Identity)
        // IMSI共有15位，其结构如下：
        // MCC+MNC+MIN
        // MCC：Mobile Country Code，移动国家码，共3位，中国为460;
        // MNC:Mobile NetworkCode，移动网络码，共2位
        // 在中国，移动的代码为电00和02，联通的代码为01，电信的代码为03
        // 合起来就是（也是Android手机中APN配置文件中的代码）：
        // 中国移动：46000 46002
        // 中国联通：46001
        // 中国电信：46003
        // 举例，一个典型的IMSI号码为460030912121001

        // IMEI是International Mobile Equipment Identity （国际移动设备标识）的简称
        // IMEI由15位数字组成的”电子串号”，它与每台手机一一对应，而且该码是全世界唯一的
        // 其组成为：
        // 1. 前6位数(TAC)是”型号核准号码”，一般代表机型
        // 2. 接着的2位数(FAC)是”最后装配号”，一般代表产地
        // 3. 之后的6位数(SNR)是”串号”，一般代表生产顺序号
        // 4. 最后1位数(SP)通常是”0″，为检验码，目前暂备用

        TelephonyManager tm = (TelephonyManager) mContext
                .getSystemService(TELEPHONY_SERVICE);
                    /*
                     * 电话状态： 1.tm.CALL_STATE_IDLE=0 无活动，无任何状态时 2.tm.CALL_STATE_RINGING=1
                     * 响铃，电话进来时 3.tm.CALL_STATE_OFFHOOK=2 摘机
                     */
        tm.getCallState();// int

                    /*
                     * 电话方位：
                     */
        // 返回当前移动终端的位置
        CellLocation location = tm.getCellLocation();
        // 请求位置更新，如果更新将产生广播，接收对象为注册LISTEN_CELL_LOCATION的对象，需要的permission名称为
        // ACCESS_COARSE_LOCATION。
        location.requestLocationUpdate();

        /**
         * 获取数据活动状态
         *
         * DATA_ACTIVITY_IN 数据连接状态：活动，正在接受数据 DATA_ACTIVITY_OUT 数据连接状态：活动，正在发送数据
         * DATA_ACTIVITY_INOUT 数据连接状态：活动，正在接受和发送数据 DATA_ACTIVITY_NONE
         * 数据连接状态：活动，但无数据发送和接受
         */
        tm.getDataActivity();

        /**
         * 获取数据连接状态
         *
         * DATA_CONNECTED 数据连接状态：已连接 DATA_CONNECTING 数据连接状态：正在连接
         * DATA_DISCONNECTED 数据连接状态：断开 DATA_SUSPENDED 数据连接状态：暂停
         */
        tm.getDataState();

        /**
         * 返回当前移动终端的唯一标识，设备ID
         *
         * 如果是GSM网络，返回IMEI；如果是CDMA网络，返回MEID Return null if device ID is not
         * available.
         */
        String Imei = tm.getDeviceId();// String

                    /*
                     * 返回移动终端的软件版本，例如：GSM手机的IMEI/SV码。 设备的软件版本号： 例如：the IMEI/SV(software
                     * version) for GSM phones. Return null if the software version is not
                     * available.
                     */
        tm.getDeviceSoftwareVersion();// String

                    /*
                     * 手机号： GSM手机的 MSISDN. Return null if it is unavailable.
                     */
        String phoneNum = tm.getLine1Number();// String

                    /*
                     * 获取ISO标准的国家码，即国际长途区号。 注意：仅当用户已在网络注册后有效。 在CDMA网络中结果也许不可靠。
                     */
        tm.getNetworkCountryIso();// String

                    /*
                     * MCC+MNC(mobile country code + mobile network code) 注意：仅当用户已在网络注册时有效。
                     * 在CDMA网络中结果也许不可靠。
                     */
        tm.getNetworkOperator();// String

                    /*
                     * 按照字母次序的current registered operator(当前已注册的用户)的名字 注意：仅当用户已在网络注册时有效。
                     * 在CDMA网络中结果也许不可靠。                     */

        tm.getNetworkOperatorName();// String


                    /*
                     * 当前使用的网络类型： 例如： NETWORK_TYPE_UNKNOWN 网络类型未知 0 NETWORK_TYPE_GPRS GPRS网络
                     * 1 NETWORK_TYPE_EDGE EDGE网络 2 NETWORK_TYPE_UMTS UMTS网络 3
                     * NETWORK_TYPE_HSDPA HSDPA网络 8 NETWORK_TYPE_HSUPA HSUPA网络 9
                     * NETWORK_TYPE_HSPA HSPA网络 10 NETWORK_TYPE_CDMA CDMA网络,IS95A 或 IS95B. 4
                     * NETWORK_TYPE_EVDO_0 EVDO网络, revision 0. 5 NETWORK_TYPE_EVDO_A EVDO网络,
                     * revision A. 6 NETWORK_TYPE_1xRTT 1xRTT网络 7
                     */
        tm.getNetworkType();// int

                    /*
                     * 手机类型： 例如： PHONE_TYPE_NONE 无信号 PHONE_TYPE_GSM GSM信号 PHONE_TYPE_CDMA
                     * CDMA信号
                     */
        tm.getPhoneType();// int

                    /*
                     * Returns the ISO country code equivalent for the SIM provider's
                     * country code. 获取ISO国家码，相当于提供SIM卡的国家码。
                     */
        tm.getSimCountryIso();// String

                    /*
                     * Returns the MCC+MNC (mobile country code + mobile network code) of
                     * the provider of the SIM. 5 or 6 decimal digits.
                     * 获取SIM卡提供的移动国家码和移动网络码.5或6位的十进制数字. SIM卡的状态必须是
                     * SIM_STATE_READY(使用getSimState()判断).
                     */
        tm.getSimOperator();// String

                    /*
                     * 服务商名称： 例如：中国移动、联通 SIM卡的状态必须是 SIM_STATE_READY(使用getSimState()判断).
                     */
        tm.getSimOperatorName();// String

                    /*
                     * SIM卡的序列号： 需要权限：READ_PHONE_STATE
                     */
        tm.getSimSerialNumber();// String

                    /*
                     * SIM的状态信息： SIM_STATE_UNKNOWN 未知状态 0 SIM_STATE_ABSENT 没插卡 1
                     * SIM_STATE_PIN_REQUIRED 锁定状态，需要用户的PIN码解锁 2 SIM_STATE_PUK_REQUIRED
                     * 锁定状态，需要用户的PUK码解锁 3 SIM_STATE_NETWORK_LOCKED 锁定状态，需要网络的PIN码解锁 4
                     * SIM_STATE_READY 就绪状态 5
                     */
        tm.getSimState();// int

                    /*
                     * 唯一的用户ID： 例如：IMSI(国际移动用户识别码) for a GSM phone. 需要权限：READ_PHONE_STATE
                     */
        tm.getSubscriberId();// String

                    /*
                     * 取得和语音邮件相关的标签，即为识别符 需要权限：READ_PHONE_STATE
                     */

        tm.getVoiceMailAlphaTag();// String

                    /*
                     * 获取语音邮件号码： 需要权限：READ_PHONE_STATE
                     */
        tm.getVoiceMailNumber();// String

                    /*
                     * ICC卡是否存在
                     */
        tm.hasIccCard();// boolean

                    /*
                     * 是否漫游: (在GSM用途下)
                     */
        tm.isNetworkRoaming();//

        String ProvidersName = null;
        // 返回唯一的用户ID;就是这张卡的编号神马的
        String IMSI = "";
        IMSI = tm.getSubscriberId(); // 国际移动用户识别码
        // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
//        System.out.println(IMSI);
        LogUtil.log(IMSI);
        if (IMSI != null) {
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                ProvidersName = "中国移动";
            } else if (IMSI.startsWith("46001")) {

                ProvidersName = "中国联通";

            } else if (IMSI.startsWith("46003")) {

                ProvidersName = "中国电信";

            }
        } else {

        }
        // 返回当前移动终端附近移动终端的信息
                    /*
                     * 附近的电话的信息: 类型：List<NeighboringCellInfo>
                     * 需要权限：android.Manifest.permission#ACCESS_COARSE_UPDATES
                     */
        List<NeighboringCellInfo> infos = tm.getNeighboringCellInfo();
        for (NeighboringCellInfo info : infos) {
            // 获取邻居小区号
            int cid = info.getCid();
            // 获取邻居小区LAC，LAC:
            // 位置区域码。为了确定移动台的位置，每个GSM/PLMN的覆盖区都被划分成许多位置区，LAC则用于标识不同的位置区。
            info.getLac();
            info.getNetworkType();
            info.getPsc();
            // 获取邻居小区信号强度
            info.getRssi();
        }
        return "手机号码:" + phoneNum + "\n" + "服务商：" + ProvidersName + "\n"
                + "IMEI：" + Imei;

    }

    /**
     * 获取build信息
     */
    public static com.imchen.testhook.Entity.Build getBuildInfo() {
        com.imchen.testhook.Entity.Build build = new com.imchen.testhook.Entity.Build();
        Version version = new Version();
        Codes codes = new Codes();
        try {
            build.setBOOTLOADER(Build.BOOTLOADER);
            build.setBRAND(Build.BRAND);
            build.setBOARD(Build.BOARD);
            build.setCPU_ABI(Build.CPU_ABI);
            build.setCPU_ABI2(Build.CPU_ABI2);
            build.setDEVICE(Build.DEVICE);
            build.setDISPLAY(Build.DISPLAY);
            build.setFINGERPRINT(Build.FINGERPRINT);
            build.setHARDWARE(Build.HARDWARE);
            build.setHOST(Build.HOST);
            build.setID(Build.ID);
//            build.setIS_DEBUGGABLE(Build.);
            build.setMANUFACTURER(Build.MANUFACTURER);
            build.setMODEL(Build.MODEL);
            build.setPRODUCT(Build.PRODUCT);
            build.setRADIO(Build.RADIO);
            build.setSERIAL(Build.SERIAL);
            build.setTAGS(Build.TAGS);
            build.setTIME(Build.TIME);
            build.setUSER(Build.USER);

            codes.setBASE(Build.VERSION_CODES.BASE);
            version.setCODENAME(Build.VERSION.CODENAME);
            version.setCodes(codes);
            version.setRELEASE(Build.VERSION.RELEASE);
            version.setSDK(Build.VERSION.SDK);
            version.setSDK_INT(Build.VERSION.SDK_INT);
            version.setINCREMENTAL(Build.VERSION.INCREMENTAL);
//            build.set(version);
        } catch (Exception e) {
            LogUtil.log("getBuildInfo: " + e);
        }
        LogUtil.log("user: " + Build.USER);
        LogUtil.log("bootloader: " + Build.BOOTLOADER);
        LogUtil.log("board: " + Build.BOARD);
        LogUtil.log("device: " + Build.DEVICE);
        LogUtil.log("display: " + Build.DISPLAY);
        LogUtil.log("fingerPrint: " + Build.FINGERPRINT);
        LogUtil.log("host: " + Build.HOST);
        LogUtil.log("id: " + Build.ID);
        LogUtil.log("hardware: " + Build.HARDWARE);
        LogUtil.log("model: " + Build.MODEL);
        LogUtil.log("manufacturer: " + Build.MANUFACTURER);
        LogUtil.log("product: " + Build.PRODUCT);
        LogUtil.log("radioVersion: " + Build.getRadioVersion());
        LogUtil.log("serial: " + Build.SERIAL);
        LogUtil.log("time: " + Build.TIME);
        LogUtil.log("type:" + Build.TYPE);
        LogUtil.log("radio: " + Build.RADIO);
//        LogUtil.log(Build.Version.SECURITY_PATCH);
        LogUtil.log("versioncode.base: " + Build.VERSION_CODES.BASE);
        LogUtil.log("release: " + Build.VERSION.RELEASE);
        LogUtil.log("codeName: " + Build.VERSION.CODENAME);
        LogUtil.log("INCREMENTAL: " + Build.VERSION.INCREMENTAL);
        return build;
    }

    /**
     * 获取电话信息
     */
    public static Telephony getTelephonyInfo() {
        TelephonyManager telephoneManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        Telephony telephony = new Telephony();
        String DeviceId = telephoneManager.getDeviceId();
        String VoiceMailAlphaTag = telephoneManager.getVoiceMailAlphaTag();
        String VoiceMailNumber = telephoneManager.getVoiceMailNumber();
        String SubscriberId = telephoneManager.getSubscriberId();
        String Line1Number = telephoneManager.getLine1Number();
        VoiceMailNumber = VoiceMailNumber == null ? "unknown" : VoiceMailNumber;
        SubscriberId = SubscriberId == null ? "unknown" : SubscriberId;
        Line1Number = Line1Number == null ? "unknown" : Line1Number;
        String groupIdLevel1 = null;
        List<CellInfo> allCellInfo = null;
        telephony.setDeviceId(DeviceId);
        telephony.setVoiceMailAlphaTag(VoiceMailAlphaTag);
        telephony.setVoiceMailNumber(VoiceMailNumber);
        telephony.setSubscriberId(SubscriberId);
        telephony.setLine1Number(Line1Number);
        LogUtil.log("deviceId: " + DeviceId);
        LogUtil.log("voiceMailAlphaTag: " + VoiceMailAlphaTag);
        LogUtil.log("voiceMailNumber: " + VoiceMailNumber);
        LogUtil.log("subscriberId: " + SubscriberId);
        LogUtil.log("Line1Number: " + Line1Number);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            groupIdLevel1 = telephoneManager.getGroupIdLevel1();
            groupIdLevel1 = groupIdLevel1 == null ? "unknown" : groupIdLevel1;
            telephony.setGroupIdLevel1(groupIdLevel1);
            LogUtil.log("groupIdLevel1: " + groupIdLevel1);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            allCellInfo = telephoneManager.getAllCellInfo();
//            allCellInfo=allCellInfo.equals(null)?"unknown":allCellInfo;
            telephony.setAllCellInfo(allCellInfo);
//            LogUtil.log("allCellInfo: " + telephoneManager.getAllCellInfo());
        }
        return telephony;
    }

    public void getBatteryInfo() {
        BatteryManager batteryManager = (BatteryManager) mContext.getSystemService(Context.BATTERY_SERVICE);
        try {
            LogUtil.log("obj : " + batteryManager);
            Class cls = String.class.getClassLoader().loadClass("android.battery.IBatteryManager");
            Field field1 = cls.getDeclaredField("BATTERY_SCALE");
            Field field2 = cls.getDeclaredField("mCriticalBatteryLevel");
            field1.setAccessible(true);
            field2.setAccessible(true);
            String level = (String) field1.get(batteryManager);
            Method[] methods = cls.getDeclaredMethods();
            LogUtil.log("level:" + level);

        } catch (Exception e) {
            e.printStackTrace();
        }
//        LogUtil.log("batteryLevel: "+batteryManager.EXTRA_SCALE);
//        LogUtil.log("batteryScale:"+batteryManager.EXTRA_LEVEL);
    }

    /**
     * 获取显示屏信息
     *
     * @param activity
     */
    public static Screen getScreenInfo(Activity activity) {
//        DisplayManager disPlayManager= (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Screen display = new Screen();

//        dm=context.getResources().getDisplayMetrics();
        int widthPixel = dm.widthPixels;
        int heightPixel = dm.heightPixels;
        float xdpi = dm.xdpi;
        float ydpi = dm.ydpi;
        float densityDpi = dm.densityDpi;

        display.setDensityDpi(densityDpi);
        display.setHeightPixels(heightPixel);
        display.setWidthPixels(widthPixel);
        display.setXdpi(xdpi);
        display.setYdpi(ydpi);
        LogUtil.log("widthPixel: " + widthPixel);
        LogUtil.log("heightPiexl: " + heightPixel);
        LogUtil.log("xdpi: " + xdpi);
        LogUtil.log("ydpi: " + ydpi);
        LogUtil.log("densityDpi: " + densityDpi);

        return display;
    }

//    public void batteryReciver() {
//        BroadcastReceiver receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
//                int batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
//                int status = intent.getIntExtra("status", -1);
//                int health = intent.getIntExtra("health", -1);
//                LogUtil.log("level: " + batteryLevel);
//                LogUtil.log("scale: " + batteryScale);
//                LogUtil.log("status: " + status);
//                LogUtil.log("health: " + health);
//            }
//        };
//    }

    /**
     * 直接获取电池状态
     */
    public static Battery getBatteryInfo2() {
        Battery battery = new Battery();
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = mContext.registerReceiver(null, ifilter);
        try {
            level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            battery.setLevel(level);
            battery.setScale(scale);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.log("getBatteryInfo2: " + e);
        }
        LogUtil.log("level: " + level);
        LogUtil.log("scale: " + scale);
        return battery;
    }

    /**
     * 获取httpAgent
     */
    public static void getHttpAgent() {
        String javaHttpAgent = System.getProperty("http.agnet");
        String webKitAgent = null;
        try {
            Class<?> cls = String.class.getClassLoader().loadClass("com.android.org.chromium.android_webview.AwSettings$LazyDefaultUserAgent");
            Field fd = cls.getDeclaredField("sInstance");
            fd.setAccessible(true);
            webKitAgent = (String) fd.get("WebKitUserAgent");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        String webkitUserAgent=System.getProperty()
        LogUtil.log("javaAgent: " + javaHttpAgent);
        LogUtil.log("webkitAgent: " + webKitAgent);
    }

    public static void getSystemSetting() {
        String ariplaneMode = Settings.System.getString(mContext.getContentResolver(), Settings.System.AIRPLANE_MODE_ON);
        String adbEnabled = Settings.System.getString(mContext.getContentResolver(), Settings.Global.ADB_ENABLED);
        LogUtil.log("airplaneModeOn: " + ariplaneMode);
        LogUtil.log("adbEnabled: " + adbEnabled);
    }

    // 显示信息对话框
    public void showDialog(String title, String info) {
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(info)
                .setPositiveButton("close",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                                dialog = null;
                            }
                        }).create();
        dialog.show();
    }

    public static String getOutNetIp() {
        String ip = "";
        InputStream inStream = null;
        try {
            URL infoUrl = new URL("http://1212.ip138.com/ic.asp");
            URLConnection connection = infoUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "gb2312"));
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    //builder.append(line).append("\n");
                }
                inStream.close();
                int start = builder.indexOf("[");
                int end = builder.indexOf("]");
                ip = builder.substring(start + 1, end);
                return ip;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
