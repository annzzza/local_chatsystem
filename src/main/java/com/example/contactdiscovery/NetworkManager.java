package com.example.contactdiscovery;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class NetworkManager {
    private static volatile NetworkManager instance;
    private UDPServer udpServer;
    private InetAddress myIP;
    private String myIPString;
    private List<ConnectedUser> connectedUserList;


    public NetworkManager() {
        connectedUserList = new ArrayList<>();
//        this.myIPString = ip;
//        throw new UnsupportedOperationException();
    }
    public void notifyConnected(ConnectedUser user){
        if(connectedUserList.stream().noneMatch(u -> u.getUsername().equals(user.getUsername()))) {
            System.out.printf("Added user to contactList: %s%n", user.getUsername());
            connectedUserList.add(user);
        } else {
            System.out.printf("User already in contactList: %s%n", user.getUsername());
        }
    }

    public void notifyDisconnected(ConnectedUser user) {
        if(connectedUserList.contains(user)) {
            System.out.printf("Removed user from contactList: %s%n", user.getUsername());
            connectedUserList.remove(user);
        }
    }

    public List<ConnectedUser> discoverNetwork() {
        throw new UnsupportedOperationException();
    }

    public void informDisconnection(User user) {
        throw new UnsupportedOperationException();
    }

    public static NetworkManager getInstance() {
        NetworkManager result = instance;
        if(result != null) {
            return result;
        }
        synchronized (NetworkManager.class) {
            if(instance == null) {
                instance = new NetworkManager();
            }
            return instance;
        }
    }
}
