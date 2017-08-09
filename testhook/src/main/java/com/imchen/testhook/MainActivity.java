package com.imchen.testhook;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
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
import com.imchen.testhook.utils.HttpUtil;
import com.imchen.testhook.utils.LogUtil;
import com.imchen.testhook.utils.PhoneInfoUtil;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "imchen";
    private Button mHelloBtn;
    private Button mWifiBtn;
    private Button mLocationBtn;
    private Button mPropertiesBtn;
    private Button mOneKeyBtn;
    private Button mCommitBtn;
    private Button mSetteings;

    private TextView mBluetoothTv;
    private TextView mWifiTv;
    private TextView mBatteryTv;
    private TextView mTelePhoneTv;
    private TextView mBuildTv;
    public static TextView mLocationTv;

    private ProgressDialog progressDialog;
    private PhoneInfoUtil phoneInfoUtil;
    private PhoneInfoService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewInit();
        listenerInit();
        phoneInfoUtil = new PhoneInfoUtil(getApplicationContext());
    }

    private void findViewInit() {
        mHelloBtn = (Button) findViewById(R.id.btn_hello);
        mWifiBtn = (Button) findViewById(R.id.btc_wifi);
        mLocationBtn = (Button) findViewById(R.id.btn_location);
        mPropertiesBtn = (Button) findViewById(R.id.btn_properties);
        mPropertiesBtn = (Button) findViewById(R.id.btn_properties);
        mOneKeyBtn = (Button) findViewById(R.id.btn_onekey);
        mCommitBtn = (Button) findViewById(R.id.btn_commit_info);
        mSetteings = (Button) findViewById(R.id.btn_setting);

        mBluetoothTv = (TextView) findViewById(R.id.tv_bluetooth_info);
        mBatteryTv = (TextView) findViewById(R.id.tv_battery_info);
        mWifiTv = (TextView) findViewById(R.id.tv_wifi_info);
        mLocationTv = (TextView) findViewById(R.id.tv_location_info);
        mBuildTv = (TextView) findViewById(R.id.tv_build_info);
        mTelePhoneTv = (TextView) findViewById(R.id.tv_telephone_info);
    }

    private void listenerInit() {
//        mHelloBtn.setOnClickListener(helloListener);
//        mWifiBtn.setOnClickListener(wifiClickListener);
//        mLocationBtn.setOnClickListener(locationClickListenr);
        mOneKeyBtn.setOnClickListener(commitOnClickListener);

        //获取settings
        mSetteings.setOnClickListener(settingListener);
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
}
