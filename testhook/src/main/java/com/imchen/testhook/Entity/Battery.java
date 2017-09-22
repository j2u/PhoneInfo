package com.imchen.testhook.Entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by imchen on 2017/8/4.
 */

public class Battery {

    @SerializedName("Battery.Level")
    private Integer Level;
    @SerializedName("Battery.Scale")
    private Integer Scale;

    public Integer getLevel() {
        return Level;
    }

    public void setLevel(Integer level) {
        Level = level;
    }

    public Integer getScale() {
        return Scale;
    }

    public void setScale(Integer scale) {
        Scale = scale;
    }

    @Override
    public String toString() {
        return "Battery{" +
                "\n Level=" + Level +
                "\n Scale=" + Scale +
                "\n}";
    }
}
