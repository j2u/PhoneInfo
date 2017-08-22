package com.imchen.testhook.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.imchen.testhook.utils.FloatViewUtil;

/**
 * Created by imchen on 2017/8/22.
 */

public class FloatViewService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
