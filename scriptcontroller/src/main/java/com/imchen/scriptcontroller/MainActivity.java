package com.imchen.scriptcontroller;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.imchen.scriptcontroller.utils.ScriptUtil;

import java.io.File;
import java.net.HttpURLConnection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mStartScript;
    private Button mStopScript;
    private EditText mPackageNameEt;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("imchen", "onKeyDown: "+event.getAction());
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();


    }

    public void initView(){
        mStartScript= (Button) findViewById(R.id.start_script_btn);
        mStopScript= (Button) findViewById(R.id.stop_script_btn);
        mPackageNameEt= (EditText) findViewById(R.id.et_packageName);
    }

    public void initListener(){
        mStartScript.setOnClickListener(this);
        mStopScript.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_script_btn:
                String packageName=mPackageNameEt.getText().toString();
                if (packageName!=null&&!"".equals(packageName)){
                    ScriptUtil.initDir(packageName);
                ScriptUtil.startScript(packageName);
                }else {
                    Toast.makeText(getApplicationContext(),"please input package name!!!",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.stop_script_btn:
                String filePath= Environment.getExternalStorageDirectory()+"/testtttt";
                File dir=new File(filePath);
                if (!dir.exists()){
                    dir.mkdir();
                }
                ScriptUtil.download("http://zhima-proxy.zhimadaili.com/android/qudao/zhimadaili_android_v1.2.8_35_official.apk",filePath+"/zhimadaili_android_v1.2.8_35_official.apk",new MyDownloadListener());
                break;
            default:
                break;
        }
    }

    public class MyDownloadListener extends ScriptUtil.DownloadListener{
        @Override
        public void onPreDownload(HttpURLConnection connection) {
            super.onPreDownload(connection);
        }

        @Override
        public void onStart(long startPosition) {
            super.onStart(startPosition);
        }

        @Override
        public void onProgress(long currentPosition) {
            super.onProgress(currentPosition);
        }

        @Override
        public void onChildComplete(long finishPosition) {
            super.onChildComplete(finishPosition);
        }

        @Override
        public void onChildResume(long resumePosition) {
            super.onChildResume(resumePosition);
        }

        @Override
        public void onResume(long resumePosition) {
            super.onResume(resumePosition);
        }

        @Override
        public void onComplete() {
            super.onComplete();
        }

        @Override
        public void onCancel() {
            super.onCancel();
        }

        @Override
        public void onFail() {
            super.onFail();
        }

        @Override
        public void onStop(long stopPosition) {
            super.onStop(stopPosition);
        }

    }

}