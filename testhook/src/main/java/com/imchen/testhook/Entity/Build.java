package com.imchen.testhook.Entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by imchen on 2017/8/4.
 */

public class Build {

    @SerializedName("Build.BOARD")
    private String BOARD;
    @SerializedName("Build.BOOTLOADER")
    private String BOOTLOADER;
    @SerializedName("Build.CPU_ABI")
    private String CPU_ABI;
    @SerializedName("Build.CPU_ABI2")
    private String CPU_ABI2;
    @SerializedName("Build.BRAND")
    private String BRAND;
    @SerializedName("Build.DEVICE")
    private String DEVICE;
    @SerializedName("Build.DISPLAY")
    private String DISPLAY;
    @SerializedName("Build.FINGERPRINT")
    private String FINGERPRINT;
    @SerializedName("Build.HARDWARE")
    private String HARDWARE;
    @SerializedName("Build.HOST")
    private String HOST;
    @SerializedName("Build.ID")
    private String ID;
    @SerializedName("Build.IS_DEBUGGABLE")
    private String IS_DEBUGGABLE;
    @SerializedName("Build.MANUFACTURER")
    private String MANUFACTURER;
    @SerializedName("Build.MODEL")
    private String MODEL;
    @SerializedName("Build.PRODUCT")
    private String PRODUCT;
    @SerializedName("Build.RADIO")
    private String RADIO;
    @SerializedName("Build.SERIAL")
    private String SERIAL;
    @SerializedName("Build.TAGS")
    private String TAGS;
    @SerializedName("Build.TIME")
    private Long TIME;
    @SerializedName("Build.USER")
    private String USER;
//    @SerializedName("Build.VERSION")
//    private Version VERSION;
    @SerializedName("Build.VERSION.CODENAME")
    private String CODENAME;
    @SerializedName("Build.VERSION.INCREMENTAL")
    private String INCREMENTAL;
    @SerializedName("Build.VERSION.RELEASE")
    private String RELEASE;
    @SerializedName("Build.VERSION.SDK")
    private String SDK;
    @SerializedName("Build.VERSION.SDK_INT")
    private Integer SDK_INT;
    @SerializedName("Build.VERSION.CODES")
    private Integer CODES;

    public String getBOARD() {
        return BOARD;
    }

    public void setBOARD(String BOARD) {
        this.BOARD = BOARD;
    }

    public String getBOOTLOADER() {
        return BOOTLOADER;
    }

    public void setBOOTLOADER(String BOOTLOADER) {
        this.BOOTLOADER = BOOTLOADER;
    }

    public String getCPU_ABI() {
        return CPU_ABI;
    }

    public void setCPU_ABI(String CPU_ABI) {
        this.CPU_ABI = CPU_ABI;
    }

    public String getCPU_ABI2() {
        return CPU_ABI2;
    }

    public void setCPU_ABI2(String CPU_ABI2) {
        this.CPU_ABI2 = CPU_ABI2;
    }

    public String getBRAND() {
        return BRAND;
    }

    public void setBRAND(String BRAND) {
        this.BRAND = BRAND;
    }

    public String getDEVICE() {
        return DEVICE;
    }

    public void setDEVICE(String DEVICE) {
        this.DEVICE = DEVICE;
    }

    public String getDISPLAY() {
        return DISPLAY;
    }

    public void setDISPLAY(String DISPLAY) {
        this.DISPLAY = DISPLAY;
    }

    public String getFINGERPRINT() {
        return FINGERPRINT;
    }

    public void setFINGERPRINT(String FINGERPRINT) {
        this.FINGERPRINT = FINGERPRINT;
    }

    public String getHARDWARE() {
        return HARDWARE;
    }

    public void setHARDWARE(String HARDWARE) {
        this.HARDWARE = HARDWARE;
    }

    public String getHOST() {
        return HOST;
    }

