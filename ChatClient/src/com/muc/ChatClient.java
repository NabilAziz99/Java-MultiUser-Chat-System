package com.muc;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

public class ChatClient{
    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private InputStream serverIn;
    private OutputStream serverOut;
    private BufferedReader BufferedIn;

    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();
    public ChatClient(String serverName, int serverPort){
        this.serverName = serverName;
        this.serverPort = serverPort;
    }
    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 8818);
        client.addUserStatusListener(new UserStatusListener(){
            @Override
            public void online(String login){
                System.out.println("ONLINE "+ login); // handles user login and displays the online
            }
            @Override
            public void offline(String login){
                System.out.println("OFFLINE "+ login); // handles user logoff and displays the offline
            }
        });
        client.addMessageListener(new MessageListener() {
            @Override // overrides the onmessage to say that you have a specific message. so this is direct messaging
            public void onMessage(String fromLogin, String msgBody){
                System.out.println("You got a message from "+fromLogin+" ----> "+msgBody);
            }
        });
        // this handsles the connection. as well as the login information for the guest and test
        if (!client.connect()){
            System.err.println("Connect Failed.");
        } else {
            System.out.println("Connect Successful");

            if(client.login("guest", "guest")){
                System.out.println("Login Successful");
                client.msg("test", "Hello!");
            } else {
                System.out.println("Login failed");
            }
            //client.logoff();
        } 
    }

    public void msg(String sendTO, String msgBody) throws IOException{
        String cmd = "msg" + sendTO + " " + msgBody + "\n";
        serverOut.write(cmd.getBytes());
    }
    public void logoff() throws IOException{
        String cmd = "logoff\n";
        serverOut.write(cmd.getBytes());
    }
    public boolean login(String login, String password) throws IOException{
        String cmd = "login" + login + " " + password + "\n";
        serverOut.write(cmd.getBytes());

        String response = BufferedIn.readLine();
        System.out.println("Response Line: " + response);
        // if the login username and password are correct, it prints out the ok login
        if("ok login".equalsIgnoreCase(response)){
            startMessageReader();
            return true;
        } else {
            return false;
        }
    }
    private void startMessageReader() {
        Thread t = new Thread() {
            @Override
            public void run(){
                readMessageLoop();
            }
        };
        t.start();
    }
    // opens a read message loop
    private void readMessageLoop() {
        try {
            String line;
            while((line = BufferedIn.readLine()) != null){
                String[] tokens = StringUtils.split(line);
                if(tokens != null && tokens.length > 0){
                    String cmd = tokens[0];
                    if("online".equalsIgnoreCase(cmd)){
                        handleOnline(tokens);
                    } else if ("offline".equalsIgnoreCase(cmd)){
                        handleOffline(tokens);
                    }else if("msg".equalsIgnoreCase(cmd)){
                        String[] tokensMsg = StringUtils.split(line, null, 3);
                        handleMessage(tokensMsg);
                    }
                }
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }try {
            socket.close();
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }
    };// each thing is a token. the login being 1 and the msg body being 2
    private void handleMessage(String[] tokensMsg) {
        String login = tokensMsg[1];
        String msgBody = tokensMsg[2];

        for(MessageListener listener : messageListeners){
            listener.onMessage(login, msgBody);
        }
    } // handles the offline tokens
    private void handleOffline(String[] tokens) {
        String login =  tokens[1];
        for(UserStatusListener listener : userStatusListeners){
            listener.offline(login);
        }
    }// handles the online tokens
    private void handleOnline(String[] tokens) {
        String login =  tokens[1];
        for(UserStatusListener listener : userStatusListeners){
            listener.online(login);
        }
    }
    // if the client port is 8818, then returns true
    public boolean connect(){
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.BufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }
        return false;
    }
    // user status listeners added and removed
    public void addUserStatusListener(UserStatusListener listener){
        userStatusListeners.add(listener);
    }
    public void removeUserStatusListener(UserStatusListener listener){
        userStatusListeners.remove(listener);
    }

    // adds and removes message listeners
    public void addMessageListener(MessageListener listener){
        messageListeners.add(listener);
    }
    public void removeMessageListener(MessageListener listener){
        messageListeners.remove(listener);
    }
}