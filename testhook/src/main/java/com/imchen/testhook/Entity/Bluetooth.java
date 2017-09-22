package com.imchen.testhook.Entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by imchen on 2017/8/4.
 */

public class Bluetooth {
    @SerializedName("Bluetooth_Address")
    private String Address;

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    @Override
    public String toString() {
        return "Bluetooth{" +
                "\n Address='" + Address + '\'' +
                "\n}";
    }
}
