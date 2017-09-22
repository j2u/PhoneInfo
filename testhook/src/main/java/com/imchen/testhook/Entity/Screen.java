package com.imchen.testhook.Entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by imchen on 2017/8/4.
 */

public class Screen {

    @SerializedName("Screen.WidthPixels")
    private Integer WidthPixels;
    @SerializedName("Screen.HeightPixels")
    private Integer HeightPixels;
    @SerializedName("Screen.DensityDpi")
    private float DensityDpi;
    @SerializedName("Screen.Xdpi")
    private float Xdpi;
    @SerializedName("Screen.Ydpi")
    private float Ydpi;

    public Integer getWidthPixels() {
        return WidthPixels;
    }

    public void setWidthPixels(Integer widthPixels) {
        WidthPixels = widthPixels;
    }

    public Integer getHeightPixels() {
        return HeightPixels;
    }

    public void setHeightPixels(Integer heightPixels) {
        HeightPixels = heightPixels;
    }

    public float getDensityDpi() {
        return DensityDpi;
    }

    public void setDensityDpi(float densityDpi) {
        DensityDpi = densityDpi;
    }

    public float getXdpi() {
        return Xdpi;
    }

    public void setXdpi(float xdpi) {
        Xdpi = xdpi;
    }

    public float getYdpi() {
        return Ydpi;
    }

    public void setYdpi(float ydpi) {
        Ydpi = ydpi;
    }

    @Override
    public String toString() {
        return "Screen{" +
                "\n WidthPixels=" + WidthPixels +
                "\n HeightPixels=" + HeightPixels +
                "\n DensityDpi=" + DensityDpi +
                "\n Xdpi=" + Xdpi +
                "\n Ydpi=" + Ydpi +
                "\n}";
    }
}
