package com.imchen.testhook;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.imchen.testhook.utils.FloatViewUtil;
import com.imchen.testhook.utils.HttpUtil;
import com.imchen.testhook.utils.JsonUtil;
import com.imchen.testhook.utils.LogUtil;
import com.imchen.testhook.utils.PhoneInfoUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "imchen";
    private Button mHelloBtn;
    private Button mWifiBtn;
    private Button mLocationBtn;
    private Button mPropertiesBtn;
    private Button mOneKeyBtn;
    private Button mCommitBtn;
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
    private static TextView mTvDump;
    public static TextView mLocationTv;

    private ProgressDialog progressDialog;
    private PhoneInfoUtil phoneInfoUtil;
    private PhoneInfoService service;
    private Context mContext;

    private static String dumpViewContent = "";

    public final static int REFRESH_FLOAT_VIEW = 0x123;

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
        phoneInfoUtil = new PhoneInfoUtil(getApplicationContext());
        JsonUtil.getJo(null);
        JsonUtil.writeJson();
        LogUtil.log("calling uid: " + Binder.getCallingUid());
        LogUtil.log("Model: " + android.os.Build.MODEL);
        LogUtil.log("Manufacture:" + android.os.Build.MANUFACTURER);

        Intent intent = new Intent(MainActivity.this, ReadViewService.class);
        startService(intent);
    }

    private void findViewInit() {
        mHelloBtn = (Button) findViewById(R.id.btn_hello);
        mWifiBtn = (Button) findViewById(R.id.btc_wifi);
        mLocationBtn = (Button) findViewById(R.id.btn_location);
        mPropertiesBtn = (Button) findViewById(R.id.btn_properties);
        mPropertiesBtn = (Button) findViewById(R.id.btn_properties);
        mOneKeyBtn = (Button) findViewById(R.id.btn_onekey);
        mCommitBtn = (Button) findViewById(R.id.btn_commit_info);
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
//        mTvDump = (TextView) FloatViewUtil.mLinearLayout.findViewById(R.id.tv_dump);
    }

    private void listenerInit() {
//        mHelloBtn.setOnClickListener(helloListener);
//        mWifiBtn.setOnClickListener(wifiClickListener);
//        mLocationBtn.setOnClickListener(locationClickListenr);
        mOneKeyBtn.setOnClickListener(commitOnClickListener);

        //获取settings
        mUninstallBtn.setOnClickListener(unInstallListener);
        mRcViewBtn.setOnClickListener(rcBtnOnClickListener);
        mInstallBtn.setOnClickListener(this);
        mAirPlaneBtn.setOnClickListener(this);
        mServiceBtn.setOnClickListener(this);
//        mCommitBtn.setOnClickListener(commitOnClickListener);
    }

//    public View.OnClickListener helloListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Toast.makeText(getApplicationContext(), "click time:" + time, Toast.LENGTH_SHORT).show();
//            time++;
//            getBluetoothInfo();
//        }
//    };

//    public View.OnClickListener wifiClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            getWifiInfo();
//        }
//    };

//    public View.OnClickListener locationClickListenr = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            getLocationInfo();
//        }
//    };

//    public View.OnClickListener oneKeyOnClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            phoneInfoUtil.getAllInfo(MainActivity.this);
//        }
//    };

    public View.OnClickListener commitOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new Thread(myRunable).start();
//            new updateViewTask().execute(service);
        }
    };

    Runnable myRunable = new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            service = new PhoneInfoService();
            String info = service.getAllPhoneInfo(getApplicationContext());

            new updateViewTask().execute(service);
//            LogUtil.log("info.json: " + info);
            HttpUtil.doPost("192.168.1.123", info);
            Looper.loop();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
            mBluetoothTv.setText("MAC Address: " + bluetooth.getAddress());
            mBatteryTv.setText("Battery Level: " + battery.getLevel() + "\nBattery Scale: " + battery.getScale());
            mTelePhoneTv.setText("DeviceId: " + telephony.getDeviceId() + "\nVoiceMailAlphaTag: " + telephony.getVoiceMailAlphaTag() +
                    "\nGroupIdLevel1: " + telephony.getGroupIdLevel1());
            mLocationTv.setText(location.toString());
            mWifiTv.setText("Wifi MAC: " + wifi.getMacAddress() + "\nWifi BSSID: " + wifi.getBSSID() + "\nIP: " + wifi.getIp());
            mBuildTv.setText(build.toString() + build.getVERSION().toString() + "\n" + build.getVERSION().getCodes().toString());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.log(e);
        }
    }

    private View.OnClickListener settingListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PhoneInfoUtil.getSystemSetting();
        }
    };

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

    private View.OnClickListener installBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            Intent
        }
    };
}
