package com.insa.GUI.view;

import java.util.UUID;

public class ChatClass {

    private UUID chatUUID;

    private String usernameChatter;


    public UUID getChatUUID() {
        return chatUUID;
    }

    public void setChatUUID() {
        this.chatUUID = UUID.randomUUID();
    }

    public String getUsernameChatter() {
        return usernameChatter;
    }

    public void setUsernameChatter(String usernameChatter) {
        this.usernameChatter = usernameChatter;
    }

    public ChatClass(String usernameChatter){
        setChatUUID();
        setUsernameChatter(usernameChatter);
    }


    public String toString(){
        return usernameChatter;
    }
}
