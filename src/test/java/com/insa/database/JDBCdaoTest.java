package com.insa.database;

import com.insa.network.ConnectedUser;
import com.insa.network.TCPMessage;
import com.insa.network.User;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JDBCdaoTest {

    ConnectedUser user1;
    User user2 = new User("ronantest");

    {
        try {
            user1 = new ConnectedUser("annatest", InetAddress.getByName("0.0.0.69"));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void addToHistoryDB() {
        TCPMessage msg = new TCPMessage(UUID.randomUUID(), "ceci est un message", user1, user2, new Timestamp(24859L));
        //TO FINISH
    }

    @Test
    void addToConnectedUserDB() {
    }

    @Test
    void getHistoryWith() {
    }

    @Test
    void getConnectedUserList() {
    }
}