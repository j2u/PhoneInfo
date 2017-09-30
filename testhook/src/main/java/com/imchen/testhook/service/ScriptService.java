package com.imchen.testhook.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.imchen.testhook.ClientThread;
import com.imchen.testhook.ConsoleActivity;
import com.imchen.testhook.Entity.Client;
import com.imchen.testhook.utils.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ScriptService extends Service {

    private final static String TAG = "ScriptService";
    private final static long CHECK_TIME=1000*5;

    private final static int PORT = 3388;

    public ScriptService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        new scriptSocket().start();
        new thCheckClientStatus(ConsoleActivity.clientArrayList).start();
        return new MyBinder();
    }

    public class scriptSocket extends Thread {

        private ServerSocket server;
        private Socket socket;
        private ArrayList<ClientThread> listClient = new ArrayList<>();
        private ArrayList<Client> listClientEntity = new ArrayList<>();

        @Override
        public void run() {
            try {
                server = new ServerSocket(PORT);
                int clientNum = 0;
                while (true) {
                    LogUtil.log("ServerSocket have been create! current client:" + clientNum);
                    socket = server.accept();
                    clientNum++;
                    LogUtil.log("current client num : " + clientNum);
                    ClientThread thClient = new ClientThread(socket);
                    thClient.start();
                    Client client = new Client();
                    client.setName(thClient.getName());
                    client.setAddress(socket.getRemoteSocketAddress().toString());
                    client.setStatus(1);
                    client.setClientThread(thClient);
                    listClient.add(thClient);
                    ConsoleActivity.clientArrayList.add(0, client);
                    listClientEntity.add(client);

                    Message msg = new Message();
                    msg.what = 0x1111;
                    msg.obj = listClientEntity;
                    ConsoleActivity.mHandler.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public class thCheckClientStatus extends Thread{

        private ArrayList<Client> clientList;

        public thCheckClientStatus(ArrayList<Client> clientList) {
            this.clientList=clientList;
        }

        @Override
        public void run() {
            while (true){
                for (int i=0;i<clientList.size();i++){
                    Log.d(TAG, "run: checking client isConnected!");
                    Client curClient=clientList.get(i);
                    Socket socket=curClient .getClientThread().client;
                    boolean isOnLine=socket.isConnected();
                    Log.d(TAG, "run: isOnLine:"+curClient.getName()+" "+curClient.getAddress()+" isOnline:"+isOnLine);
                    if (!isOnLine){
                        curClient.setStatus(-1);
                        Message msg=new Message();
                        msg.what=0x1112;
                        msg.obj=i;
                        ConsoleActivity.mHandler.sendMessage(msg);
                        Log.d(TAG, "run: offLine:"+curClient.getName()+" "+curClient.getAddress());
                    }
                }
                try {
                    Thread.sleep(CHECK_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public class MyBinder extends Binder {
        public ScriptService getService() {
            return ScriptService.this;
        }
    }
}
