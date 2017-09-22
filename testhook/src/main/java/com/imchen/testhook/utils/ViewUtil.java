package com.imchen.testhook.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.imchen.testhook.InstallActivity;
import com.imchen.testhook.R;

/**
 * Created by imchen on 2017/9/22.
 */

public class ViewUtil {

    public static void hintDialog(Context context,String title, String message, String btnName) {
        hintDialog(context,title, message, btnName, null, null, null);
    }

    public static void hintDialog(Context context,String title, String message, String confirmButtonName, String cancelButtonName,
                           DialogInterface.OnClickListener mConfListener, DialogInterface.OnClickListener mCancListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setMessage(message);
        if (mConfListener != null) {
            builder.setNegativeButton(confirmButtonName == null ? "Cancel" : cancelButtonName, mCancListener);
        }
        if (mCancListener != null) {
            builder.setPositiveButton(confirmButtonName == null ? "Confirm" : confirmButtonName, mConfListener);
        }
        if (mCancListener == null && confirmButtonName != null) {
            builder.setNegativeButton(confirmButtonName, null);
        }
        builder.create();
        builder.show();
    }
}
