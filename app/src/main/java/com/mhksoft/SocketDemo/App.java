package com.mhksoft.SocketDemo;

import android.app.Application;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by MHK on 18/12/14.
 * www.MHKSoft.com
 */
public class App extends Application {
    private static Socket mSocket;

    public static void setSocket(String ip, String port) {
        try {
            IO.Options opts = new IO.Options();
            opts.reconnection = false;
            opts.query = "UserType=App";
            mSocket = IO.socket("http://" + ip + ":" + port, opts);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static Socket getSocket() {
        return mSocket;
    }
}
