package com.imchen.testhook.Entity;

/**
 * Created by imchen on 2017/8/4.
 */

public class Version {

    private String CODENAME;
    private String INCREAMENTAL;
    private String RELEASE;
    private String SDK;
    private Integer SDK_INT;
    private Codes codes;

    public String getCODENAME() {
        return CODENAME;
    }

    public void setCODENAME(String CODENAME) {
        this.CODENAME = CODENAME;
    }

    public String getINCREAMENTAL() {
        return INCREAMENTAL;
    }

    public void setINCREAMENTAL(String INCREAMENTAL) {
        this.INCREAMENTAL = INCREAMENTAL;
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

    public Codes getCodes() {
        return codes;
    }

    public void setCodes(Codes codes) {
        this.codes = codes;
    }
}
