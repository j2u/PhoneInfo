package com.imchen.scriptcontroller.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.imchen.scriptcontroller.R;

/**
 * Created by imchen on 2017/9/12.
 */

public class ViewUtils {
    public static void hintDialog(String title, String message, String btnName,Context activityContext) {
        hintDialog(title, message, btnName, null, null, null,activityContext);
    }

    public static void hintDialog(String title, String message, String confirmButtonName, String cancelButtonName,
                           DialogInterface.OnClickListener mConfListener, DialogInterface.OnClickListener mCancListener, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setIcon(R.mipmap.ic_launcher);
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

    public static void progressDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("downloading.....");
    }

}
