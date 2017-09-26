package com.imchen.testhook.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.imchen.testhook.utils.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ScriptService extends Service {

    private final static int PORT = 3388;
    public ScriptService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        new scriptSocket().start();
        return new MyBinder();
    }

    public class scriptSocket extends Thread {

        private ServerSocket server;
        private Socket socket;
        private ArrayList<ClientThread> listClient=new ArrayList<>();

        @Override
        public void run() {
            try {
                server = new ServerSocket(PORT);

                int clientNum=0;
                while (true) {
                    LogUtil.log("ServerSocket have been create! current client:"+clientNum);
                    socket = server.accept();
                    clientNum++;
                    LogUtil.log("current client num : "+clientNum);
                    ClientThread thClient=new ClientThread(socket);
                    thClient.start();
                    listClient.add(thClient);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ClientThread extends  Thread{

        private Socket client;
        private BufferedReader reader;

        public ClientThread(Socket client) {
            this.client=client;
        }

        @Override
        public void run() {
            while (true){
                if (reader==null){
                    try {
                        reader = new BufferedReader(new InputStreamReader(client.getInputStream(),"UTF-8"));
                        String line;
                        while ((line=reader.readLine())!=null){
                            LogUtil.log("client say:*********************"+line+"*************************");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    public class MyBinder extends Binder {
        public ScriptService getService(){
            return ScriptService.this;
        }
    }
}
