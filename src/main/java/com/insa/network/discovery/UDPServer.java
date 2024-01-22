package com.insa.network.discovery;

import java.io.IOException;
import java.net.*;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        void handleMessage(UDPMessage message);
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
     *
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
     *
     * @throws SocketException if an error occurs while creating the server socket (e.g. if the port is already used)
     */
    public UDPServer(int port) throws SocketException {
        serverSocket = new DatagramSocket(port);
        running = true;
        LOGGER.info("UDPServer created on " + serverSocket.getLocalAddress() + ":" + serverSocket.getLocalPort());
    }

    @Override
    public void run() {

        LOGGER.info("UDPServer started");
        while (running) {
            LOGGER.info("UDPServer running");
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
                    LOGGER.info("All current IP: " + getAllCurrentIp());
                    LOGGER.info("Message from server (" + receivedAddress + ")" + " itself ignored:\n" + receivedString);
                    continue;
                }

                // Deserialize the message
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                UDPMessage receivedMessage = gson.fromJson(receivedString, UDPMessage.class);
                receivedMessage.setSenderIP(receivedAddress);
                LOGGER.info("Message received: \n" + gson.toJson(receivedMessage));

                // Notify the observers
                synchronized (observerList) {
                    for (Observer observer : observerList) {
                        observer.handleMessage(receivedMessage);
                        LOGGER.info("Message sent to observer");
                    }
                }

            } catch (IOException e) {
                LOGGER.severe("Error while receiving UDP message: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        LOGGER.info("UDPServer stopped");
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
    public void close() {
        running = false;
    }
}
