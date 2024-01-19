package com.insa.network.discovery;

import org.junit.jupiter.api.Test;

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

}
