package com.imchen.testhook;

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
import android.provider.Settings;
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
import com.imchen.testhook.utils.ContextUtil;
import com.imchen.testhook.utils.JsonUtil;
import com.imchen.testhook.utils.LogUtil;
import com.imchen.testhook.utils.PhoneInfoUtil;
import com.imchen.testhook.utils.Utils;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@SuppressWarnings("WrongConstant")
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "imchen";
    private Button mHelloBtn;
    private Button mWifiBtn;
    private Button mLocationBtn;
    private Button mPropertiesBtn;
    private Button mOneKeyBtn;
    private Button mStartScriptBtn;
    private Button mUninstallBtn;
    private Button mRcViewBtn;
    private Button mInstallBtn;
    private Button mAirPlaneBtn;
    private Button mServiceBtn;

    private TextView mBluetoothTv;
    private TextView mWifiTv;
    private TextView mBatteryTv;
    private TextView mTelePhoneTv;
    private TextView mBuildTv;
    private TextView mDirTv;
    private static TextView mTvDump;
    public static TextView mLocationTv;

    private ProgressDialog progressDialog;
    private PhoneInfoUtil phoneInfoUtil;
    private PhoneInfoService service;
    private Context mContext;

    private static String dumpViewContent = "";

    public final static int REFRESH_FLOAT_VIEW = 0x123;

    public String applicationDir;
    public ArrayList<String> fileList;

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
        setContentView(R.layout.activity_main);
//        FloatViewUtil.addFloatView(mContext);
        findViewInit();
        listenerInit();
        JsonUtil.getJo(null);
        JsonUtil.writeJson();
//        LogUtil.log("calling uid: " + Binder.getCallingUid());
//        LogUtil.log("Model: " + android.os.Build.MODEL);
//        LogUtil.log("Manufacture:" + android.os.Build.MANUFACTURER);

        Intent intent = new Intent(MainActivity.this, ReadViewService.class);
        startService(intent);
        initDir("/sdcard/testhook/test"+formatTime(new Date())+".json");
//        testAirplaneMode();


    }

    private void findViewInit() {
        mHelloBtn = (Button) findViewById(R.id.btn_hello);
        mWifiBtn = (Button) findViewById(R.id.btc_wifi);
        mLocationBtn = (Button) findViewById(R.id.btn_location);
        mPropertiesBtn = (Button) findViewById(R.id.btn_properties);
        mPropertiesBtn = (Button) findViewById(R.id.btn_properties);
        mOneKeyBtn = (Button) findViewById(R.id.btn_onekey);
        mStartScriptBtn = (Button) findViewById(R.id.btn_startScript);
        mUninstallBtn = (Button) findViewById(R.id.btn_uninstall);
        mRcViewBtn = (Button) findViewById(R.id.btn_rcview);
        mInstallBtn = (Button) findViewById(R.id.btn_install);
        mAirPlaneBtn = (Button) findViewById(R.id.btn_airplane);
        mServiceBtn = (Button) findViewById(R.id.btn_service);

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
        mUninstallBtn.setOnClickListener(unInstallListener);
        mRcViewBtn.setOnClickListener(rcBtnOnClickListener);
        mInstallBtn.setOnClickListener(this);
        mAirPlaneBtn.setOnClickListener(this);
        mServiceBtn.setOnClickListener(this);
        mStartScriptBtn.setOnClickListener(this);
    }

    Runnable myRunable = new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            service = new PhoneInfoService();
            String info = service.getAllPhoneInfo();//获取信息
//            setViewInfo(service);
            new updateViewTask().execute(service);
//            LogUtil.log("info.json: " + info);
//            HttpUtil.doPost("192.168.1.123", info);
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
            case R.id.btn_startScript:
                JSONObject jsonObject = new JSONObject();
                try {
                    Intent scriptIntent = new Intent("com.myrom.mm.script");
                    scriptIntent.setPackage("com.tencent.mm");
                    bindService(scriptIntent, new ServiceConnection() {
                        @Override
                        public void onServiceConnected(ComponentName name, IBinder service) {
                            LogUtil.log("connected success! " + name);
                        }

                        @Override
                        public void onServiceDisconnected(ComponentName name) {
                            LogUtil.log("disconnected!!!" + name);
                        }
                    }, Context.BIND_AUTO_CREATE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            progressDialog.cancel();

        }
    }

    public void setViewInfo(PhoneInfoService service) {
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
            mBluetoothTv.setText("MAC Address: " + bluetooth.getAddress());
            mBatteryTv.setText("Battery Level: " + battery.getLevel() + "\nBattery Scale: " + battery.getScale());
            mTelePhoneTv.setText("Imei: " + telephony.getDeviceId() + "\nImsi:" + telephony.getSubscriberId() + "\nVoiceMailAlphaTag: " + telephony.getVoiceMailAlphaTag() +
                    "\nGroupIdLevel1: " + telephony.getGroupIdLevel1());
            mLocationTv.setText(location.toString());
            mWifiTv.setText("Wifi MAC: " + wifi.getMacAddress() + "\nWifi BSSID: " + wifi.getBSSID() + "\nIP: " + wifi.getIp() +
                    "\nOutNetIP:" + wifi.getOutNetIp());
            mBuildTv.setText(build.toString() + build.getVERSION().toString() + "\n" + build.getVERSION().getCodes().toString());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.log(e);
        }
    }

    private View.OnClickListener unInstallListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), ScrollingActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener rcBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, PackageActivity.class);
            startActivity(intent);
        }
    };

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

    public void initDir(String baseDir) {
        File file = new File(baseDir);
        File dir = new File(file.getParent());
        File newFile=new File(Environment.getExternalStorageDirectory()+"/test.txt");
        LogUtil.log("sdEnv: "+ Environment.getExternalStorageDirectory());
        try {
        newFile.createNewFile();
            LogUtil.log("absPath:"+newFile.getAbsolutePath()+" proc:"+ Process.myUid());
            if (!dir.exists()) {
                dir.mkdirs();

                file.createNewFile();

            } else {
                if (!file.exists()) {
                    file.createNewFile();
                }
            }
        File file1=new File(Environment.getExternalStorageDirectory()+"/10098/testhook/test.txt");
        file1.createNewFile();
        Utils.wirteStringToFile(file1.getPath(),"dfafdsfgaerwqrqwrwrq");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String result=Utils.readSDCardFile(Environment.getExternalStorageDirectory()+"/testhook/test.txt");
        LogUtil.log("read result: "+result);
        applicationDir = file.getParent();
        fileList = new ArrayList<>();
        for (File list : dir.listFiles()
                ) {
            fileList.add(list.getName());
        }
    }

    public String formatTime(Date date) {
        SimpleDateFormat spdf = new SimpleDateFormat("hh-mm-ss");
        return spdf.format(date);
    }

}
