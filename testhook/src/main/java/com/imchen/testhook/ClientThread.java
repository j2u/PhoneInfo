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

    private final static String TAG="ClientThread";
    public Socket client;
    private BufferedReader reader;
    private String address;

    public ClientThread(Socket client) {
        this.client = client;
        address = String.valueOf(client.getRemoteSocketAddress());
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (reader == null) {

                    reader = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        LogUtil.log(address + " say:*********************" + line + "*************************");
                    }

                }
                if (!client.isConnected()) {
                    return;
                }
                Log.d(TAG, "run: reader null");

                Thread.sleep(1000);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}