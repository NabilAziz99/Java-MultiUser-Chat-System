//Nabil Abdelaziz Ferhat Taleb

package com.muc;
import javax.swing.*;//declaring libraries
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class UserListPane extends JPanel implements UserStatusListener {
    private final ChatClient client;
    private JList<String> userListUI;//shoz the list of users
    private DefaultListModel<String> userListModel;

    public UserListPane(ChatClient client) {
        this.client=client;
        this.client.addUserStatusListener(this);// adding a presence listner
        userListModel= new DefaultListModel<>();
        userListUI=new JList<>(userListModel);// passing a user list model 
        setLayout(new BorderLayout());//setting up the layout
        add(new JScrollPane(userListUI),BorderLayout.CENTER);



        userListUI.addMouseListener(new MouseAdapter()//create an event listner 
 {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount()>1){
                  String login=   userListUI.getSelectedValue();//get which login the user clicked on 
                  MessagePane messagePane= new MessagePane(client, login);
                  JFrame f= new JFrame("Message: "+login);//create a message pane
                  f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                  f.setSize(500,500);//setting up the size
                  f.getContentPane().add(messagePane,BorderLayout.CENTER);//add out message pane in the center of the border layout
                  f.setVisible(true) ;//make it visible 
                }
            }
        });

    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient("localhost", 8818);
        UserListPane userListPane= new UserListPane(client); //obkect declaration
        JFrame frame= new JFrame("User List"); // display the pane on a window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//This causes the application to exit when the application receives a close window event from the operating system
        frame.setSize(400,600);//setting up the dimentions 
        frame.getContentPane().add(userListPane, BorderLayout.CENTER);//adding userlistPane as the main component of the frame
        frame.setVisible(true);
        if(client.connect()){//if the user can connect, we can login
            try {
                client.login("guest", "guest");
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    @Override
    public void online(String login){
        userListModel.addElement(login);//add the user to the model when online
    }
    @Override
    public void offline(String login){
        userListModel.removeElement(login);//remove  the user to the model when online

    }
}

