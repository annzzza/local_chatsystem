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
    private final InetAddress serverAddress;
    private boolean running;
    private byte[] buffer = new byte[Constants.MAX_UDP_PACKET_SIZE];
    private int port = Constants.UDP_SERVER_PORT;

    private UDPServer() {
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
        MyLogger.getInstance().info(data);
        Message receivedMessage = new Gson().fromJson(data, Message.class);

        MyLogger.getInstance().info("Message received: \n" + new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(receivedMessage)
        );

        switch (receivedMessage.getType()) {
            case DISCOVERY -> {
                MyLogger.getInstance().info("Discovery message received.");
                ConnectedUser user = new ConnectedUser(receivedMessage.getSender(), address);
                NetworkManager.getInstance().notifyConnected(user);

                Message answer = new Message();
                answer.setType(Message.MessageType.USER_CONNECTED);
                answer.setDate(new Date());
                answer.setSender(LocalDatabase.Database.currentUser);

                UDPClient client = new UDPClient();
                String addressStr = String.valueOf(address);
                addressStr = addressStr.replace("/", "");


                Gson gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .create();

                buffer = (gson.toJson(answer)).getBytes();
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, Constants.UDP_SERVER_PORT);
                    serverSocket.send(packet);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                MyLogger.getInstance().info("Display connectedUserList:\n" + new GsonBuilder()
                            .setPrettyPrinting()
                            .create()
                            .toJson(LocalDatabase.Database.connectedUserList)
                );
            }
            case USER_CONNECTED -> {
                MyLogger.getInstance().info("User connected message received.");
                ConnectedUser user = new ConnectedUser(receivedMessage.getSender(), address);
                NetworkManager.getInstance().notifyConnected(user);
            }
            case TEXT_MESSAGE -> {

            }
            case USERNAME_CHANGED -> {
                //TODO call notifyChangeUsername
                MyLogger.getInstance().info("Username changed message received.");
                ConnectedUser user = new ConnectedUser(receivedMessage.getSender(), address);
                String newUsername = receivedMessage.getContent();
                NetworkManager.getInstance().notifyChangeUsername(user, newUsername);

            }
            case  USER_DISCONNECTED -> {
                MyLogger.getInstance().info("User disconnected message received.");
                ConnectedUser user = new ConnectedUser(receivedMessage.getSender(), address);
                NetworkManager.getInstance().notifyDisconnected(user);
            }
        }
    }

    public void run() {
        running = true;

        while (running) {
            buffer = new byte[Constants.MAX_UDP_PACKET_SIZE];
            receivedPacket = new DatagramPacket(buffer, buffer.length);
            try {
                serverSocket.receive(receivedPacket);
                InetAddress receivedAddress = receivedPacket.getAddress();
                int receivedPort = receivedPacket.getPort();
                String receivedString = new String(receivedPacket.getData(), 0, receivedPacket.getLength());

                MyLogger.getInstance().info(receivedAddress.toString());
                MyLogger.getInstance().info(serverAddress.toString());
                MyLogger.getInstance().info("Addresses are the same?" + receivedAddress.toString() +"?=" + serverAddress.toString()
                        + ":" + receivedAddress.toString().equals(serverAddress.toString()));
                if (!receivedAddress.toString().equals(serverAddress.toString())) {
                    MyLogger.getInstance().info("Process data");
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
