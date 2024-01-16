package com.insa.network.tcp;

import com.google.gson.GsonBuilder;
import com.insa.utils.MyLogger;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to send TCP messages to a server
 **/
public class TCPClient {
    private static final MyLogger LOGGER = new MyLogger(TCPClient.class.getName());

    private Socket clientSocket;
    private PrintWriter out;

    /**
     * Observer interface for the TCP client
     */
    public interface TCPClientObserver {
        /**
         * Called when a new message is sent
         * @param message the message that was sent
         */
        void sendMessage(TCPMessage message);
    }

    private static final List<TCPClientObserver> observerList = new ArrayList<>();

    public void addObserver(TCPClientObserver observer) {
        synchronized (observerList) {
            LOGGER.info("Adding observer");
            observerList.add(observer);
        }
    }
    /**
     * Starts a connection with a server
     **/
    public void startConnection(String ip, int port) throws IOException {
        LOGGER.info("Starting connection with server: " + ip + ":" + port);
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    /**
     * Sends a message to the server and returns the response
     **/
    public void sendMessage(TCPMessage message) {
        var gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.S").create();

        LOGGER.info("Sending message: " + gson.toJson(message));
        //System.out.println(("[TEST] - Check that conversion is working: " + gson.fromJson(gson.toJson(message), TCPMessage.class)));
        out.println(gson.toJson(message));
        synchronized (observerList) {
            for (TCPClientObserver observer : observerList) {
                LOGGER.info("Notifying observer that message was sent: " + message);
                observer.sendMessage(message);
            }
        }
    }

    /**
     * Stops the connection with the server
     *
     * @throws IOException if an error occurs while closing the socket
     */
    public void stopConnection() throws IOException {
        clientSocket.close();
    }
}
