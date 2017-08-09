package com.imchen.testhook.Entity;

/**
 * Created by imchen on 2017/8/4.
 */

public class Build {

    private String BOARD;
    private String BOOTLOADER;
    private String CPU_ABI;
    private String CPU_ABI2;
    private String BRAND;
    private String DEVICE;
    private String DISPLAY;
    private String FINGERPRINT;
    private String HARDWARE;
    private String HOST;
    private String ID;
    private String IS_DEBUGGABLE;
    private String MANUFACTURRER;
    private String MODEL;
    private String PRODUCT;
    private String RADIO;
    private String SERIAL;
    private String TAGS;
    private Long TIME;
    private String USER;
    private Version VERSION;

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

    public String getMANUFACTURRER() {
        return MANUFACTURRER;
    }

    public void setMANUFACTURRER(String MANUFACTURRER) {
        this.MANUFACTURRER = MANUFACTURRER;
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

    public Version getVERSION() {
        return VERSION;
    }

    public void setVERSION(Version VERSION) {
        this.VERSION = VERSION;
    }

    @Override
    public String toString() {
        return "BOARD='" + BOARD + "'\n" +
                "BOOTLOADER='" + BOOTLOADER + "'\n" +
                "CPU_ABI='" + CPU_ABI + "'\n" +
                "CPU_ABI2='" + CPU_ABI2 + "'\n" +
                "BRAND='" + BRAND + "'\n" +
                "DEVICE='" + DEVICE + "'\n" +
                "DISPLAY='" + DISPLAY + "'\n" +
                "FINGERPRINT='" + FINGERPRINT + "'\n" +
                "HARDWARE='" + HARDWARE + "'\n" +
                "HOST='" + HOST + "'\n" +
                "ID='" + ID + "'\n" +
                "IS_DEBUGGABLE='" + IS_DEBUGGABLE + "'\n" +
                "MANUFACTURRER='" + MANUFACTURRER + "'\n" +
                "MODEL='" + MODEL + "'\n" +
                "PRODUCT='" + PRODUCT + "'\n" +
                "RADIO='" + RADIO + "'\n" +
                "SERIAL='" + SERIAL + "'\n" +
                "TAGS='" + TAGS + "'\n" +
                "TIME=" + TIME +
                "USER='" + USER + "'\n" ;
    }
}
