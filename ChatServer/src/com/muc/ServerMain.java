package com.muc;

import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;

public class ServerMain{
    public static void main(String[] args){ // we set up a port to be able to connect
        int port = 8818;
        Server server = new Server(port);
        server.start();//make the thread starts 
    }


}
