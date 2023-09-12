package com.muc;

public interface MessageListener { // message listener so that it knows theres a message in hold, to send

    void onMessage(String login, String msgBody);
}
