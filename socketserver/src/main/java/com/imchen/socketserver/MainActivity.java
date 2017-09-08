package com.imchen.socketserver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.imchen.socketserver.service.SocketService;
import com.imchen.socketserver.utils.LogUtil;

public class MainActivity extends AppCompatActivity {

    private MyServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startSocket();
    }


    public void startSocket(){
        serviceConnection=new MyServiceConnection();
        Intent intent =new Intent(MainActivity.this,SocketService.class);
        startService(intent);
    }


    public class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.log("connected:"+name);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.log("disconnected:"+name);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}
