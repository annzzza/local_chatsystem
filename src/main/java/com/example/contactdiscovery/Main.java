package com.example.contactdiscovery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws UnknownHostException {


        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Message msg = new Message(Message.MessageType.USER_CONNECTED, "coucou", new Date(), new User("Ronan"), new User("Anna"));
        Message msgout = new Message(Message.MessageType.USER_DISCONNECTED, "ciao", new Date(), new User("Ronan"), new User("Anna"));

        System.out.println((gson.toJson(msg)));
        UDPServer udpServer = new UDPServer();
        udpServer.start();
        /**
        System.out.println(anna.sendUsername("Anna"));
        System.out.println(ronan.sendUsername("Ronan"));
        System.out.println(anna.sendUsername("Ronan"));
**/

        UDPClient anna = new UDPClient();
        UDPClient ronan = new UDPClient();

        System.out.println(gson.toJson(msg));

        anna.sendUDP(msg, 5555, "localhost");
        anna.sendUDP(msgout, 5555, "localhost");

        anna.close();
        ronan.close();
    }
}
