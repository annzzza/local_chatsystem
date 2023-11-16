package com.example.contactdiscovery;

import java.net.InetAddress;

public class ConnectedUser extends User {

    protected InetAddress ipAddress;

    public ConnectedUser(String username, String password, InetAddress ipAddress) {
        super(username, password);
        this.ipAddress = ipAddress;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress address) {
        this.ipAddress = address;
    }



}
