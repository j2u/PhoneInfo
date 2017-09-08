package com.imchen.testhook;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.imchen.testhook.utils.LogUtil;
import com.imchen.testhook.utils.MyScriptUtil;
import com.myrom.aidl.IScriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.List;

@SuppressWarnings("WrongConstant")
public class StartscriptActivity extends AppCompatActivity {

    private EditText scriptPathEt;
    private Button startScriptBt;
    private IScriptInterface scriptInterface;
    private boolean isConnected = false;
    private boolean isScriptStart = false;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startscript);
        mContext=getApplicationContext();
        initView();
        getAllService();
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
                        JSONObject json=new JSONObject();
                        try {
                            json.put("packageName","com.test.hook");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        scriptInterface.startScript(json.toString());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
//                    try {
//                        if (!isScriptStart&&scriptInterface!=null) {
//                            scriptInterface.startScript(scriptPath, new String[]{"cn.kuwo.player"});
//                        }else if(scriptInterface==null){
//                            Toast.makeText(getApplicationContext(),"bind error!",Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
                    try {
//                        Context context=createPackageContext("cn.kuwo.player",CONTEXT_IGNORE_SECURITY|CONTEXT_INCLUDE_CODE);
//                        Class cls=context.getClassLoader().loadClass("com.myrom.app");
//                        Method method=cls.getDeclaredMethod("main",new Class[]{Context.class});
//                        method.invoke(cls,mContext);
                    } catch (Exception e) {
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
//        Intent intent = new Intent();
//        intent.setAction("com.myrom.myservice");
//        intent.setPackage("android");
//        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        IBinder service= MyScriptUtil.getScriptBinder("scriptService");
        scriptInterface=IScriptInterface.Stub.asInterface(service);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.log("onServiceConnected: name:"+name);
            Toast.makeText(getApplicationContext(),"bind "+name+" success!",Toast.LENGTH_SHORT ).show();
            isConnected = true;
            scriptInterface = IScriptInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.log(name + " have been disconnected");
        }
    };

    public void getAllService(){
        ActivityManager am= (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices=am.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo info: runningServices
             ) {
            LogUtil.log("service class: "+info.service.getClassName());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
