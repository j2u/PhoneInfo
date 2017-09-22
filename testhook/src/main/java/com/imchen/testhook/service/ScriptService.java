package com.imchen.testhook.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.imchen.testhook.utils.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ScriptService extends Service {

    private final static int PORT = 3388;

    public ScriptService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        new scriptSocket().start();
        return null;
    }

    public class scriptSocket extends Thread {

        private ServerSocket server;
        private Socket socket;

        @Override
        public void run() {
            try {
                server = new ServerSocket(PORT);
                while (true) {
                    socket = server.accept();
                    BufferedReader buffer=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    StringBuilder builder=new StringBuilder("UTF-8");
                    String line;
                    while ((line=buffer.readLine())!=null){
                        builder.append(line);
                        builder.append(System.getProperty("line.separator"));
                    }
                    LogUtil.log(buffer.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
