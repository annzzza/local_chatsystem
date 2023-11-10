package com.example.contactdiscovery;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class Main {
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
