package com.imchen.scriptcontroller;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mStartScript;
    private Button mStopScript;

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
    }

    public void initListener(){
        mStartScript.setOnClickListener(this);
        mStopScript.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_script_btn:
                break;
            case R.id.stop_script_btn:
                break;
            default:
                break;
        }
    }
}
