package com.insa.network;

import java.io.IOException;
import java.net.*;
import java.util.Date;
import java.util.Enumeration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.insa.database.LocalDatabase;
import com.insa.utils.Constants;
import com.insa.utils.MyLogger;

public class UDPServer extends Thread {
    private static volatile UDPServer instance;
    private static DatagramSocket serverSocket;
    private DatagramPacket receivedPacket;
    private InetAddress serverAddress;
    private boolean running;
    private byte[] buffer = new byte[256];
    private int port = Constants.UDP_SERVER_PORT;

    private final int MAX_UDP_DATAGRAM_LENGTH = Constants.MAX_UDP_PACKET_SIZE; // ?

    public UDPServer() {
        try {
            serverSocket = new DatagramSocket(port);

            try(final DatagramSocket socket = new DatagramSocket()){
                try {
                    socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
                serverAddress = socket.getLocalAddress();
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public void dataProcessing(String data, InetAddress address, int port) {
        // Convert data to message
        Message receivedMessage = new Gson().fromJson(data, Message.class);

        MyLogger.info("Message received: \n" + new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(receivedMessage)
        );

        switch (receivedMessage.getType()) {
            case DISCOVERY -> {
                MyLogger.info("Discovery message received.");
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

                MyLogger.info("Display connectedUserList:\n" + new GsonBuilder()
                            .setPrettyPrinting()
                            .create()
                            .toJson(LocalDatabase.Database.connectedUserList)
                );
            }
            case USER_CONNECTED -> {
                MyLogger.info("User connected message received.");
                ConnectedUser user = new ConnectedUser(receivedMessage.getSender(), address);
                NetworkManager.getInstance().notifyConnected(user);
            }
            case TEXT_MESSAGE -> {

            }
            case USERNAME_CHANGED -> {
                //TODO call notifyChangeUsername
                MyLogger.info("Username changed message received.");
                ConnectedUser user = new ConnectedUser(receivedMessage.getSender(), address);
                String newUsername = receivedMessage.getContent();
                NetworkManager.getInstance().notifyChangeUsername(user, newUsername);

            }
            case  USER_DISCONNECTED -> {
                MyLogger.info("User disconnected message received.");
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

                MyLogger.info(receivedAddress.toString());
                MyLogger.info(serverAddress.toString());
                MyLogger.info("Addresses are the same?" + receivedAddress.toString() +"?=" + serverAddress.toString()
                        + ":" + receivedAddress.toString().equals(serverAddress.toString()));
                if (!receivedAddress.toString().equals(serverAddress.toString())) {
                    MyLogger.info("Process data");
                    dataProcessing(receivedString, receivedAddress, receivedPort);
                }
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
