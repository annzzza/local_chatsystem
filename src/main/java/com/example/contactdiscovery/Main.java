package com.example.contactdiscovery;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws UnknownHostException {


        //UDPServer server = UDPServer.getInstance();
        //server.start();

        UDPClient anna = new UDPClient();
        UDPClient ronan = new UDPClient();

        /**
        System.out.println(anna.sendUsername("Anna"));
        System.out.println(ronan.sendUsername("Ronan"));
        System.out.println(anna.sendUsername("Ronan"));
**/
        Message msg = new Message(Message.MessageType.USER_CONNECTED, "coucou");
        System.out.println(msg.toString());
        System.out.println(new Gson().toJson(msg));

        anna.sendUDP(msg, 5554, "localhost");

        anna.close();
        ronan.close();
    }
}
