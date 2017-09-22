package com.imchen.testhook.service;

import android.Manifest;
import android.app.Activity;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.imchen.testhook.Entity.Battery;
import com.imchen.testhook.Entity.Bluetooth;
import com.imchen.testhook.Entity.Build;
import com.imchen.testhook.Entity.Location;
import com.imchen.testhook.Entity.PhoneInfo;
import com.imchen.testhook.Entity.Screen;
import com.imchen.testhook.Entity.Telephony;
import com.imchen.testhook.Entity.Wifi;
import com.imchen.testhook.utils.PermissionUtil;
import com.imchen.testhook.utils.PhoneInfoUtil;

import java.util.Map;

/**
 * Created by imchen on 2017/8/4.
 */

public class PhoneInfoService {

    public PhoneInfo info;

    public String getAllPhoneInfo(Activity activity) {
        info = new PhoneInfo();
        Battery battery = PhoneInfoUtil.getBatteryInfo2();
        Bluetooth bluetooth = PhoneInfoUtil.getBluetoothInfo();
        Build build = PhoneInfoUtil.getBuildInfo();
        final Location[] location = {null};
        Screen screen = PhoneInfoUtil.getScreenInfo(activity);
        Wifi wifi = PhoneInfoUtil.getWifiInfo();
        final Telephony[] telephony = {null};

        if (!PermissionUtil.checkPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            PermissionUtil.requestPermission(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, PermissionUtil.ACCESS_COARSE_LOCATION, new PermissionUtil.IRequestPermissionListener() {
                @Override
                public void success(String permissionName) {
                    location[0] =PhoneInfoUtil.getLocation();
                }

                @Override
                public void fail(String permissionName) {

                }
            });
        }else{
            location[0] =PhoneInfoUtil.getLocation();
        }
        if (!PermissionUtil.checkPermission(activity,Manifest.permission.READ_PHONE_STATE)){
            PermissionUtil.requestPermission(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, PermissionUtil.ACCESS_COARSE_LOCATION, new PermissionUtil.IRequestPermissionListener() {
                @Override
                public void success(String permissionName) {
                   telephony[0] = PhoneInfoUtil.getTelephonyInfo();
                }

                @Override
                public void fail(String permissionName) {

                }
            });
        }else{
          telephony[0] =  PhoneInfoUtil.getTelephonyInfo();
        }

        info.setBattery(battery);
        info.setBluetooth(bluetooth);
        info.setBuild(build);
        info.setLocation(location[0]);
        info.setWifi(wifi);
        info.setTelephony(telephony[0]);

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject phoneInfoObj = new JsonObject();

        String batteryJson = gson.toJson(battery);
        JsonObject batteryObj = parser.parse(batteryJson).getAsJsonObject();
        deepMerge(batteryObj, phoneInfoObj);

        String buildJson = gson.toJson(build);
        JsonObject buildObj = parser.parse(buildJson).getAsJsonObject();
        deepMerge(buildObj, phoneInfoObj);

        String bluetoothJson = gson.toJson(bluetooth);
        JsonObject bluetoothObj = parser.parse(bluetoothJson).getAsJsonObject();
        deepMerge(bluetoothObj, phoneInfoObj);

        if (location[0]!=null){
            String locationJson = gson.toJson(location[0]);
            JsonObject locationObj = parser.parse(locationJson).getAsJsonObject();
            deepMerge(locationObj, phoneInfoObj);
        }

        String screenJson = gson.toJson(screen);
        JsonObject screenObj = parser.parse(screenJson).getAsJsonObject();
        deepMerge(screenObj, phoneInfoObj);

        String wifiJson = gson.toJson(wifi);
        JsonObject wifiObj = parser.parse(wifiJson).getAsJsonObject();
        deepMerge(wifiObj, phoneInfoObj);

        if (telephony[0]!=null){
            String telephonyJson = gson.toJson(telephony[0]);
            JsonObject telephonyObj = parser.parse(telephonyJson).getAsJsonObject();
            deepMerge(telephonyObj, phoneInfoObj);
        }


//        String result=gson.toJson(info);
        return phoneInfoObj.toString();
    }

    /**
     * Source 的值将覆盖 Target 的值
     *
     * @param source
     * @param target
     * @return
     * @throws JsonIOException
     */
    public static JsonObject deepMerge(JsonObject source, JsonObject target) throws JsonIOException {
        for (Map.Entry<String, JsonElement> entry : source.entrySet()) {
            String key = entry.getKey();
            JsonElement value = source.get(key);
            if (!target.has(key)) {
                // new value for "key":
                target.add(key, value);
            } else {
                // existing value for "key" - recursively deep merge:
                if (value instanceof JsonObject) {
                    deepMerge(value.getAsJsonObject(), target.get(key).getAsJsonObject());
                } else {
                    target.add(key, value);
                }
            }
        }
        return target;
    }
}
