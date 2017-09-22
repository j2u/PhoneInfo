package com.imchen.myconfig;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;

public class MainActivity extends AppCompatActivity {

    private Button mConfigBtn;
    private EditText mConfigEt;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x123:
                    String config= (String) msg.obj;
                    mConfigEt.setText(config);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    public void init(){
        mConfigBtn= (Button) findViewById(R.id.config_btn);
        mConfigEt= (EditText)findViewById(R.id.config_et);
        mConfigBtn.setOnClickListener(new MyOnClickListener());
    }

    public String  readConfig(String filePath){
        File file=new File(filePath);
        try {
            FileInputStream fis=new FileInputStream(file);
            byte[] buffer=new byte[1024];
            fis.read(buffer);
            return new String(buffer,"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String config=readConfig("data/local/rom/server.json");
            Message msg=new Message();
            msg.what=0x123;
            msg.obj=config;
            mHandler.sendMessage(msg);
        }
    }
}
