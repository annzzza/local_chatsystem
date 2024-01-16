package com.insa.users;

import com.insa.network.discovery.UDPClient;
import com.insa.utils.MyLogger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * List of connected users without duplicates
 */
public class ConnectedUserList implements Iterable<ConnectedUser> {

    private static final MyLogger LOGGER = new MyLogger(UDPClient.class.getName());

    /**
     * Iterator for the list of connected users (used to iterate over the list)
     */
    @Override
    public Iterator<ConnectedUser> iterator() {

        return new Iterator<>() {
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
    }

    /**
     * Observer interface for the connected user list
     */
    public interface Observer {
        /**
         * Called when a new user connects
         * @param connectedUser The user that connected
         */
        void newConnectedUser(ConnectedUser connectedUser);

        /**
         * Called when a user disconnects
         * @param connectedUser The user that disconnected
         */
        void removeConnectedUser(ConnectedUser connectedUser);

        /**
         * Called when a user changes their username
         * @param newConnectedUser The user that changed their username
         * @param previousUsername The previous username of the user
         */
        void usernameChanged(ConnectedUser newConnectedUser, String previousUsername);
    }

    /**
     * Singleton instance of the connected user list
     */
    private static final ConnectedUserList instance = new ConnectedUserList();

    /**
     * Getter for the singleton instance
     * @return The singleton instance of the connected user list
     */
    public static ConnectedUserList getInstance() {
        return instance;
    }

    /**
     * List of connected users
     */
    List<ConnectedUser> connectedUsers = new ArrayList<>();

    /**
     * List of subscribers to the connected user list
     */
    List<Observer> observers = new ArrayList<>();

    /**
     * Private constructor for the singleton
     */
    private ConnectedUserList() {
        LOGGER.info("Creating ConnectedUserList");
    }


    /**
     * Adds a new observer to the class
     * @param observer The observer to add
     */
    public synchronized void addObserver(Observer observer) {
        LOGGER.info("Adding observer");
        this.observers.add(observer);
    }


    /**
     * Adds a new connected user to the list without duplicates
     * @param connectedUser The connected user to add
     * @throws ConnectedUserAlreadyExists If the user already exists in the list
     */
    public synchronized void addConnectedUser(ConnectedUser connectedUser) throws ConnectedUserAlreadyExists {
        LOGGER.info("Adding connected user");
        if (hasUsername(connectedUser.getUsername())) {
            throw new ConnectedUserAlreadyExists(connectedUser);
        } else {
            this.connectedUsers.add(connectedUser);
            for (Observer observer : this.observers) {
                observer.newConnectedUser(connectedUser);
            }
        }
    }

    /**
     * Changes the username of a connected user
     * @param user The user to change the username of
     * @param newUsername The new username of the user
     * @return True if the username has been changed, false otherwise
     */
    public synchronized boolean changeUsername(ConnectedUser user, String newUsername) {
        LOGGER.info("Changing username");
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

    /**
     * Removes a connected user from the list
     * @param connectedUser The connected user to remove
     */
    public synchronized void removeConnectedUser(ConnectedUser connectedUser) {
        LOGGER.info("Removing connected user");
        this.connectedUsers.remove(connectedUser);
        for (Observer observer : this.observers) {
            observer.removeConnectedUser(connectedUser);
        }
    }

    /**
     * Checks if the list of connected users is empty
     * @return True if the list is empty, false otherwise
     */
    public synchronized boolean isEmpty() {
        LOGGER.info("Checking if connected users is empty: "+this.connectedUsers.isEmpty()+"\n" + this.connectedUsers);
        return this.connectedUsers.isEmpty();
    }

    /**
     * Returns the size of the list of connected users
     * @return The size of the list of connected users
     */
    public synchronized int size() {
        return this.connectedUsers.size();
    }

    /**
     * Checks if a username is already used in the list of connected users
     * @param username The username to check
     * @return True if the username is already used, false otherwise
     */
    public synchronized boolean hasUsername(String username) {
        LOGGER.info("Checking if username exists");
        for (ConnectedUser connectedUser : this.connectedUsers) {
            if (connectedUser.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a connected user is already in the list of connected users
     */
    public synchronized boolean contains(ConnectedUser connectedUser) {
        LOGGER.info("Checking if connected user exists");
        return this.connectedUsers.contains(connectedUser);
    }

    /**
     * Returns the connected user with the given username
     * @param username The username of the connected user to return
     * @return The connected user with the given username, null if no connected user with this username exists
     */
    public synchronized ConnectedUser getConnectedUser(String username) {
        LOGGER.info("Getting connected user");
        return connectedUsers.stream().filter(u -> u.getUsername().equals(username)).findFirst().orElse(null);
    }

    /**
     * Returns the entire list of connected users
     * @return The entire list of connected users
     */
    public synchronized List<ConnectedUser> getAllConnectedUsers() {
        // defensive copy
        LOGGER.info("Getting all connected users");
        return new ArrayList<>(this.connectedUsers);
    }

    /**
     * Clears the list of connected users
     */
    public synchronized void clear() {
        LOGGER.info("Clearing connected users");
        this.connectedUsers.clear();
    }
}
