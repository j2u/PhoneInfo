package com.imchen.scriptcontroller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.imchen.scriptcontroller.utils.ScriptUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mStartScript;
    private Button mStopScript;
    private EditText mPackageNameEt;

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
                ScriptUtil.startScript(packageName);
                }else {
                    Toast.makeText(getApplicationContext(),"please input package name!!!",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.stop_script_btn:
                break;
            default:
                break;
        }
    }

}
