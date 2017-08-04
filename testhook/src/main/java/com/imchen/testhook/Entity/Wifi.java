package com.imchen.testhook.Entity;

/**
 * Created by imchen on 2017/8/4.
 */

public class Wifi {

    private String BSSID;
    private String MacAddress;
    private String ip;

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public String getMacAddress() {
        return MacAddress;
    }

    public void setMacAddress(String macAddress) {
        MacAddress = macAddress;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
