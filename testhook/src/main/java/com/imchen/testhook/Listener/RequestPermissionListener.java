package com.imchen.testhook.Listener;

/**
 * Created by imchen on 2017/9/21.
 */

public class RequestPermissionListener {

    interface IRequestPermission {
        void success(String permissionName);
        void fail(String permissionName);
    }

}
