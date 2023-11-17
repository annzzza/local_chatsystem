package com.example.contactdiscovery;

import java.net.InetAddress;
import java.util.UUID;

public class ConnectedUser extends User {

    private InetAddress ip;

    public ConnectedUser(String username, InetAddress ip) {
        super(username);
        this.ip = ip;
    }
    public ConnectedUser(String username, UUID uuid, InetAddress ip) {
        super(username, uuid);
        this.ip = ip;
    }

    public ConnectedUser(User user, InetAddress ip) {
        super(user.getUsername(), user.getUuid());
        this.ip = ip;
    }

    public InetAddress getIP() {
        return this.ip;
    }
    public void setIP(InetAddress ip){
        this.ip=ip;
    }

}
