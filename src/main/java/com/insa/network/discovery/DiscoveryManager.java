package com.insa.network.discovery;

import com.insa.database.LocalDatabase;
import com.insa.users.User;
import com.insa.users.ConnectedUser;
import com.insa.users.ConnectedUserList;
import com.insa.utils.Constants;
import com.insa.utils.MyLogger;

import java.io.IOException;
import java.net.SocketException;

public class DiscoveryManager {
    private static final MyLogger LOGGER = new MyLogger(DiscoveryManager.class.getName());

    private static volatile DiscoveryManager instance;


    /**  Constructor of Singleton NetworkManager  **/
    private DiscoveryManager() {
        try {
            UDPServer udpServer = new UDPServer(Constants.UDP_SERVER_PORT);
            udpServer.start();
            udpServer.addObserver(new DiscoveryController());
        } catch (SocketException e) {
            LOGGER.severe("Error while creating UDP server: " + e.getMessage());
        }
    }

    public void discoverNetwork(String username) {
        LOGGER.info("Begin client discovery");

        LocalDatabase.Database.currentUser = new User(username);

        // Message creation
        UDPClient udpClient = new UDPClient();
        UDPMessage discoveryMessage = new UDPMessage();
        discoveryMessage.setType(UDPMessage.MessageType.DISCOVERY);
        discoveryMessage.setSender(LocalDatabase.Database.currentUser);

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

        LOGGER.info("Discovery finished");
    }
    /**
     * Create an instance of UsernameChanged Message and broadcast it
     *
     * @param user ConnectedUser requesting a change of Username
     * @param newUsername String requested new Username
     */
    public void sendChangeUsername(ConnectedUser user, String newUsername){
        try {
            LOGGER.info(String.format("Change username to: %s Message sent.", newUsername));

            UDPClient udpClient = new UDPClient();
            UDPMessage changeUsernameMessage = new UDPMessage(UDPMessage.MessageType.USERNAME_CHANGED, newUsername, user);

            udpClient.sendBroadcast(changeUsernameMessage, Constants.UDP_SERVER_PORT);
            LocalDatabase.Database.currentUser = new User(newUsername, LocalDatabase.Database.currentUser.getUuid());
        } catch (NoBroadcastAddressFound e){
            LOGGER.severe(e.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create an instance of UserDisconnected Message and broadcast it
     *
     * @param user ConnectedUser disconnecting
     */
    public void sendDisconnection(ConnectedUser user) {
        try {
            LOGGER.info("Disconnection message sent.");

            UDPClient udpClient = new UDPClient();
            UDPMessage disconnectedMessage = new UDPMessage(UDPMessage.MessageType.USER_DISCONNECTED, "", user);

            udpClient.sendBroadcast(disconnectedMessage, Constants.UDP_SERVER_PORT);
            ConnectedUserList.getInstance().clear();
            LocalDatabase.Database.currentUser = null;
        } catch (NoBroadcastAddressFound e){
            LOGGER.severe(e.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


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
