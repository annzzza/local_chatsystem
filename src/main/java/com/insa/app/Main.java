package com.insa.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.insa.network.Message;
import com.insa.network.UDPClient;
import com.insa.network.UDPServer;
import com.insa.network.User;
import com.insa.utils.Logger;

import java.net.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws UnknownHostException {

        Logger logger = Logger.getInstance();
        logger.log("Launching app");

        // Creation of messages
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Message msg = new Message(Message.MessageType.USER_CONNECTED, "coucou", new Date(), new User("Ronan"), new User("Anna"));
        Message msgout = new Message(Message.MessageType.USER_DISCONNECTED, "ciao", new Date(), new User("Ronan"), new User("Anna"));

        logger.log("Creating UDP Server and Client");
        UDPServer udpServer = new UDPServer();
        udpServer.start();

        UDPClient anna = new UDPClient();


        logger.log("Begin client discovery");
        anna.sendUDP(msg, 5555, "localhost");

        logger.log("Waiting for responses");

        logger.log("Discovery finished");

        logger.log("Client disconnect");
        anna.sendUDP(msgout, 5555, "localhost");

        anna.close();
    }
}
