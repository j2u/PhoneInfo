package com.imchen.testhook.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.imchen.testhook.utils.LogUtil;

/**
 * Created by imchen on 2017/8/3.
 */

public class BatteryRec extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int batteryLevel=intent.getIntExtra("level",-1);
        int batteryScale=intent.getIntExtra("scale",-1);
        int status = intent.getIntExtra("status", -1);
        int health = intent.getIntExtra("health", -1);
        LogUtil.log("level: "+batteryLevel);
        LogUtil.log("scale: "+batteryScale);
        LogUtil.log("status: "+status);
        LogUtil.log("health: "+health);
        Toast.makeText(context,"level: "+batteryLevel,Toast.LENGTH_SHORT).show();
    }
}
