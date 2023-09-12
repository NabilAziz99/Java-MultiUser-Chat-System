package com.muc;

import java.io.*;
import java.lang.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
 import org.apache.commons.lang3.StringUtils; // in order for you to use StringUtils, we added a library for apache commons

public class ServerWorker extends Thread{

    private final Socket clientSocket;
    private final Server server;
    private String login = null;
    private OutputStream outputStream;
    private HashSet<String> topicSet = new HashSet<>(); // new hashset created for our topic

    public ServerWorker(Server server, Socket clientSocket){
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run(){
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    // loop that reads lines from client
    private void handleClientSocket() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while((line = reader.readLine()) != null){
            String[] tokens = line.split(" ");//split lines to individual tokens
            if(tokens != null && tokens.length > 0){
                String cmd = tokens[0];//our first token will be cmd
                if("logoff".equals(cmd) || "quit".equalsIgnoreCase(cmd)){
                    handleLogoff();// call log off
                    break;
                }
                else if("login".equalsIgnoreCase(cmd)){ // execute if the string comparison is true regardless of the higher cases or lower cases
                    handleLogin(outputStream, tokens);//passing the tokens and the ouputstream  
                }
                else if ("msg".equalsIgnoreCase(cmd)) { // msg <user> text....
                    String[] tokenMsg = StringUtils.split(line);
                    handleMessage(tokenMsg);
                }
                else if ("join".equalsIgnoreCase(cmd)) { // joins the chat and handles the join token
                    handleJoin(tokens);
                }
                else{
                    String msg = "unknown " + cmd + "\n"; //if there is an error, this message will be displayed
                    outputStream.write(msg.getBytes());
                }
                String msg = "You typed " + line + "\n";
                outputStream.write(msg.getBytes());
            }
        }
        clientSocket.close();
    }
    // if member is of topic, it will return the topic set
    public boolean isMemberOfTopic(String topic){
        return topicSet.contains(topic);
    }

    /*
        #topic <-- chatroom / groupchat
        join #topic
        msg #topic body...
    */
    private void handleJoin(String[] tokens) {
        if (tokens.length > 1) {
            String topic = tokens[1];
            topicSet.add(topic);
        }
    }

    // msg <user> text..... 
    // i.e. "msg guest hello world!" <<-sent
    // "msg guest hello world" <<-received
    // format "msg" "login" body.....
    // second format = "msg" "#topic" body...
    private void handleMessage(String[] tokens) throws IOException {
        String sendTo = tokens[1];
        String body = tokens[2];

        boolean isTopic = sendTo.charAt(0) == '#';
        // iterate through the list of server workers
        List<ServerWorker> workerList = server.getWorkerList();
        for (ServerWorker worker : workerList) {
            if(isTopic){
                if(worker.isMemberOfTopic(sendTo)){ // if it matches, then it sends the message to the particular worker
                    String outMsg = "msg" + sendTo + ":" + login + " " + body + "\n";
                    worker.send(outMsg);
                }
            }
            if (sendTo.equalsIgnoreCase(worker.getLogin())) {
                String outMsg = "msg" + login + " " + body + "\n";
                worker.send(outMsg);
            }
        }
    }
    // removes yourselve from the worker list
    // 
    private void handleLogoff() throws IOException {
        server.removeWorker(this);
        List<ServerWorker> workerList = server.getWorkerList();
        //send other online users current user's status
        String onlineMsg = "offline " + login + "\n";
        for(ServerWorker worker : workerList){
            if(!login.equals(worker.getLogin())) {
                worker.send(onlineMsg);
            }
        }
        clientSocket.close();
    }

    public String getLogin(){
        return login;
    }

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if(tokens.length == 3){
            String login = tokens[1];//first token will represent the login 
            String password = tokens[2];//second token will represent the password
            if((login.equals("guest") && password.equals("guest") || login.equals("test") && password.equals("test"))){ //set up of conditions to login
                String msg = "ok login\n";
                outputStream.write(msg.getBytes());//output
                this.login = login;
                System.out.println("User logged in successfully: " + login);//output 

                List<ServerWorker> workerList = server.getWorkerList();

                //send current user all other online logins
                for(ServerWorker worker : workerList){
                    if(worker.getLogin() != null) {
                        if(!login.equals(worker.getLogin())) {// to avoid sending a message of login to yourself
                            String msg2 = "online" + worker.getLogin() + "\n";
                            send(msg2);
                        }
                    }
                }

                //send other online users current user's status
                String onlineMsg = "online " + login + "\n";
                for(ServerWorker worker : workerList){
                    if(!login.equals(worker.getLogin())) {
                        worker.send(onlineMsg);
                    }
                }
            }
            else{
                String msg = "error login\n";//send error if failed to login
                outputStream.write(msg.getBytes());
                System.err.println("Login failed for "+ login);
            }
        }
    }

    private void send(String onlineMsg) throws IOException{
        if(login != null){//avoid sending a message is the user isnt login 

            outputStream.write(onlineMsg.getBytes());
        }

    }
}