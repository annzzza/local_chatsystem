package com.insa.network;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.io.PrintWriter;

public class TCPServerTest {

    private static final int TEST_PORT = 1234;

    @Test
    void testTCPServer() throws Exception {

        List<TCPMessage> receivedMessages = new ArrayList<>();
        TCPServer server = new TCPServer(TEST_PORT);
        server.addObserver(receivedMessages::add);
        server.start();

        // Make client from scratch to send a message
        Socket clientSocket = new Socket("127.0.0.1", TEST_PORT);
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        // Send a message
        TCPMessage message = new TCPMessage("Hello", new User("sender"), new User("receiver"), null);
        out.println(new Gson().toJson(message));

        // Check that the message has been received
        Thread.sleep(1000);
        System.out.println(receivedMessages);
        assert(receivedMessages.size() == 1);
        assert(receivedMessages.get(0).content().equals("Hello"));
        assert(receivedMessages.get(0).sender().getUsername().equals("sender"));
        assert(receivedMessages.get(0).receiver().getUsername().equals("receiver"));

        // Close everything
        clientSocket.close();
        server.interrupt();


    }
}
