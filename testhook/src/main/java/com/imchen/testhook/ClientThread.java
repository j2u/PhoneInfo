package com.imchen.testhook;

import android.util.Log;

import com.imchen.testhook.utils.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;

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
    public LinkedList<String> msgList=new LinkedList<>();
    private final static int MSGBOX_SIZE=10;

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
                    while (checkIsAlive(client)) {
                        if ((line = reader.readLine()) != null){
                            addMsg(line);
                            LogUtil.log(address + " say:*********************" + line + "*************************");
                        }else {
                            LogUtil.log(address + " say:*********************" + null + "*************************");
                        }

                    }
                    Log.d(TAG, "ClientThread reader line is null");
                } else {
                    Log.d(TAG, "run: reader null");

                    client.close();
//                        Thread.sleep(1000);
                    isOffLine = true;
                    if (reader!=null){
                        reader.close();
                        reader=null;
                    }
                    return;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private LinkedList<String> addMsg(String msg){
        if (msgList.size()==MSGBOX_SIZE){
            Log.d(TAG, "addMsg: clear msglist"+msgList.size());
            msgList.clear();
        }
        msgList.add(msg);
        return msgList;
    }

    public boolean checkIsAlive(Socket client){
        try {
            client.sendUrgentData(0);
            Log.d(TAG, "checkIsAlive: on server!");
            return true ;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}