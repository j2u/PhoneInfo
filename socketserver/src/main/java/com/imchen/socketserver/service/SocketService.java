package com.imchen.socketserver.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;

import com.imchen.socketserver.utils.LogUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketService extends Service {

    private final static String TAG="imchen";

    public SocketService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        LogUtil.log("onBind:");
        return  null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        new Thread(){
            @Override
            public void run() {
                startSocketServer("192.168.0.1",3838);
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void startSocketServer(String host,int port){
        try {
            ServerSocket serverSocket =new ServerSocket(port);
            while (true){
                Socket socket=serverSocket.accept();
                OutputStream out=socket.getOutputStream();
                out.write("ok ! i am server!".getBytes("UTF-8"));
                LogUtil.log("socket address: "+socket.getInetAddress());
                out.close();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
