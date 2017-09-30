package com.imchen.testhook;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.imchen.testhook.Entity.Battery;
import com.imchen.testhook.Entity.Bluetooth;
import com.imchen.testhook.Entity.Build;
import com.imchen.testhook.Entity.Telephony;
import com.imchen.testhook.Entity.Wifi;
import com.imchen.testhook.service.PhoneInfoService;
import com.imchen.testhook.service.ReadViewService;
import com.imchen.testhook.service.ScriptService;
import com.imchen.testhook.utils.ContextUtil;
import com.imchen.testhook.utils.LogUtil;
import com.imchen.testhook.utils.PermissionUtil;
import com.imchen.testhook.utils.Utils;
import com.imchen.testhook.utils.ViewUtil;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@SuppressWarnings("WrongConstant")
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "imchen";
    private Button mHelloBtn;
    private Button mWifiBtn;
    private Button mLocationBtn;
    private Button mSocketBtn;
    private Button mOneKeyBtn;
    private Button mStartScriptBtn;
    private Button mUninstallBtn;
    private Button mRcViewBtn;
    private Button mInstallBtn;
    private Button mAirPlaneBtn;
    private Button mServiceBtn;
    private Button mCameraBtn;

    private static TextView mBluetoothTv;
    private static TextView mWifiTv;
    private static TextView mBatteryTv;
    private static TextView mTelePhoneTv;
    private static TextView mBuildTv;
    private static TextView mDirTv;
    private static TextView mTvDump;
    public static TextView mLocationTv;

    private ProgressDialog progressDialog;
    private static PhoneInfoService service;
    private Context mContext;
    private static Activity mActivity;

    private Intent socketIntent;
    private serviceSocketConnection socketConnection;

    public static int applying = 0;
    public static PermissionUtil.IRequestPermissionListener mPermissionListener;


    private static String dumpViewContent = "";

    public final static int REFRESH_FLOAT_VIEW = 0x123;
    public final static int REQUEST_PERMISSION_RESULT = 0x112;
    public final static int SET_VIEW_INFO = 0x111;
    public final static int NETWORK_EXCEPTION = 0x404;
    private final static String PHONE_INFO_API = "http://192.168.1.99:8080/adserver/manager/device/newconfig";

    public static String applicationDir;
    public static ArrayList<String> fileList;

    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_FLOAT_VIEW:
                    if (mTvDump.getText() != null && !"".equals(mTvDump.getText())) {
                        dumpViewContent = mTvDump.getText().toString();
                    }
                    String tmp = (String) msg.obj;
                    mTvDump.setText(dumpViewContent + "\n" + tmp);
                    LogUtil.log("content: " + dumpViewContent + " tmp: " + tmp);
                    break;
                case 0x1:
                    Location location = msg.getData().getParcelable("location");
                    MainActivity.mLocationTv.setText("Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude() +
                            "\nAccuracy: " + location.getAccuracy() + "\nAltitude: " + location.getAltitude() + "\nSpeed: " + location.getSpeed() +
                            "\nTime: " + location.getTime());
                    break;
                case REQUEST_PERMISSION_RESULT:
                    applying = 0;
                    HashMap<String, Integer> resultMaps = (HashMap<String, Integer>) msg.obj;
                    for (String key : resultMaps.keySet()) {
                        int result = resultMaps.get(key);
                    }
                    break;
                case SET_VIEW_INFO:

                    break;
                case NETWORK_EXCEPTION:
                    String exception = msg.obj.toString();
                    ViewUtil.hintDialog(mActivity, "Error info!", exception, "Tell Me");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ContextUtil();
        mContext = getApplicationContext();
        mActivity = MainActivity.this;
        setContentView(R.layout.activity_main);
//        FloatViewUtil.addFloatView(mContext);
        findViewInit();
        listenerInit();
