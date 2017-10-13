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
    private final static long CHECK_TIME = 1000 * 8;

    private final static int PORT = 3388;

    private final static int MAX_PING_TIMES=3;
    private int curPingTimes=0;

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

                    ClientThread thClient = new ClientThread(socket);
                    thClient.start();

                    String remoteIp = socket.getInetAddress().toString().substring(1);
                    boolean isNewUser = true;
                    for (int i = 0; i < ConsoleActivity.clientArrayList.size(); i++) {
                        Client tmpClient = ConsoleActivity.clientArrayList.get(i);
                        if (tmpClient.getAddress().equals(remoteIp)) {
                            tmpClient.setStatus(1);
                            tmpClient.setClientThread(thClient);
                            tmpClient.setName(thClient.getName());
                            notifyStatus(i);
                            isNewUser = false;
                        }
                    }
                    if (isNewUser) {
                        Client client = new Client();
                        client.setName(thClient.getName());
                        client.setAddress(remoteIp);
                        client.setStatus(1);
                        client.setClientThread(thClient);
                        listClient.add(thClient);
                        ConsoleActivity.clientArrayList.add(0, client);
                        listClientEntity.add(client);

                        Message msg = new Message();
                        msg.what = 0x1111;
                        msg.obj = listClientEntity;
                        ConsoleActivity.mHandler.sendMessage(msg);

                        clientNum++;
                        LogUtil.log("current client num : " + clientNum);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public class thCheckClientStatus extends Thread {

        private ArrayList<Client> clientList;

        public thCheckClientStatus(ArrayList<Client> clientList) {
            this.clientList =clientList;

        }

        @Override
        public void run() {
            while (true) {
                for (int i = 0; i < clientList.size(); i++) {
                    Log.d(TAG, "Thread for checking client isConnected per 8 second");
                    Client curClient = clientList.get(i);
//                    Log.d(TAG, "run: isOnLine:" + curClient.getName() + " " + curClient.getAddress() + " isOnline:");
                    ClientThread thread = curClient.getClientThread();

                    if (thread.isOffLine && curClient.getStatus() == 1 ) {
                        curClient.setStatus(-1);
                        thread.isOffLine=true;
                        notifyStatus(i);
                        Log.d(TAG, "run: offLine:" + curClient.getName() + " " + curClient.getAddress());
                    }
                    if (curClient.getStatus()>=0){
                        if (!checkIsAlive(curClient)){
                            curClient.setStatus(-2);
                            thread.isOffLine=true;
                            notifyStatus(i);
                            Log.d(TAG, "run: deadline:" + curClient.getName() + " " + curClient.getAddress());
                        }
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

    private void notifyStatus(int index) {
        Message msg = new Message();
        msg.what = 0x1112;
        msg.obj = index;
        ConsoleActivity.mHandler.sendMessage(msg);
    }

    /**
     * send heartbeat
     *
     * @param myClient
     * @return
     */
    public boolean checkIsAlive(Client myClient) {
        try {
            boolean result=true;
            ClientThread thread=myClient.getClientThread();
            Socket client=thread.client;
            client.sendUrgentData(0);
            if (!thread.isOffLine){
                result=pingIpAddress(client.getInetAddress().toString().substring(1));
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "checkIsAlive: "+e );
            return false;
        }
    }

    private boolean pingIpAddress(String ipAddress) {

        try {
            Log.d(TAG, "pingIpAddress: ping "+ipAddress);
            Process process = Runtime.getRuntime().exec("ping -c 1 -W 8 " + ipAddress);
            int status = process.waitFor();
            Log.d(TAG, "pingIpAddress: ping status:" + status);
            if (status == 0) {
                return true;
            } else {
                curPingTimes++;
                if (curPingTimes<MAX_PING_TIMES){
                    //try to ping 3 time
                    pingIpAddress(ipAddress);
                }
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }


    public class MyBinder extends Binder {
        public ScriptService getService() {
            return ScriptService.this;
        }
    }
}
