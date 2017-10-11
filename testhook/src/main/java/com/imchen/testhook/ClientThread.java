package com.imchen.testhook;

import android.util.Log;

import com.imchen.testhook.utils.LogUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by imchen on 2017/9/29.
 */


public class ClientThread extends Thread {

    private final static String TAG = "ClientThread";
    public boolean isOffLine = false;
    public Socket client;
    public BufferedReader reader;
    public String line;
    private String address;

    public ClientThread(Socket client) {
        this.client = client;
        address = String.valueOf(client.getInetAddress().toString().substring(1));
        isOffLine = false;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (reader == null) {
                    reader = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
                    Log.d(TAG, "ClientThread reader line is "+reader);
                    while ((line = reader.readLine()) != null&&line.length()!=0) {
                        LogUtil.log(address + " say:*********************" + line + "*************************");

                    }
                    Log.d(TAG, "ClientThread reader line is null");
                } else {
                    Log.d(TAG, "run: reader null");

                    client.close();
//                        Thread.sleep(1000);
                    isOffLine = true;
                    return;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}