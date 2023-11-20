package com.insa.network;

import com.google.gson.GsonBuilder;
import com.insa.database.LocalDatabase;
import com.insa.utils.Constants;
import com.insa.utils.MyLogger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;

public class NetworkManager {
    private static volatile NetworkManager instance;
    private UDPServer udpServer;
    private InetAddress myIP;
    private String myIPString;


    public NetworkManager() {
        udpServer = new UDPServer();
        udpServer.start();
//        this.myIPString = ip;
//        throw new UnsupportedOperationException();
    }

    public void notifyConnected(ConnectedUser user) {
        if (LocalDatabase.Database.connectedUserList.stream().noneMatch(u -> u.getUsername().equals(user.getUsername()))) {
            MyLogger.info(String.format("Added user to connectedUserList: %s\n",
                    new GsonBuilder()
                            .setPrettyPrinting()
                            .create()
                            .toJson(user))
            );
            LocalDatabase.Database.connectedUserList.add(user);
        } else {
            MyLogger.info(String.format("User already in connectedUserList: %s\n",
                    new GsonBuilder()
                            .setPrettyPrinting()
                            .create()
                            .toJson(user))
            );
        }
    }

    public void notifyDisconnected(ConnectedUser user) {
        if (LocalDatabase.Database.connectedUserList.stream().anyMatch(u -> u.getUsername().equals(user.getUsername()))) {
            MyLogger.info(String.format("Removed user from connectedUserList: %s\n",
                    new GsonBuilder()
                            .setPrettyPrinting()
                            .create()
                            .toJson(user))
            );
            LocalDatabase.Database.connectedUserList.remove(user);
        } else {
            MyLogger.info(String.format("User not found in connectedUserList: %s\n",
                    new GsonBuilder()
                            .setPrettyPrinting()
                            .create()
                            .toJson(user))
            );
        }
    }

    public void discoverNetwork(String username) {
        // Create UDP Client
        // Check UDPServer running
        // CLient -> broadcast coucou
        // Waiting for answers with timeout
        // update contactlist

        UDPClient udpClient = new UDPClient();
        Message discoveryMessage = new Message();
        discoveryMessage.setType(Message.MessageType.DISCOVERY);
        discoveryMessage.setDate(new Date());
        discoveryMessage.setSender(new User(username));

        try {
            MyLogger.info("Broadcast sent");
            udpClient.sendBroadcast(discoveryMessage, Constants.UDP_SERVER_PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void informDisconnection(User user) {
        throw new UnsupportedOperationException();
    }

    public static NetworkManager getInstance() {
        NetworkManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized (NetworkManager.class) {
            if (instance == null) {
                instance = new NetworkManager();
            }
            return instance;
        }
    }
}
