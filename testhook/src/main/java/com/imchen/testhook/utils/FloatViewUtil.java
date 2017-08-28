package com.imchen.testhook.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.imchen.testhook.R;

/**
 * Created by imchen on 2017/8/22.
 */

public class FloatViewUtil {

    private static WindowManager mWindowManager;
    public static LinearLayout mLinearLayout;

    public FloatViewUtil() {

    }

    public static boolean addFloatView(Context context) {
        getWindowManager(context);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.format = PixelFormat.RGBA_8888;
        LayoutInflater inflater = LayoutInflater.from(context);
        mLinearLayout = (LinearLayout) inflater.inflate(R.layout.floatview_content, null);
        mWindowManager.addView(mLinearLayout, layoutParams);
        return false;
    }

    public static boolean removeView(Context context, View view) {
        if (view != null) {
            getWindowManager(context);
            mWindowManager.removeView(view);
            view = null;
        }
        return true;
    }

    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }
}
