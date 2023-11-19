package com.insa.network;

import com.insa.utils.Logger;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class NetworkManager {
    private static volatile NetworkManager instance;
    private UDPServer udpServer;
    private InetAddress myIP;
    private String myIPString;
    private final List<ConnectedUser> connectedUserList;
    private com.insa.utils.Logger logger;


    public NetworkManager() {
        connectedUserList = new ArrayList<>();
        logger = Logger.getInstance();
//        this.myIPString = ip;
//        throw new UnsupportedOperationException();
    }
    public void notifyConnected(ConnectedUser user){
        if(connectedUserList.stream().noneMatch(u -> u.getUsername().equals(user.getUsername()))) {
            logger.log(String.format("Added user to contactList: %s%n", user.getUsername()));
            connectedUserList.add(user);
        } else {
            logger.log(String.format("User already in contactList: %s%n", user.getUsername()));
        }
    }

    public void notifyDisconnected(ConnectedUser user) {
        if(connectedUserList.stream().anyMatch(u -> u.getUsername().equals(user.getUsername()))) {
            logger.log(String.format("Removed user from contactList: %s%n", user.getUsername()));
            connectedUserList.remove(user);
        } else {
            logger.log(String.format("User not found in contactList: %s%n", user.getUsername()));
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