    public void setHOST(String HOST) {
        this.HOST = HOST;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getIS_DEBUGGABLE() {
        return IS_DEBUGGABLE;
    }

    public void setIS_DEBUGGABLE(String IS_DEBUGGABLE) {
        this.IS_DEBUGGABLE = IS_DEBUGGABLE;
    }

    public String getMANUFACTURER() {
        return MANUFACTURER;
    }

    public void setMANUFACTURER(String MANUFACTURER) {
        this.MANUFACTURER = MANUFACTURER;
    }

    public String getMODEL() {
        return MODEL;
    }

    public void setMODEL(String MODEL) {
        this.MODEL = MODEL;
    }

    public String getPRODUCT() {
        return PRODUCT;
    }

    public void setPRODUCT(String PRODUCT) {
        this.PRODUCT = PRODUCT;
    }

    public String getRADIO() {
        return RADIO;
    }

    public void setRADIO(String RADIO) {
        this.RADIO = RADIO;
    }

    public String getSERIAL() {
        return SERIAL;
    }

    public void setSERIAL(String SERIAL) {
        this.SERIAL = SERIAL;
    }

    public String getTAGS() {
        return TAGS;
    }

    public void setTAGS(String TAGS) {
        this.TAGS = TAGS;
    }

    public Long getTIME() {
        return TIME;
    }

    public void setTIME(Long TIME) {
        this.TIME = TIME;
    }

    public String getUSER() {
        return USER;
    }

    public void setUSER(String USER) {
        this.USER = USER;
    }

    public String getCODENAME() {
        return CODENAME;
    }

    public void setCODENAME(String CODENAME) {
        this.CODENAME = CODENAME;
    }

    public String getINCREMENTAL() {
        return INCREMENTAL;
    }

    public void setINCREMENTAL(String INCREMENTAL) {
        this.INCREMENTAL = INCREMENTAL;
    }

    public String getRELEASE() {
        return RELEASE;
    }

    public void setRELEASE(String RELEASE) {
        this.RELEASE = RELEASE;
    }

    public String getSDK() {
        return SDK;
    }

    public void setSDK(String SDK) {
        this.SDK = SDK;
    }

    public Integer getSDK_INT() {
        return SDK_INT;
    }

    public void setSDK_INT(Integer SDK_INT) {
        this.SDK_INT = SDK_INT;
    }

    public Integer getCODES() {
        return CODES;
    }

    public void setCODES(Integer CODES) {
        this.CODES = CODES;
    }

    @Override
    public String toString() {
        return "Build{" +
                "\n BOARD='" + BOARD + '\'' +
                "\n BOOTLOADER='" + BOOTLOADER + '\'' +
                "\n CPU_ABI='" + CPU_ABI + '\'' +
                "\n CPU_ABI2='" + CPU_ABI2 + '\'' +
                "\n BRAND='" + BRAND + '\'' +
                "\n DEVICE='" + DEVICE + '\'' +
                "\n DISPLAY='" + DISPLAY + '\'' +
                "\n FINGERPRINT='" + FINGERPRINT + '\'' +
                "\n HARDWARE='" + HARDWARE + '\'' +
                "\n HOST='" + HOST + '\'' +
                "\n ID='" + ID + '\'' +
                "\n IS_DEBUGGABLE='" + IS_DEBUGGABLE + '\'' +
                "\n MANUFACTURER='" + MANUFACTURER + '\'' +
                "\n MODEL='" + MODEL + '\'' +
                "\n PRODUCT='" + PRODUCT + '\'' +
                "\n RADIO='" + RADIO + '\'' +
                "\n SERIAL='" + SERIAL + '\'' +
                "\n TAGS='" + TAGS + '\'' +
                "\n TIME=" + TIME +
                "\n USER='" + USER + '\'' +
                "\n CODENAME='" + CODENAME + '\'' +
                "\n INCREMENTAL='" + INCREMENTAL + '\'' +
                "\n RELEASE='" + RELEASE + '\'' +
                "\n SDK='" + SDK + '\'' +
                "\n SDK_INT=" + SDK_INT +
                "\n CODES=" + CODES +
                "\n}";
    }
}
