package com.muc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread{
    private final int serverPort;

    private ArrayList<ServerWorker> workerList = new ArrayList<>();//list of server workers 

    public Server(int serverPort) {
        this.serverPort = serverPort;
    }

    public List<ServerWorker> getWorkerList(){
        return workerList;
    }

    @Override
    public void run(){ // run method for the thread
        try{
            ServerSocket serverSocket = new ServerSocket(serverPort); // creating a new server socket
            while(true){
                Socket clientSocket = serverSocket.accept();
                ServerWorker worker = new ServerWorker(this, clientSocket);// passing the server and the client socket 
                workerList.add(worker); // add the server worker to the list 
                worker.start();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void removeWorker(ServerWorker serverWorker) {
        workerList.remove(serverWorker);
    }
}
