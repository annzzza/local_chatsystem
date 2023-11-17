package com.example.contactdiscovery;

import java.util.UUID;

public class User {

    private String username;
    private UUID uuid;


    public User(String name){
        this.username=name;
    }
    public User(String name, UUID uuid){
        this.username=name;
        this.uuid=uuid;
    }


    public boolean equals(User user){
        return (this.uuid==user.uuid);
    }



    public void setUsername(String name) {
        this.username=name;
    }
    public String getUsername(){
        return this.username;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String toString(){
        return "username: " + this.username + "\nUUID: " + this.uuid.toString();
    }


}
