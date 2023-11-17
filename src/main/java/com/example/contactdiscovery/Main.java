package com.example.contactdiscovery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {


        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Message msg = new Message(Message.MessageType.USER_CONNECTED, "coucou", new Date(), new User("Ronan"), new User("Anna"));
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

        Message msg1 = new Message(Message.MessageType.USER_CONNECTED, "coucou");
        System.out.println(gson.toJson(msg));

        anna.sendUDP(msg1, 5554, "localhost");

        anna.close();
        ronan.close();
    }
}
