package com.example.contactdiscovery;

import java.net.InetAddress;
import java.util.List;

public class NetworkManager {
    private UDPServer udpServer;
    private InetAddress myIP;
    private String myIPString;
    private List<ConnectedUser> connectedUserList;

    public NetworkManager(String ip) {
        this.myIPString = ip;
        throw new UnsupportedOperationException();
    }
    public void notifyConnected(User user){
        throw new UnsupportedOperationException();
    }

    public void notifyDisconnected(User user) {
        throw new UnsupportedOperationException();
    }

    public List<ConnectedUser> discoverNetwork() {
        throw new UnsupportedOperationException();
    }

    public void informDisconnection(User user) {
        throw new UnsupportedOperationException();
    }
}
