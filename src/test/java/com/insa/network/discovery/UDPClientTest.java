package com.insa.network.discovery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.insa.users.User;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class UDPClientTest {

    DatagramSocket testSocket;
    User testUser;
    @Test
    void sendUDP() throws IOException {

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        testSocket= new DatagramSocket(2235);

        //initialize testmessage
        testUser = new User("test");
        UDPMessage testMessage = new UDPMessage();
        testMessage.setSender(testUser);
        testMessage.setType(UDPMessage.MessageType.USER_CONNECTED);

        UDPClient udpClient = new UDPClient();
        udpClient.sendUDP(testMessage, 2235, "localhost"); //my machine's address -> to be changed


        //in testSocket, reception of the sent message
        byte[] buffer = new byte[1024];
        DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

        testSocket.receive(receivedPacket);

        String receivedMessageString = new String(receivedPacket.getData(),0, receivedPacket.getLength(), StandardCharsets.UTF_8);

        //compare String formatted received message with sent message
        assertEquals(gson.toJson(testMessage), receivedMessageString, "not good");
        testSocket.close();
    }

    @Test
    void sendBroadcast() {

    }
}