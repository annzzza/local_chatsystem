package com.example.contactdiscovery;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Contact extends User {

    protected InetAddress ipAddress;

    public Contact(String username, String password, InetAddress ipAddress) {
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
