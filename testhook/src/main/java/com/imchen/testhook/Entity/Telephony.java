package com.imchen.testhook.Entity;

import android.telephony.CellInfo;

import java.util.List;

/**
 * Created by imchen on 2017/8/4.
 */

public class Telephony {

    private String DeviceId;
    private String IsimDomain;
    private String IsimImpi;
    private String VoiceMailAlphaTag;
    private String Msisdn;
    private String Line1AlphaTag;
    private String Line1Number;
    private String IccSerialNumber;
    private String SubscriberId;
    private String GroupIdLevel1;
    private String DeviceSvn;
    private String VoiceMailNumber;
    private List<CellInfo> allCellInfo;

    public Telephony( ) {
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }

    public String getIsimDomain() {
        return IsimDomain;
    }

    public void setIsimDomain(String isimDomain) {
        IsimDomain = isimDomain;
    }

    public String getIsimImpi() {
        return IsimImpi;
    }

    public void setIsimImpi(String isimImpi) {
        IsimImpi = isimImpi;
    }

    public String getVoiceMailAlphaTag() {
        return VoiceMailAlphaTag;
    }

    public void setVoiceMailAlphaTag(String voiceMailAlphaTag) {
        VoiceMailAlphaTag = voiceMailAlphaTag;
    }

    public String getMsisdn() {
        return Msisdn;
    }

    public void setMsisdn(String msisdn) {
        Msisdn = msisdn;
    }

    public String getLine1AlphaTag() {
        return Line1AlphaTag;
    }

    public void setLine1AlphaTag(String line1AlphaTag) {
        Line1AlphaTag = line1AlphaTag;
    }

    public String getLine1Number() {
        return Line1Number;
    }

    public void setLine1Number(String line1Number) {
        Line1Number = line1Number;
    }

    public String getIccSerialNumber() {
        return IccSerialNumber;
    }

    public void setIccSerialNumber(String iccSerialNumber) {
        IccSerialNumber = iccSerialNumber;
    }

    public String getSubscriberId() {
        return SubscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        SubscriberId = subscriberId;
    }

    public String getGroupIdLevel1() {
        return GroupIdLevel1;
    }

    public void setGroupIdLevel1(String groupIdLevel1) {
        GroupIdLevel1 = groupIdLevel1;
    }

    public String getDeviceSvn() {
        return DeviceSvn;
    }

    public void setDeviceSvn(String deviceSvn) {
        DeviceSvn = deviceSvn;
    }

    public List<CellInfo> getAllCellInfo() {
        return allCellInfo;
    }

    public void setAllCellInfo(List<CellInfo> allCellInfo) {
        this.allCellInfo = allCellInfo;
    }

    public String getVoiceMailNumber() {
        return VoiceMailNumber;
    }

    public void setVoiceMailNumber(String voiceMailNumber) {
        VoiceMailNumber = voiceMailNumber;
    }
}
