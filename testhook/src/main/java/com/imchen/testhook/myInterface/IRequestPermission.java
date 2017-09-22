package com.imchen.testhook.myInterface;

/**
 * Created by imchen on 2017/9/21.
 */

public interface IRequestPermission {
    void success(String permissionName);
    void fail(String permissionName);
}
