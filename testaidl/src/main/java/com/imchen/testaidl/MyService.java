package com.imchen.testaidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class MyService extends Service {
    private static final String TAG="imchen";

    public MyService() {
    }

    private final IMyAidlInterface.Stub mbinder=new IMyAidlInterface.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
            Log.d(TAG, "basicTypes: "+anInt);
        }

        @Override
        public void test() throws RemoteException {
            Log.d(TAG, "test: ");
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: "+intent.toString());
        return mbinder;
    }
}
