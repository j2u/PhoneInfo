package com.imchen.hookbinder;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.imchen.hookbinder.hook.BinderHookHandler;
import com.imchen.hookbinder.hook.HookManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "imchen";
    private Button mLocationButton;
    private Button mGetLocationButton;
    private LocationManager locationManager;
    private String provider;
    private String locationProvider;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocationButton = (Button) findViewById(R.id.hooklocation);
        mGetLocationButton = (Button) findViewById(R.id.getLocation);
        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHookLocation();
            }
        });
        mGetLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
    }

    public void getLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"Permission denied!",Toast.LENGTH_SHORT).show();
            return;
        }
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
            Log.d(TAG, "use providers ->"+locationProvider);
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
            Log.d(TAG, "use providers ->"+locationProvider);
        } else {
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "getLocation: getProvider:" + providers.toString());
//        getProvider();

        location = locationManager.getLastKnownLocation(locationProvider);
        if (location == null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 1.0f, mLocationListener);
            Log.d(TAG, "requestLocationUpdates-> "+location);
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, mLocationListener);
            return;
        }
        getSCache(null);
        Log.d(TAG, "getLocation: 经度：longit:" + location.getLongitude() + " 纬度lati:" + location.getLatitude());
        locationManager.removeUpdates(mLocationListener);
    }

    public void startHookLocation() {
        Toast.makeText(getApplicationContext(), "start Hook", Toast.LENGTH_SHORT).show();
        HookManager.hookLocation();
    }


    private void getProvider() {
        // TODO Auto-generated method stub
        // 构建位置查询条件
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);  //高精度
        criteria.setAltitudeRequired(false);  //不查询海拔
        criteria.setBearingRequired(false);  //不查询方位
        criteria.setCostAllowed(true);  //不允许付费
        criteria.setPowerRequirement(Criteria.POWER_LOW);  //低耗
        // 返回最合适的符合条件的 provider ，第 2 个参数为 true 说明 , 如果只有一个 provider 是有效的 , 则返回当前  provider
        provider = locationManager.getBestProvider(criteria, true);
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

    public static Object getSCache(String key) {
        try {
            // 1. 获取系统自己的Binder
            Class serviceManager = Class.forName("android.os.ServiceManager");
            Method getService = serviceManager.getDeclaredMethod("getService", String.class);
            IBinder binder = (IBinder) getService.invoke(null, Context.LOCATION_SERVICE);
            // 2. 创建我们自己的Binder
            ClassLoader classLoader = binder.getClass().getClassLoader();
            Class[] interfaces = {IBinder.class};
            BinderHookHandler handler = new BinderHookHandler(binder);
            IBinder myBinder = (IBinder) Proxy.newProxyInstance(classLoader, interfaces, handler);
            // 3. 获取ServiceManager中的sCache
            Field sCache = serviceManager.getDeclaredField("sCache");
            sCache.setAccessible(true);

            // 4. 将自定义的Binder替换掉旧的系统Binder
//            cache.put(Context.LOCATION_SERVICE,myBinder);

            Log.d(TAG, "hookLocation: " + sCache.get(Context.LOCATION_SERVICE));
            sCache.setAccessible(false);
            return sCache.get(Context.LOCATION_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getSystemStatus(){
        PowerManager powerManager= (PowerManager) getSystemService(Context.POWER_SERVICE);
        Log.d(TAG, "getSystemStatus: ");
    }
}
