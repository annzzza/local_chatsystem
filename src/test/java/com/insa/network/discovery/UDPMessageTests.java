package com.insa.network.discovery;

import com.insa.users.User;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.*;

public class UDPMessageTests {

    @Test
    void testUDPMessage() {
        // Test constructors
        UDPMessage msg = new UDPMessage();
        assertNotNull(msg);
        UDPMessage msg2 = new UDPMessage(UDPMessage.MessageType.DISCOVERY, "Hello", new User("sender"));
        assertNotNull(msg2);

        User sender = new User("sender");
        UDPMessage msg3 = new UDPMessage(UDPMessage.MessageType.DISCOVERY, "Hello", sender, InetAddress.getLoopbackAddress());
        assertNotNull(msg3);

        // Test setters
        msg.setType(UDPMessage.MessageType.DISCOVERY);
        msg.setContent("Hello");
        msg.setSender(sender);
        msg.setSenderIP(InetAddress.getLoopbackAddress());

        // Test getters
        assertEquals(UDPMessage.MessageType.DISCOVERY, msg.getType());
        assertEquals("Hello", msg.getContent());
        assertEquals(sender, msg.getSender());
        assertEquals(InetAddress.getLoopbackAddress(), msg.getSenderIP());

        // Test equals
        assertEquals(msg, msg3);
        assertNotEquals(msg, msg2);
        assertNotEquals(msg, null);
        assertNotEquals(msg, new Object());
        assertEquals(msg, msg);
    }
}
