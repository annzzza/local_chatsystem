package com.example.contactdiscovery;

import org.javatuples.Pair;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class Main {
    public static class UDPServer extends Thread{
        private static volatile UDPServer instance;
        private static DatagramSocket socket;
        private boolean running;
        private byte[] buffer = new byte[256];
        private int port = 5555;
        private Map<String, InetAddress> unnomcool = new HashMap<String, InetAddress>();

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

                String msgToClient = "";
                if (unnomcool.containsKey(received)) {
                    msgToClient = "You should not pass";
                } else {
                    msgToClient = "Welcome";
                    unnomcool.put(received, address);
                }

                buffer = msgToClient.getBytes();
                DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length, address, packet.getPort());
                try {
                    socket.send(responsePacket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                System.out.println(unnomcool);
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

    public static class UDPClient {
        private DatagramSocket socket;
        private InetAddress address;
        private byte[] buffer;
        private int port = 5555;

        public UDPClient() {
            try {
                socket = new DatagramSocket();
                address = InetAddress.getByName("localhost");
            } catch (SocketException | UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }

        public String sendUsername(String username) {
            buffer = username.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
            try {
                socket.send(packet);
                buffer = new byte[256];
                DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(responsePacket);
                return "Response:" + new String(responsePacket.getData(), 0, responsePacket.getLength());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void close() { socket.close();}
    }
    public static void main(String[] args) {
        UDPServer server = UDPServer.getInstance();
        server.start();

        UDPClient anna = new UDPClient();
        UDPClient ronan = new UDPClient();

        System.out.println(anna.sendUsername("Anna"));
        System.out.println(ronan.sendUsername("Ronan"));
        System.out.println(anna.sendUsername("Ronan"));

        anna.close();
        ronan.close();
    }
}
