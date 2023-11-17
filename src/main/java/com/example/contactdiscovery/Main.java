package com.example.contactdiscovery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {


//        UDPServer server = UDPServer.getInstance();
//        server.start();
//
//        UDPClient anna = new UDPClient();
//        UDPClient ronan = new UDPClient();
//
//        System.out.println(anna.sendUsername("Anna"));
//        System.out.println(ronan.sendUsername("Ronan"));
//        System.out.println(anna.sendUsername("Ronan"));
//
//
//        anna.close();
//        ronan.close();

        Gson g = new GsonBuilder().setPrettyPrinting().create();

        Message msg = new Message(Message.MessageType.USER_CONNECTED, "coucou", new Date(), new User("Ronan"), new User("Anna"));
        System.out.println((g.toJson(msg)));
        UDPServer udpServer = new UDPServer();
        udpServer.start();

    }
}
