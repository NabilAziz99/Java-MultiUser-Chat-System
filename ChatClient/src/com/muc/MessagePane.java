package com.muc;
//Nabil Abdelaziz Ferhat Taleb 

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MessagePane  extends JPanel implements MessageListener {


    private final ChatClient client;
    private final String login;
    private DefaultListModel<String> listModel=new DefaultListModel<>();
private JList<String> messageList=new JList<>(listModel);//creating new jlist
private JTextField inputField= new JTextField();//creating a new jtextfield 


    public MessagePane(ChatClient client, String login) {
        this.client=client;
        this.login=login;
        client.addMessageListener(this);
        setLayout(new BorderLayout()); //setting up the layouts
add(new JScrollPane(messageList), BorderLayout.CENTER); // add the message list in the center of the borderlayout
add(inputField,BorderLayout.SOUTH); // add the input field  in the south of the borderlayout
inputField.addActionListener(new ActionListener() {
    @Override
//adding and event listner to the input field 
    public void actionPerformed(ActionEvent e) {
        try {
            String text=inputField.getText();

            client.msg(login,text);//sending the text to the client
            listModel.addElement("You : "+text); //add to the conversation list
            inputField.setText("");//reset the text to empty
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
});
    }


    @Override
    public void onMessage(String fromLogin, String msgBody) {
        if(login.equalsIgnoreCase(fromLogin)) {
            String line = fromLogin + ": " + msgBody; 
            listModel.addElement(line);// adding a line where it includes the login and the msg body 
        }
    }


}