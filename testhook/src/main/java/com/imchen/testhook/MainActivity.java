package com.imchen.testhook;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.os.health.SystemHealthManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.imchen.testhook.service.PhoneInfoService;
import com.imchen.testhook.utils.HttpUtil;
import com.imchen.testhook.utils.LogUtil;
import com.imchen.testhook.utils.PhoneInfoUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "imchen";
    private Button mHelloBtn;
    private Button mWifiBtn;
    private Button mLocationBtn;
    private Button mPropertiesBtn;
    private Button mOneKeyBtn;
    private Button mCommitBtn;
    private int time = 0;
    private String locationProvider;
    private Location location;
    private PhoneInfoUtil phoneInfoUtil;

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
        mCommitBtn= (Button) findViewById(R.id.btn_commit_info);
    }

    private void listenerInit() {
        mHelloBtn.setOnClickListener(helloListener);
        mWifiBtn.setOnClickListener(wifiClickListener);
        mLocationBtn.setOnClickListener(locationClickListenr);
        mOneKeyBtn.setOnClickListener(oneKeyOnClickListener);
        mCommitBtn.setOnClickListener(commitOnClickListener);
    }

    public View.OnClickListener helloListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "click time:" + time, Toast.LENGTH_SHORT).show();
            time++;
            getBluetoothInfo();
        }
    };

    public View.OnClickListener wifiClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getWifiInfo();
        }
    };

    public View.OnClickListener locationClickListenr = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getLocationInfo();
        }
    };

    public View.OnClickListener oneKeyOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            phoneInfoUtil.getAllInfo(MainActivity.this);
        }
    };

    public View.OnClickListener commitOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new Thread(myRunable).start();
        }
    };

    Runnable myRunable=new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            PhoneInfoService service=new PhoneInfoService();
            String info=service.getAllPhoneInfo();
            LogUtil.log("info.json: "+info);
            HttpUtil.doPost("192.168.1.123",info);
            Looper.loop();
        }
    };

//    public Thread commitThread=new Thread(myRunable);
//        @Override
//        public void run() {
//            Looper.prepare();
//            PhoneInfoService service=new PhoneInfoService();
//            String info=service.getAllPhoneInfo();
//            LogUtil.log("info.json: "+info);
//            HttpUtil.doPost("192.168.1.123",info);
//            Looper.loop();
//        }
//    });

    public void getBluetoothInfo() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter.isEnabled()) {
            Log.d(TAG, "getBluetoothInfo: address->>>" + adapter.getAddress());
        }
    }

    public void getBluetoothInfo2() {

    }

    public void getWifiInfo() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo info = wifiManager.getConnectionInfo();
            String bssid = info.getBSSID();
            String macAddress = info.getMacAddress();
            String ipAddress = intToIp(info.getIpAddress());
            Log.d(TAG, "getWifiInfo: \n bssid->>>" + bssid + "\n madAddress->>>" + macAddress + "\n ipAddress->>" + ipAddress);
        }
    }

    public void getProperties() {

    }

    public void getLocationInfo() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
            Log.d(TAG, "use providers ->" + locationProvider);
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
            Log.d(TAG, "use providers ->" + locationProvider);
        } else {
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "getLocation: getProvider:" + providers.toString());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
            return;
        }
        location = locationManager.getLastKnownLocation(locationProvider);
        if (location == null) {
            locationManager.requestLocationUpdates(locationProvider, 60000, 1.0f, mLocationListener);
            Log.d(TAG, "getLocationInfo: \n update location ing...");
        } else {
            Log.d(TAG, "getLocationInfo: \n lat:" + location.getLatitude() + "\n lot:" + location.getLongitude());
        }
        if (location != null && mLocationListener != null) {
            locationManager.removeUpdates(mLocationListener);
        }
    }

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "时间：" + location.getTime());
            Log.i(TAG, "经度：" + location.getLongitude());
            Log.i(TAG, "纬度：" + location.getLatitude());
            Log.i(TAG, "海拔：" + location.getAltitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged: ");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "getLocation: lat:" + location.getLatitude() + " lot:" + location.getLongitude());
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled: ");
        }
    };

    /**
     * 整形转ip
     *
     * @param ip
     * @return
     */
    private String intToIp(int ip) {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "."
                + ((ip >> 24) & 0xFF);
    }
}
