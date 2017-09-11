package com.imchen.testhook.service;

import com.google.gson.Gson;
import com.imchen.testhook.Entity.PhoneInfo;
import com.imchen.testhook.utils.PhoneInfoUtil;

/**
 * Created by imchen on 2017/8/4.
 */

public class PhoneInfoService {

    public PhoneInfo info;

    public String getAllPhoneInfo(){
        info=new PhoneInfo();
        info.setBattery(PhoneInfoUtil.getBatteryInfo2());
        info.setBluetooth(PhoneInfoUtil.getBluetoothInfo());
        info.setBuild(PhoneInfoUtil.getBuildInfo());
        info.setLocation(PhoneInfoUtil.getLocation());
        info.setTelephony(PhoneInfoUtil.getTelephoneInfo());
        info.setWifi(PhoneInfoUtil.getWifiInfo());
        Gson gson=new Gson();
        String result=gson.toJson(info);
        return result;
    }
}
