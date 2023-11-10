package com.example.contactdiscovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UDPServer extends Thread {
    private static volatile UDPServer instance;
    private static DatagramSocket socket;
    private boolean running;
    private byte[] buffer = new byte[256];
    private int port = 5555;
    private ContactList contactList = new ContactList();

    public UDPServer() {
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        running = true;

        while (running) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            String received = new String(packet.getData(), 0, packet.getLength());


            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Message msg = gson.fromJson(received, Message.class);

            String msgToClient = "";
            //according to msg type, different behaviour
            switch(msg.getType()) {

                case USERNAMECHANGE:
                    if (contactList.contains(msg.getContent())) {
                        msgToClient = "This username: " + msg.getContent() + " is already taken. Please choose another username.";
                    } else {
                        msgToClient = "Welcome " + msg.getContent();
                        //todo: update username in ContactList to new username for the given password and IPaddress
                        //contactList.add(msg);
                    }
                    break;

                case DISCOVERYSEND:
                    if (contactList.contains(msg.getContent())) {
                        msgToClient = "This username: " + msg.getContent() + " is already taken. Please choose another username.";
                    } else {
                        msgToClient = "Welcome " + msg.getContent();
                        //todo: update username in ContactList to new username for the given password and IPaddress
                        //contactList.add(msg);
                    }
                    break;

                case DISCOVERYACK:
                    break;
            }



            //todo : understand which msg case the following code belongs to
            if (contactList.contains(msg.getContent())) {
                msgToClient = "You should not pass";
            } else {
                msgToClient = "Welcome";
                //contactList.add(msg);
            }

            buffer = msgToClient.getBytes();
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length, address, packet.getPort());
            try {
                socket.send(responsePacket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println(contactList);
        }
        socket.close();
    }

    public static void close() {
        socket.close();
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
