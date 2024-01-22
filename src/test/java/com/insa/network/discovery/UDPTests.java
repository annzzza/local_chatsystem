package com.insa.network.discovery;

import com.insa.utils.MyLogger;
import org.junit.jupiter.api.Test;
import com.insa.users.User;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UDPTests {

    private static final MyLogger LOGGER = new MyLogger(UDPTests.class.getName());

    private static final int TEST_PORT = 12345;

    @Test
    void sendReceiveTest() throws IOException, InterruptedException, NoBroadcastAddressFound {
        // Create test messages
        // Address given so that we can test equality of arrays
        List<UDPMessage> testMessages = Arrays.asList(
                new UDPMessage(UDPMessage.MessageType.DISCOVERY, "", new User("ronan"), InetAddress.getLoopbackAddress()),
                new UDPMessage(UDPMessage.MessageType.DISCOVERY, "", new User("anna"), InetAddress.getLoopbackAddress()),
                new UDPMessage(UDPMessage.MessageType.DISCOVERY, "", new User("multi\nline\nmessage"), InetAddress.getLoopbackAddress()),
                new UDPMessage(UDPMessage.MessageType.DISCOVERY, "", new User(""), InetAddress.getLoopbackAddress()),
                new UDPMessage(UDPMessage.MessageType.DISCOVERY, "", new User("привет"), InetAddress.getLoopbackAddress())
        );
        LOGGER.info("[TEST] Test messages: " + testMessages);

        List<UDPMessage> receivedMessages = new ArrayList<>();

        LOGGER.info("[TEST] Creating server");
        UDPServer server = new UDPServer(TEST_PORT);
        LOGGER.info("[TEST] Server created");

        LOGGER.info("[TEST] Adding observer");
        server.addObserver(receivedMessages::add);
        LOGGER.info("[TEST] Starting server");
        server.start();
        LOGGER.info("[TEST] Server started");


        LOGGER.info("[TEST] Sending messages");
        UDPClient client = new UDPClient();
        for (UDPMessage msg : testMessages) {
            client.sendUDP(msg, TEST_PORT, "0.0.0.0");
            client.sendBroadcast(msg, TEST_PORT);
        }
        LOGGER.info("[TEST] Messages sent");

        Thread.sleep(100);

        LOGGER.info("[TEST] Received messages:" + receivedMessages);

        LOGGER.info("[TEST] Test that all messages have been received correctly");
        assertEquals(testMessages.size(), receivedMessages.size());
        assertEquals(testMessages, receivedMessages);

        client.close();
        server.close();
    }

}
