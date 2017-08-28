package com.imchen.testhook;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.imchen.testhook.utils.LogUtil;
import com.myrom.aidl.IScriptInterface;

public class StartscriptActivity extends AppCompatActivity {

    private EditText scriptPathEt;
    private Button startScriptBt;
    private IScriptInterface scriptInterface;
    private boolean isConnected = false;
    private boolean isScriptStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startscript);
        initView();
        if (!isConnected) {
            connectService();
        }
        startScriptBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(scriptPathEt.getText())) {
                    Toast.makeText(getApplicationContext(), "please input content!!", Toast.LENGTH_SHORT).show();
                } else {
                    String scriptPath = "/data/data/com.imchen.testhook/"+scriptPathEt.getText().toString();
                    LogUtil.log("scriptPath:"+scriptPath);
                    try {
                        if (!isScriptStart&&scriptInterface!=null) {
                            scriptInterface.startScript(scriptPath, new String[]{"cn.kuwo.player"});
                        }else if(scriptInterface==null){
                            Toast.makeText(getApplicationContext(),"bind error!",Toast.LENGTH_SHORT).show();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    isScriptStart = true;
                }
            }
        });
    }

    private void initView() {
        scriptPathEt = (EditText) findViewById(R.id.et_scriptpath);
        startScriptBt = (Button) findViewById(R.id.bt_start);
    }

    private void connectService() {
        Intent intent = new Intent();
        intent.setAction("com.myrom.startscript");
        intent.setPackage("com.myrom");
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.log("onServiceConnected: name:"+name);
            isConnected = true;
            scriptInterface = IScriptInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.log(name + " have been disconnected");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
