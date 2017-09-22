package com.imchen.testhook.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;

import com.imchen.testhook.MainActivity;

/**
 * Created by imchen on 2017/9/21.
 */

public class PermissionUtil {

    public final static int READ_EXTERNAL_STORAGE = 1;
    public final static int WRITE_EXTERNAL_STORAGE = 2;
    public final static int READ_WRITE_EXTERNAL_STORAGE = 3;
    public final static int READ_PHONE_STATE = 4;
    public final static int ACCESS_COARSE_LOCATION = 5;

    public interface IRequestPermissionListener {
        void success(String permissionName);

        void fail(String permissionName);
    }

    public boolean requestPermission(Activity activity, String[] permissiones) {
        ActivityCompat.requestPermissions(activity, permissiones, 1);
        return false;
    }

    public static void requestPermission(Activity activity, String[] permissions, int requestCode, IRequestPermissionListener listener) {
            MainActivity.mPermissionListener = listener;
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    public static boolean checkPermission(Context context, String permission) {
        int result = ActivityCompat.checkSelfPermission(context, permission);
        if (result == 0) {
            return true;
        } else {
            return false;
        }
    }

}
