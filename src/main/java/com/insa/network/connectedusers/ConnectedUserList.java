package com.insa.network.connectedusers;

import com.insa.utils.MyLogger;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConnectedUserList implements Iterable<ConnectedUser> {

    @Override
    public Iterator<ConnectedUser> iterator() {
        Iterator<ConnectedUser> it = new Iterator<ConnectedUser>() {
            private int currentIndex = 0;
            @Override
            public boolean hasNext() {
                return currentIndex < connectedUsers.size() && connectedUsers.get(currentIndex) != null;
            }

            @Override
            public ConnectedUser next() {
                return connectedUsers.get(currentIndex++);
            }
        };
        return it;
    }

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

    public synchronized boolean changeUsername(ConnectedUser user, String newUsername) {
        MyLogger.getInstance().info("Changing username");
        if (connectedUsers.stream().anyMatch(u -> u.getUsername().equals(newUsername))) {
            return false;
        } else {
            ConnectedUser newConnectedUser = new ConnectedUser(newUsername, user.getUuid(), user.getIP());
            this.connectedUsers.remove(user);
            this.connectedUsers.add(newConnectedUser);
            for (Observer observer : this.observers) {
                observer.usernameChanged(newConnectedUser, user.getUsername());
            }
            return true;
        }
    }

    public synchronized void removeConnectedUser(ConnectedUser connectedUser) {
        MyLogger.getInstance().info("Removing connected user");
        this.connectedUsers.remove(connectedUser);
        for (Observer observer : this.observers) {
            observer.removeConnectedUser(connectedUser);
        }
    }

    public synchronized boolean isEmpty() {
        MyLogger.getInstance().info("Checking if connected users is empty: "+this.connectedUsers.isEmpty()+"\n" + this.connectedUsers);
        return this.connectedUsers.isEmpty();
    }

    public synchronized int size() {
        return this.connectedUsers.size();
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

    public synchronized boolean contains(ConnectedUser connectedUser) {
        MyLogger.getInstance().info("Checking if connected user exists");

        return this.connectedUsers.contains(connectedUser);
    }

    public synchronized ConnectedUser getConnectedUser(String username) {
        MyLogger.getInstance().info("Getting connected user");
        return connectedUsers.stream().filter(u -> u.getUsername().equals(username)).findFirst().orElse(null);
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
