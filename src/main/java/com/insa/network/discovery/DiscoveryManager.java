package com.insa.network.discovery;

import com.insa.users.User;
import com.insa.users.ConnectedUserList;
import com.insa.utils.Constants;
import com.insa.utils.MyLogger;

import java.io.IOException;
import java.net.SocketException;


/**
 * Singleton DiscoveryManager
 * This class is used to manage the discovery of the network and the user's connection
 */
public class DiscoveryManager {
    private static final MyLogger LOGGER = new MyLogger(DiscoveryManager.class.getName());

    private static volatile DiscoveryManager instance;

    private final DiscoveryController discoveryController = new DiscoveryController();


    /**
     * Constructor of Singleton NetworkManager
     **/
    private DiscoveryManager() {
        try {
            UDPServer udpServer = new UDPServer(Constants.UDP_SERVER_PORT);
            udpServer.start();
            udpServer.addObserver(discoveryController);
        } catch (SocketException e) {
            LOGGER.severe("Error while creating UDP server: " + e.getMessage());
        }
    }

    /**
     * Get the user of the current application
     *
     * @return User of the current application
     */
    public User getCurrentUser() {
        return discoveryController.getCurrentUser();
    }

    /**
     * Create an instance of Discovery Message and broadcast it
     * @param username String requested username
     * @throws UsernameAlreadyTakenException if the username is already taken
     */
    public void discoverNetwork(String username) throws UsernameAlreadyTakenException {
        LOGGER.info("Begin client discovery");

        // Set current user
        User currentUser = new User(username);
        LOGGER.info("Current user set to " + currentUser.getUsername());
        discoveryController.setCurrentUser(currentUser);

        // Clear connected user list
        ConnectedUserList.getInstance().clear();

        // Message creation
        UDPClient udpClient = new UDPClient();
        UDPMessage discoveryMessage = new UDPMessage();
        discoveryMessage.setType(UDPMessage.MessageType.DISCOVERY);
        discoveryMessage.setSender(currentUser);

        try {
            LOGGER.info("Broadcast discovery sent");
            udpClient.sendBroadcast(discoveryMessage, Constants.UDP_SERVER_PORT);
        } catch (NoBroadcastAddressFound e) {
            LOGGER.severe(e.toString());
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }

        LOGGER.info("Waiting for responses");
        try {
            Thread.sleep(Constants.DISCOVERY_TIMEOUT);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if(ConnectedUserList.getInstance().hasUsername(username)) {
            LOGGER.info("Username already taken.");
            throw new UsernameAlreadyTakenException(username);
        }

        LOGGER.info("Discovery finished");
    }

    /**
     * Create an instance of UsernameChanged Message and broadcast it
     *
     * @param user        User requesting a change of Username
     * @param newUsername String requested new Username
     */
    public void sendChangeUsername(User user, String newUsername) throws UsernameAlreadyTakenException {
        try {
            LOGGER.info(String.format("Change username to: %s Message sent.", newUsername));

            if (ConnectedUserList.getInstance().hasUsername(newUsername)) {
                LOGGER.info("Username already taken.");
                throw new UsernameAlreadyTakenException(newUsername);
            } else {
                UDPClient udpClient = new UDPClient();
                UDPMessage changeUsernameMessage = new UDPMessage(UDPMessage.MessageType.USERNAME_CHANGED, newUsername, user);

                udpClient.sendBroadcast(changeUsernameMessage, Constants.UDP_SERVER_PORT);
                discoveryController.setCurrentUser(new User(newUsername, discoveryController.getCurrentUser().getUuid()));

            }
        } catch (NoBroadcastAddressFound e){
            LOGGER.severe(e.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create an instance of UserDisconnected Message and broadcast it
     *
     * @param user User disconnecting
     */
    public void sendDisconnection(User user) {
        try {
            LOGGER.info("Disconnection message sent.");

            UDPClient udpClient = new UDPClient();
            UDPMessage disconnectedMessage = new UDPMessage(UDPMessage.MessageType.USER_DISCONNECTED, "", user);

            udpClient.sendBroadcast(disconnectedMessage, Constants.UDP_SERVER_PORT);
            ConnectedUserList.getInstance().clear();
            discoveryController.setCurrentUser(null);
        } catch (NoBroadcastAddressFound e){
            LOGGER.severe(e.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Get the instance of the singleton
     * @return DiscoveryManager instance
     */
    public static DiscoveryManager getInstance() {
        DiscoveryManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized (DiscoveryManager.class) {
            if (instance == null) {
                instance = new DiscoveryManager();
            }
            return instance;
        }
    }
}
