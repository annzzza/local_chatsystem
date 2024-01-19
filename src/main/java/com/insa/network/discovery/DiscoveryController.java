package com.insa.network.discovery;

import com.insa.users.ConnectedUser;
import com.insa.users.ConnectedUserAlreadyExists;
import com.insa.users.ConnectedUserList;
import com.insa.users.User;
import com.insa.utils.Constants;
import com.insa.utils.MyLogger;

import java.net.UnknownHostException;

/**
 * Controller of DiscoveryManager
 */
public class DiscoveryController implements UDPServer.Observer {

    private static final MyLogger LOGGER = new MyLogger(DiscoveryController.class.getName());
    private User currentUser = null; // User of the current application

    /**
     * Default Constructor of DiscoveryController
     */
    public DiscoveryController() {
    }

    /**
     * Constructor of DiscoveryController
     *
     * @param currentUser User of the current application
     */
    public DiscoveryController(User currentUser) {
        this.currentUser = currentUser;
    }


    /**
     * Set the current user
     * Package private because it is only used by DiscoveryManager
     *
     * @param currentUser User of the current application
     */
    void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Get the current user
     * Package private because it is only used by DiscoveryManager
     *
     * @return User of the current application
     */
    User getCurrentUser() {
        return currentUser;
    }


    /**
     * Display the list of connected users
     */
    private void displayConnectedUsers() {
        LOGGER.info("Connected users:");
        for (ConnectedUser user : ConnectedUserList.getInstance()) {
            LOGGER.info("\t\t" + user.getUsername());
        }
    }

    @Override
    public void handleMessage(UDPMessage message) {
        LOGGER.info("Discovery - Message received:\n " + message);
        ConnectedUser user = new ConnectedUser(message.getSender(), message.getSenderIP());

        switch (message.getType()) {
            case DISCOVERY -> onNewUserConnected(user);
            case USER_CONNECTED -> onAnswerReceived(user);
            case USERNAME_CHANGED -> onUsernameChanged(user, message.getContent());
            case USER_DISCONNECTED -> onUserDisconnected(user);
            default -> LOGGER.info("Discovery - Unknown message type: " + message.getType());
        }
    }

    /**
     * Handle a new user trying to connect
     * @param user ConnectedUser of the new user
     */
    public void onNewUserConnected(ConnectedUser user) {
        LOGGER.info("Discovery - New user trying to connect: " + user);

        // Send back that we are connected
        try {
            UDPMessage answer = new UDPMessage(UDPMessage.MessageType.USER_CONNECTED, "", this.currentUser);
            UDPClient udpClient = new UDPClient();
            udpClient.sendUDP(answer, Constants.UDP_SERVER_PORT, user.getIP().toString().substring(1));

        } catch (UnknownHostException e) {
            LOGGER.severe("Error while creating UDP client: " + e.getMessage());
        }

        // Add the user to the connectedUserList
        try {
            if (this.currentUser != null && this.currentUser.getUsername().equals(user.getUsername())) {
                LOGGER.info("New user tried to connect with the same username as the current user, has not been added to connectedUserList");
                return;
            }
            ConnectedUserList.getInstance().addConnectedUser(user);
            LOGGER.info("Added user to connectedUserList: " + user);
        } catch (ConnectedUserAlreadyExists e) {
            LOGGER.info("Received duplicated connectedUser: " + e);
            return;
        }
        displayConnectedUsers();


    }

    /**
     * Handle an answer from a user
     * @param user ConnectedUser of the user
     */
    public void onAnswerReceived(ConnectedUser user) {
        LOGGER.info("Discovery - Answer received from user: " + user);
        try {
            ConnectedUserList.getInstance().addConnectedUser(user);
            LOGGER.info("Added user to connectedUserList: " + user);
        } catch (ConnectedUserAlreadyExists e) {
            LOGGER.info("Received duplicated connectedUser: " + e);
            this.setCurrentUser(null);
        }
        displayConnectedUsers();
    }

    /**
     * Handle a username change request
     * @param user ConnectedUser of the user
     * @param newUsername String new username
     */
    public void onUsernameChanged(ConnectedUser user, String newUsername) {
        LOGGER.info("Discovery - User asking to change username from: " + user.getUsername() + " to " + newUsername);
        LOGGER.info("Discovery - Current user: " + this.currentUser.getUsername());
        if (this.currentUser.getUsername().equals(newUsername)) {
            LOGGER.info("Username has not been changed because it is already taken.");
            return;
        }

        if (ConnectedUserList.getInstance().changeUsername(user, newUsername)) {
            LOGGER.info(String.format("Username has been changed in connectedUserList: %s\n", newUsername));
        } else {
            LOGGER.info("Username already used in connectedUserList, has not been updated.");
        }
        displayConnectedUsers();
    }

    /**
     * Handle a user disconnection
     * @param user ConnectedUser of the user
     */
    public void onUserDisconnected(ConnectedUser user) {
        LOGGER.info("Discovery - User disconnected: " + user);
        if (ConnectedUserList.getInstance().hasUsername(user.getUsername())) {
            ConnectedUserList.getInstance().removeConnectedUser(user);
            LOGGER.info("Removed user from connectedUserList: " + user);
        } else {
            LOGGER.info("User not found in connectedUserList: " + user);
        }
        displayConnectedUsers();
    }
}
