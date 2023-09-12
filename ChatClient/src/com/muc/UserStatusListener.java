package com.muc;

public interface UserStatusListener{ // for online and offline use of who is available online, or shows whos offline 
    public void online(String login);
    public void offline(String login);
}