//        JsonUtil.getJo(null);
//        JsonUtil.writeJson();
//        LogUtil.log("calling uid: " + Binder.getCallingUid());
//        LogUtil.log("Model: " + android.os.Build.MODEL);
//        LogUtil.log("Manufacture:" + android.os.Build.MANUFACTURER);

        Intent intent = new Intent(MainActivity.this, ReadViewService.class);
        startService(intent);
        if (!PermissionUtil.checkPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PermissionUtil.requestPermission(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, PermissionUtil.READ_WRITE_EXTERNAL_STORAGE, new PermissionUtil.IRequestPermissionListener() {
                @Override
                public void success(String permissionName) {
                    initDir("/sdcard/testhook/test" + formatTime(new Date()) + ".json");
                }

                @Override
                public void fail(String permissionName) {

                }
            });
        } else {
            initDir("/sdcard/testhook/test" + formatTime(new Date()) + ".json");
        }

//        testAirplaneMode();


    }

    private void findViewInit() {
        mHelloBtn = (Button) findViewById(R.id.btn_hello);
        mWifiBtn = (Button) findViewById(R.id.btc_wifi);
        mLocationBtn = (Button) findViewById(R.id.btn_location);
        mSocketBtn = (Button) findViewById(R.id.btn_socket_server);
        mOneKeyBtn = (Button) findViewById(R.id.btn_onekey);
        mStartScriptBtn = (Button) findViewById(R.id.btn_startScript);
        mUninstallBtn = (Button) findViewById(R.id.btn_uninstall);
        mRcViewBtn = (Button) findViewById(R.id.btn_rcview);
        mInstallBtn = (Button) findViewById(R.id.btn_install);
        mAirPlaneBtn = (Button) findViewById(R.id.btn_airplane);
        mServiceBtn = (Button) findViewById(R.id.btn_service);
        mCameraBtn= (Button) findViewById(R.id.btn_camera);

        mBluetoothTv = (TextView) findViewById(R.id.tv_bluetooth_info);
        mBatteryTv = (TextView) findViewById(R.id.tv_battery_info);
        mWifiTv = (TextView) findViewById(R.id.tv_wifi_info);
        mLocationTv = (TextView) findViewById(R.id.tv_location_info);
        mBuildTv = (TextView) findViewById(R.id.tv_build_info);
        mTelePhoneTv = (TextView) findViewById(R.id.tv_telephone_info);
        mDirTv = (TextView) findViewById(R.id.tv_dir);
//        mTvDump = (TextView) FloatViewUtil.mLinearLayout.findViewById(R.id.tv_dump);
    }

    private void listenerInit() {
        mOneKeyBtn.setOnClickListener(this);

        //获取settings
        mUninstallBtn.setOnClickListener(this);
        mRcViewBtn.setOnClickListener(this);
        mInstallBtn.setOnClickListener(this);
        mAirPlaneBtn.setOnClickListener(this);
        mServiceBtn.setOnClickListener(this);
        mStartScriptBtn.setOnClickListener(this);
        mSocketBtn.setOnClickListener(this);
        mCameraBtn.setOnClickListener(this);
    }

    Runnable myRunable = new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            try {
                service = new PhoneInfoService();
                String jsonInfo = service.getAllPhoneInfo(MainActivity.this);//获取信息
                new updateViewTask().execute(service);
                LogUtil.log("info.json: " + jsonInfo);
//                String result = HttpUtil.doPost(PHONE_INFO_API, jsonInfo);
//                JSONObject json = new JSONObject(result);
//                result = json.getInt("code") == 0 ? "Success!" : "Fail!";
//                Toast.makeText(mContext, "Post info " + result, Toast.LENGTH_SHORT).show();
//                LogUtil.log("post result: " + result);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Looper.loop();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_onekey:
                new Thread(myRunable).start();
                break;
            case R.id.btn_install:
                Intent intent = new Intent(MainActivity.this, InstallActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_airplane:
                ContentResolver resolver = mContext.getContentResolver();
                Settings.System.putInt(resolver, Settings.System.AIRPLANE_MODE_ON, 1);
                Intent intent1 = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
                intent1.putExtra("status", true);
                mContext.sendBroadcast(intent1);
            case R.id.btn_service:
                Intent intent2 = new Intent(MainActivity.this, StartscriptActivity.class);
                startActivity(intent2);
                break;
            case R.id.btn_socket_server:
                Intent consoleIntent=new Intent(MainActivity.this,ConsoleActivity.class);
                startActivity(consoleIntent);
                break;
            case R.id.btn_startScript:
                JSONObject jsonObject = new JSONObject();
                try {
                    Intent scriptIntent = new Intent("com.myrom.mm.script");
                    scriptIntent.setPackage("com.tencent.mm");
                    bindService(scriptIntent,new serviceSocketConnection() , Context.BIND_AUTO_CREATE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_rcview:
                Intent rcIntent = new Intent(MainActivity.this, PackageActivity.class);
                startActivity(rcIntent);
                break;
            case R.id.btn_uninstall:
                Intent scIntent = new Intent();
                scIntent.setClass(getApplicationContext(), ScrollingActivity.class);
                startActivity(scIntent);
                break;
            case R.id.btn_camera:
                Intent openCamera=new Intent(MainActivity.this,MyCamera.class);
                startActivity(openCamera);
                break;
            default:
                break;
        }
    }

    private class updateViewTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Please wait!");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Object o) {
            setViewInfo(service);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            progressDialog.cancel();

        }
    }

    public static void setViewInfo(PhoneInfoService service) {
        Battery battery = service.info.getBattery();
        Bluetooth bluetooth = service.info.getBluetooth();
        com.imchen.testhook.Entity.Location location = service.info.getLocation();
        Wifi wifi = service.info.getWifi();
        Telephony telephony = service.info.getTelephony();
        Build build = service.info.getBuild();
        try {
            String line = "";
            if (fileList != null) {
                for (String fileName : fileList
                        ) {
                    line += fileName + "\n";
                }
            }
            mDirTv.setText("ApplicatoinDir: " + applicationDir + "\nfile: " + line);
            mBluetoothTv.setText(bluetooth.toString());
            mBatteryTv.setText(battery.toString());
            mTelePhoneTv.setText(telephony.toString());
            mLocationTv.setText(location.toString());
            mWifiTv.setText(wifi.toString());
            mBuildTv.setText(build.toString());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.log(e);
        }
    }




    public void initDir(String baseDir) {
        File file = new File(baseDir);
        File dir = new File(file.getParent());
        File newFile = new File(Environment.getExternalStorageDirectory() + "/test.txt");
        LogUtil.log("sdEnv: " + Environment.getExternalStorageDirectory());
        try {
            newFile.createNewFile();
            LogUtil.log("absPath:" + newFile.getAbsolutePath() + " proc:" + Process.myUid());
            if (!dir.exists()) {
                dir.mkdirs();

                file.createNewFile();

            } else {
                if (!file.exists()) {
                    file.createNewFile();
                }
            }
//            File file1 = new File(Environment.getExternalStorageDirectory() + "/10098/testhook/test.txt");
//            file1.createNewFile();
//            Utils.wirteStringToFile(file1.getPath(), "dfafdsfgaerwqrqwrwrq");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String result = Utils.readSDCardFile(Environment.getExternalStorageDirectory() + "/testhook/test.txt");
        LogUtil.log("read result: " + result);
        applicationDir = file.getParent();
        fileList = new ArrayList<>();
        for (File list : dir.listFiles()
                ) {
            fileList.add(list.getName());
        }
    }

    public String formatTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh-mm-ss");
        return sdf.format(date);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        StringBuilder builder = new StringBuilder();
        Message msg = new Message();
        msg.what = REQUEST_PERMISSION_RESULT;
        HashMap<String, Integer> resultMaps = new HashMap<>();
        for (int i = 0; i < permissions.length; i++) {
            resultMaps.put(permissions[i], grantResults[i]);
            if (grantResults[i] == 0) {
                mPermissionListener.success(permissions[i]);
            } else {
                mPermissionListener.fail(permissions[i]);
            }
//            builder.append(permissions[i]);
//            builder.append(" ");
//            String result = grantResults[i] == 0 ? " success!" : " fail!";
//            Toast.makeText(getApplicationContext(), "Apply permission :" + builder.toString() + result, Toast.LENGTH_SHORT).show();
        }
        applying = 0;
        msg.obj = resultMaps;
        mHandler.sendMessage(msg);
    }

    //don't have permission
    public void testAirplaneMode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        LogUtil.log(Settings.System.getInt(getApplicationContext().getContentResolver(), Settings.System.AIRPLANE_MODE_ON));
                        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        LogUtil.log("wifi address: " + manager.getConnectionInfo().getIpAddress());
                    } catch (Settings.SettingNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public class serviceSocketConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unbindService(socketConnection);
    }
}
