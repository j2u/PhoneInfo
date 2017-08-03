package com.imchen.ipcclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.imchen.testaidl.IMyAidlInterface;

public class MainActivity extends AppCompatActivity {

    private static final String TAG="imchen";
    IMyAidlInterface iMyAidl=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectRemoteServcer();
    }

    public void connectRemoteServcer(){
        Intent intent=new Intent();
        intent.setAction("com.imchen.IMyAidlInterface.aidl");
        intent.setPackage("com.imchen.testaidl");
        bindService(intent,mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mServiceConnection =new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: "+name);
            iMyAidl=IMyAidlInterface.Stub.asInterface(service);
            if (iMyAidl!=null){
                try {
                    iMyAidl.test();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: "+name);
        }
    };
}
