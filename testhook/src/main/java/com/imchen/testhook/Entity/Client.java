package com.imchen.testhook.Entity;

import com.imchen.testhook.ClientThread;

/**
 * Created by imchen on 2017/9/28.
 */

public class Client {

    private String name;
    private String address;
    private int status;
    private ClientThread clientThread;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ClientThread getClientThread() {
        return clientThread;
    }

    public void setClientThread(ClientThread clientThread) {
        this.clientThread = clientThread;
    }
}
