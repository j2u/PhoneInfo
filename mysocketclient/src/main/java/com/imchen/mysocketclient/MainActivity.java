package com.imchen.mysocketclient;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG="MainActivity";

    private EditText mContentEt;
    private EditText mHostEt;
    private EditText mPortEt;
    private Button mSendBtn;
    private static Button mConnectBtn;
    private static Button mDisconnectBtn;

    private final static String SERVER_ADDRESS = "127.0.0.1";
    private final static int SERVER_PORT = 3388;
    private static Socket client = null;
    private static boolean isConnect = false;
    private String host;
    private int port;

    private static Context mContext;

    private static BufferedWriter writer;

    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x1cc:
                    if (isConnect) {
                        Toast.makeText(mContext, "Connect success!", Toast.LENGTH_SHORT).show();
                        mConnectBtn.setEnabled(false);
                    } else {
                        Toast.makeText(mContext, "Connect fail!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 0x1c1:
                    if (!isConnect){
                        mDisconnectBtn.setEnabled(false);
                        mConnectBtn.setEnabled(true);
                    }
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
        initView();
        mContext = getApplicationContext();
    }

    public void initView() {
        mContentEt = (EditText) findViewById(R.id.et_content);
        mHostEt = (EditText) findViewById(R.id.et_host);
        mPortEt = (EditText) findViewById(R.id.et_port);
        mSendBtn = (Button) findViewById(R.id.btn_send_message);
        mConnectBtn = (Button) findViewById(R.id.btn_connect);
        mDisconnectBtn = (Button) findViewById(R.id.btn_disconnect);

        mSendBtn.setOnClickListener(this);
        mConnectBtn.setOnClickListener(this);
        mDisconnectBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connect:

                host = mHostEt.getText().toString();
                port = Integer.parseInt(mPortEt.getText().toString());
                initSocketServer(host, port);

                break;
            case R.id.btn_send_message:
                new Thread() {
                    @Override
                    public void run() {
                        String msg = mContentEt.getText().toString() + "\n";
                        int i = 0;
                        while (true) {
                            sendMessage("testafsadf" + i + "\n");
                            try {
                                i++;
                                this.sleep(4000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
                break;
            case R.id.btn_disconnect:
                try {
                    Log.d(TAG, "onClick: "+client.isConnected());
                    if (client.isConnected()) {
                        client.close();
                        isConnect=false;
//                        client.sendUrgentData(0xff);
                    }
                    Message msg=new Message();
                    msg.what=0x1c1;
                    mHandler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public static void initSocketServer(final String host, final int port) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        client = new Socket(host, port);
                        client.setSoTimeout(10);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    while (true){
                                     client.sendUrgentData(0xff);
                                        Log.d(TAG, "run: sending heartbeat");
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        if (client.isConnected()) {
                            isConnect = true;

                        } else {
                            Log.d("imchen", "run: connect is " + client.isConnected());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Message msg = new Message();
                    msg.what = 0x1cc;
                    mHandler.sendMessage(msg);
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String msg) {
        try {
            if (client == null) {
                initSocketServer(host, port);
            }
            Log.d("imchen", "onClick: " + writer);
            if (writer == null) {
                writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "UTF-8"));
            }
            writer.write(msg);
            writer.flush();
            Log.d("imchen", "onClick: " + writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (client != null) {
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
