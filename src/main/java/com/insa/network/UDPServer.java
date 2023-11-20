package com.insa.network;

import java.io.IOException;
import java.net.*;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.insa.database.LocalDatabase;
import com.insa.utils.Constants;
import com.insa.utils.Logger;

public class UDPServer extends Thread {
    private static volatile UDPServer instance;
    private static DatagramSocket serverSocket;
    private DatagramPacket receivedPacket;
    private boolean running;
    private byte[] buffer = new byte[256];
    private int port = Constants.UDP_SERVER_PORT;

    private final int MAX_UDP_DATAGRAM_LENGTH = Constants.MAX_UDP_PACKET_SIZE; // ?

    public UDPServer() {
        try {
            serverSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public void dataProcessing(String data, InetAddress address, int port) {
        // Convert data to message
        GsonBuilder builder = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .setPrettyPrinting();
        Gson gson = builder.create();

        Logger.getInstance().log(data);

        Message receivedMessage = new Gson().fromJson(data, Message.class);

        switch (receivedMessage.getType()) {
            case DISCOVERY -> {
                Logger.getInstance().log("Discovery message received.");
                ConnectedUser user = new ConnectedUser(receivedMessage.getSender(), address);
                NetworkManager.getInstance().notifyConnected(user);

                Message answer = new Message();
                answer.setType(Message.MessageType.USER_CONNECTED);
                answer.setDate(new Date());
                answer.setSender(LocalDatabase.Database.currentUser);

                UDPClient client = new UDPClient();
                try {
                    String addressStr = String.valueOf(address);
                    addressStr = addressStr.replace("/", "");

                    client.sendUDP(answer, Constants.UDP_SERVER_PORT, String.valueOf(addressStr));
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }

                Logger.getInstance().log("contactList:\n" + new GsonBuilder()
                        .setPrettyPrinting()
                        .create()
                        .toJson(LocalDatabase.Database.connectedUserList));
            }
            case USER_CONNECTED -> {
                Logger.getInstance().log("User connected message received.");
                ConnectedUser user = new ConnectedUser(receivedMessage.getSender(), address);
                NetworkManager.getInstance().notifyConnected(user);
            }
            case TEXT_MESSAGE -> {

            }
            case USERNAME_CHANGED -> {

            }
            case  USER_DISCONNECTED -> {
                Logger.getInstance().log("User disconnected message received.");
                ConnectedUser user = new ConnectedUser(receivedMessage.getSender(), address);
                NetworkManager.getInstance().notifyDisconnected(user);
            }
        }
    }

    public void run() {
        running = true;

        while (running) {
            receivedPacket = new DatagramPacket(buffer, buffer.length);

            try {
                serverSocket.receive(receivedPacket);
                InetAddress receivedAddress = receivedPacket.getAddress();
                int receivedPort = receivedPacket.getPort();
                String receivedString = new String(receivedPacket.getData(), 0, receivedPacket.getLength());

                dataProcessing(receivedString, receivedAddress, receivedPort);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        serverSocket.close();
    }

    public static void close() {
        serverSocket.close();
    }

    public static UDPServer getInstance() {
        UDPServer result = instance;
        if (result != null) {
            return result;
        }
        synchronized (UDPServer.class) {
            if(instance == null) {
                instance = new UDPServer();
            }
            return instance;
        }
    }
}
