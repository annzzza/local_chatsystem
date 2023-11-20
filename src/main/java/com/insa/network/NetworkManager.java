package com.insa.network;

import com.insa.database.LocalDatabase;
import com.insa.utils.Constants;
import com.insa.utils.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

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
        udpServer = new UDPServer();
        udpServer.start();
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

    public boolean discoverNetwork(String username) {
        // Create UDP Client
        // Check UDPServer running
        // CLient -> broadcast coucou
        // Waiting for answers with timeout
        // update contactlist
        boolean userInDB = false;

        UDPClient udpClient = new UDPClient();
        Message discoveryMessage = new Message();
        discoveryMessage.setType(Message.MessageType.DISCOVERY);
        discoveryMessage.setDate(new Date());
        discoveryMessage.setSender(new User(username));

        try {
            logger.log("Broadcast sent");
            udpClient.sendBroadcast(discoveryMessage, Constants.UDP_SERVER_PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<ConnectedUser> contactList = LocalDatabase.Database.connectedUserList;
        if (!contactList.isEmpty()){
            for (ConnectedUser connectedUser :contactList){
                if (connectedUser.getUsername().equals(username)) {
                    userInDB = true; break;
                }
            }
        }
        return userInDB;
    }

    public List<ConnectedUser> getConnectedUserList() {
        return this.connectedUserList;
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
