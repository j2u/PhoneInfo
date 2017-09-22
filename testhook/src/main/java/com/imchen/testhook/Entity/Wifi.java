package com.imchen.testhook.Entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by imchen on 2017/8/4.
 */

public class Wifi {

    @SerializedName("Wifi.BSSID")
    private String BSSID;
    @SerializedName("Wifi.MacAddress")
    private String MacAddress;
    @SerializedName("Wifi.ip")
    private String ip;
    @SerializedName("Wifi.outNetIp")
    private String outNetIp;

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

    public String getOutNetIp() {
        return outNetIp;
    }

    public void setOutNetIp(String outNetIp) {
        this.outNetIp = outNetIp;
    }

    @Override
    public String toString() {
        return "Wifi{" +
                "\n BSSID='" + BSSID + '\'' +
                "\n MacAddress='" + MacAddress + '\'' +
                "\n ip='" + ip + '\'' +
                "\n outNetIp='" + outNetIp + '\'' +
                "\n}";
    }
}
