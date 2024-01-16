package com.insa.network.discovery;

import java.io.IOException;
import java.net.*;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.insa.database.LocalDatabase;
import com.insa.users.ConnectedUser;
import com.insa.utils.Constants;
import com.insa.utils.MyLogger;

/**
 * This class is used to receive UDP messages from other clients
 */
public class UDPServer extends Thread {

    private static final MyLogger LOGGER = new MyLogger(UDPServer.class.getName());


    /**
     * Observer interface for the UDP server
     * This interface is used to notify the observers of the server when a new user connects, changes their username or disconnects
     */
    public interface Observer {
        /**
         * Called when a new user connects
         * @param user The user that connected
         */
        void onNewUserConnected(ConnectedUser user);

        /**
         * Called when a user answers to a discovery message
         * @param user The user that answered
         */
        void onAnswerReceived(ConnectedUser user);

        /**
         * Called when a user changes their username
         * @param user The user that changed their username
         * @param newUsername The new username of the user
         */
        void onUsernameChanged(ConnectedUser user, String newUsername);

        /**
         * Called when a user disconnects
         * @param user The user that disconnected
         */
        void onUserDisconnected(ConnectedUser user);
    }

    /**
     * Server socket used to receive UDP messages
     */
    private static DatagramSocket serverSocket;

    /**
     * Boolean used to stop the server
     */
    private static boolean running = true;

    /**
     * List of observers of the server
     */
    private final List<Observer> observerList = new ArrayList<>();


    /**
     * Adds a new observer to the class
     * @param observer The observer to add
     */
    public void addObserver(Observer observer) {
        synchronized (observerList) {
            LOGGER.info("Adding observer to UDPServer");
            observerList.add(observer);
        }
    }

    /**
     * No-arg constructor
     * @throws SocketException if an error occurs while creating the server socket (e.g. if the port is already used)
     */
    public UDPServer(int port) throws SocketException {
        serverSocket = new DatagramSocket(port);
    }


    /**
     * Processes the data received by the server
     * @param data Raw data received
     * @param address Address of the sender
     */
    public void dataProcessing(String data, InetAddress address) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        UDPMessage receivedMessage = gson.fromJson(data, UDPMessage.class);
        LOGGER.info("Message received: \n" + gson.toJson(receivedMessage));

        switch (receivedMessage.getType()) {
            case DISCOVERY -> {
                LOGGER.info("Discovery message received.");

                // New user connected
                ConnectedUser user = new ConnectedUser(receivedMessage.getSender(), address);
                synchronized (observerList) {
                    for (Observer observer : observerList) {
                        LOGGER.info("Notifying observer that new user connected: " + user);
                        observer.onNewUserConnected(user);
                    }
                }

                // Send back that we are connected
                try {
                    UDPMessage answer = new UDPMessage(UDPMessage.MessageType.USER_CONNECTED,"" , LocalDatabase.Database.currentUser);
                    byte[] buffer = (gson.toJson(answer)).getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, Constants.UDP_SERVER_PORT);
                    serverSocket.send(packet);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case USER_CONNECTED -> {
                LOGGER.info("User connected message received.");
                ConnectedUser user = new ConnectedUser(receivedMessage.getSender(), address);
                synchronized (observerList) {
                    for (Observer observer : observerList) {
                        LOGGER.info("Notifying observer that new user connected: " + user);
                        observer.onAnswerReceived(user);
                    }
                }
            }
            case USERNAME_CHANGED -> {
                LOGGER.info("Username changed message received.");
                ConnectedUser user = new ConnectedUser(receivedMessage.getSender(), address);
                synchronized (observerList) {
                    for (Observer observer : observerList) {
                        LOGGER.info("Notifying observer that username changed: " + user + " to " + receivedMessage.getContent());
                        observer.onUsernameChanged(user, receivedMessage.getContent());
                    }
                }
            }
            case USER_DISCONNECTED -> {
                LOGGER.info("User disconnected message received.");
                ConnectedUser user = new ConnectedUser(receivedMessage.getSender(), address);
                synchronized (observerList) {
                    for (Observer observer : observerList) {
                        LOGGER.info("Notifying observer that user disconnected: " + user);
                        observer.onUserDisconnected(user);
                    }
                }
            }
        }
    }

    @Override
    public void run() {

        while (running) {
            try {
                byte[] buffer = new byte[Constants.MAX_UDP_PACKET_SIZE];
                DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

                // Wait for the next message
                serverSocket.receive(receivedPacket);

                // Get the message and network properties
                InetAddress receivedAddress = receivedPacket.getAddress();
                String receivedString = new String(receivedPacket.getData(), 0, receivedPacket.getLength());

                // Check if the message is not from the server itself
                if (Objects.requireNonNull(getAllCurrentIp()).contains(receivedAddress)) {
                    LOGGER.info("Message from server itself ignored.");
                    continue;
                }

                dataProcessing(receivedString, receivedAddress);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        serverSocket.close();
    }

    /**
     * @return List of all InetAddresses of UDPClient machine
     */
    public static ArrayList<InetAddress> getAllCurrentIp() {
        try {
            ArrayList<InetAddress> AllCurrentIp = new ArrayList<>();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                Enumeration<InetAddress> nias = ni.getInetAddresses();
                while (nias.hasMoreElements()) {
                    InetAddress ia = nias.nextElement();
                    if (!ia.isLinkLocalAddress() && !ia.isLoopbackAddress() && ia instanceof Inet4Address) {
                        AllCurrentIp.add(ia);
                    }
                }
            }
            return AllCurrentIp;
        } catch (SocketException e) {
            LOGGER.severe("Unable to get current IPs " + e.getMessage());
        }
        return null;
    }

    /**
     * Closes the server socket
     */
    public static void close() {
        running = false;
    }
}
