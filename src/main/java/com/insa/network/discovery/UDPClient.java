package com.insa.network.discovery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.insa.utils.MyLogger;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class UDPClient {
    private static final MyLogger LOGGER = new MyLogger(UDPClient.class.getName());

    private final DatagramSocket clientSocket;

    /**
     * No-arg constructor
     */
    public UDPClient() {
        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * send UDP Message
     *
     * @param msg            Message to be sent
     * @param sendingPort    int
     * @param sendingAddress InetAddress, destinator address
     * @throws UnknownHostException if the address is not valid
     */
    public void sendUDP(UDPMessage msg, int sendingPort, String sendingAddress) throws UnknownHostException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        InetAddress address = InetAddress.getByName(sendingAddress);
        byte[] buffer = (gson.toJson(msg)).getBytes();

        try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, sendingPort);
            clientSocket.send(packet);
            LOGGER.info("UDP message sent to " + sendingAddress + ":" + sendingPort + ":\n" + gson.toJson(msg));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @return List of all Broadcast InetAddresses of UDPClient machine
     * @throws SocketException if the socket is not valid
     */
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
                    .map(InterfaceAddress::getBroadcast)
                    .filter(Objects::nonNull)
                    .forEach(broadcastList::add);
        }
        return broadcastList;
    }

    /**
     * send UDP Broadcast to each Broadcast address listed by listAllBroadcastAddresses()
     *
     * @param msg         Message to be sent
     * @param sendingPort int
     * @throws IOException if the socket is not valid
     */
    public void sendBroadcast(UDPMessage msg, int sendingPort) throws IOException, NoBroadcastAddressFound {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        List<InetAddress> groupBroadcast = listAllBroadcastAddresses();

        if (groupBroadcast.isEmpty()) throw new NoBroadcastAddressFound();
        else {
            byte[] buffer = gson.toJson(msg).getBytes();
            for (InetAddress address : groupBroadcast) {
                clientSocket.setBroadcast(true);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, sendingPort);
                clientSocket.send(packet);
            }
        }
    }

    /**
     * Close the socket
     */
    public void close() {
        clientSocket.close();
    }

}
