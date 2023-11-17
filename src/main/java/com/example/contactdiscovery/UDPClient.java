package com.example.contactdiscovery;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class UDPClient {

    private DatagramSocket clientSocket;
    private InetAddress clientAddress;
    private byte[] buffer;
    private int port = 5555;

    public UDPClient() {
        try {
            clientSocket = new DatagramSocket();
            clientAddress = InetAddress.getByName("localhost");
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /**
    public String sendUsername(String username) {
        buffer = username.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientAddress, port);
        try {
            clientSocket.send(packet);
            buffer = new byte[256];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
            clientSocket.receive(responsePacket);
            return "Response:" + new String(responsePacket.getData(), 0, responsePacket.getLength());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
**/

    public void sendUDP(Message msg, int sendingPort, String sendingAddress) throws UnknownHostException {

        InetAddress address = InetAddress.getByName(sendingAddress);
        buffer = (new Gson().toJson(msg)).getBytes();
        try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, sendingPort);
            clientSocket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    List<InetAddress> listAllBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces
                = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            networkInterface.getInterfaceAddresses().stream()
                    .map(a -> a.getBroadcast())
                    .filter(Objects::nonNull)
                    .forEach(broadcastList::add);
        }
        return broadcastList;
    }
    public void sendBroadcast(Message msg, int sendingPort) throws IOException {

        List<InetAddress> groupBroadcast = listAllBroadcastAddresses();
        if (groupBroadcast.isEmpty()){
            throw new IllegalStateException("no broadcast address available");
        } else {
            byte[] buffer = msg.toString().getBytes();
            for (InetAddress address : groupBroadcast) {
                clientSocket.setBroadcast(true);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, sendingPort);
                clientSocket.send(packet);
            }
        }
    }

    public void close() { clientSocket.close();}

}
