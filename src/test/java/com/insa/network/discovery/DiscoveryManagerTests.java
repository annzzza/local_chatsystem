package com.insa.network.discovery;

import com.insa.users.ConnectedUser;
import com.insa.users.ConnectedUserAlreadyExists;
import com.insa.users.ConnectedUserList;
import com.insa.users.User;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

public class DiscoveryManagerTests {

    /**
     * Test of singleton
     */
    @Test
    void testSingleton() {
        DiscoveryManager manager1 = DiscoveryManager.getInstance();
        DiscoveryManager manager2 = DiscoveryManager.getInstance();
        assertEquals(manager1, manager2);
    }

    /**
     * Test of discoverNetwork method, of class DiscoveryManager.
     */
    @Test
    void testDiscoverNetwork() throws UsernameAlreadyTakenException {
        DiscoveryManager manager = DiscoveryManager.getInstance();
        manager.discoverNetwork("test");
        assertNotNull(manager.getCurrentUser());
        assertEquals("test", manager.getCurrentUser().getUsername());
    }

    /**
     * Test of sendChangeUsername method, of class DiscoveryManager.
     */
    @Test
    void testSendChangeUsername() throws UsernameAlreadyTakenException, UnknownHostException, ConnectedUserAlreadyExists {
        DiscoveryManager manager = DiscoveryManager.getInstance();
        manager.discoverNetwork("test");
        manager.sendChangeUsername(new User("test"), "test2");
        assertEquals("test2", manager.getCurrentUser().getUsername());

        // Test with an already taken username
        ConnectedUserList.getInstance().addConnectedUser(new ConnectedUser("userConnected", InetAddress.getLocalHost()));
        assertThrows(UsernameAlreadyTakenException.class, () -> manager.sendChangeUsername(new User("test3"), "userConnected"));
    }

    /**
     * Test of sendDisconnection method, of class DiscoveryManager.
     */
    @Test
    void testSendDisconnection() throws UsernameAlreadyTakenException {
        DiscoveryManager manager = DiscoveryManager.getInstance();
        manager.discoverNetwork("test");
        manager.sendDisconnection(new User("test"));
        assertNull(manager.getCurrentUser());
    }
}
