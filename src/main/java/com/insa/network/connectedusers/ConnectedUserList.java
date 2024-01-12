package com.insa.network.connectedusers;

import com.insa.utils.MyLogger;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ConnectedUserList {

    public interface Observer {
        void newConnectedUser(ConnectedUser connectedUser);
        void removeConnectedUser(ConnectedUser connectedUser);
        void usernameChanged(ConnectedUser newConnectedUser, String previousUsername);
    }

    private static final ConnectedUserList instance = new ConnectedUserList();

    public static ConnectedUserList getInstance() {
        return instance;
    }

    List<ConnectedUser> connectedUsers = new ArrayList<>();
    List<Observer> observers = new ArrayList<>();

    private ConnectedUserList() {
        MyLogger.getInstance().info("Creating ConnectedUserList");
    }

    public synchronized void addObserver(Observer observer) {
        MyLogger.getInstance().info("Adding observer");
        this.observers.add(observer);
    }

    public synchronized void addConnectedUser(String username, InetAddress ip) throws ConnectedUserAlreadyExists {
        MyLogger.getInstance().info("Adding connected user");
        if (hasUsername(username)) {
            throw new ConnectedUserAlreadyExists(new ConnectedUser(username, ip));
        } else {
            ConnectedUser connectedUser = new ConnectedUser(username, ip);
            this.connectedUsers.add(connectedUser);
            for (Observer observer : this.observers) {
                observer.newConnectedUser(connectedUser);
            }
        }
    }

    public synchronized void addConnectedUser(ConnectedUser connectedUser) throws ConnectedUserAlreadyExists {
        MyLogger.getInstance().info("Adding connected user");
        if (hasUsername(connectedUser.getUsername())) {
            throw new ConnectedUserAlreadyExists(connectedUser);
        } else {
            this.connectedUsers.add(connectedUser);
            for (Observer observer : this.observers) {
                observer.newConnectedUser(connectedUser);
            }
        }
    }

    public synchronized void removeConnectedUser(ConnectedUser connectedUser) {
        MyLogger.getInstance().info("Removing connected user");
        this.connectedUsers.remove(connectedUser);
        for (Observer observer : this.observers) {
            observer.removeConnectedUser(connectedUser);
        }
    }

    public synchronized boolean hasUsername(String username) {
        MyLogger.getInstance().info("Checking if username exists");
        for (ConnectedUser connectedUser : this.connectedUsers) {
            if (connectedUser.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(ConnectedUser connectedUser) {
        MyLogger.getInstance().info("Checking if connected user exists");

        return this.connectedUsers.contains(connectedUser);
    }

    public synchronized List<ConnectedUser> getAllConnectedUsers() {
        // defensive copy
        MyLogger.getInstance().info("Getting all connected users");
        return new ArrayList<>(this.connectedUsers);
    }

    public synchronized void clear() {
        MyLogger.getInstance().info("Clearing connected users");
        this.connectedUsers.clear();
    }
}
