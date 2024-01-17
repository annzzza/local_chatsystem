package com.insa.database;

import com.insa.users.ConnectedUser;
import com.insa.network.tcp.TCPMessage;
import com.insa.users.User;
import com.insa.utils.MyLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class HistoryDAOTest {

    private static final MyLogger LOGGER = new MyLogger(HistoryDAOTest.class.getName());


    HistoryDAO jdao = new HistoryDAO();
    static Connection con;

    ConnectedUser user1;
    ConnectedUser user2;

    ConnectedUser self;

    public static boolean doesTableExist(Connection connection, String tableName) throws SQLException {
        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, tableName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // If the result set has at least one row, the table exists
            }
        }
    }

    @BeforeAll
    static void setUpPrimary() throws SQLException {
        con = Database.getDBConnection();
        if (!(doesTableExist(con, "message_history"))) {
            Database.createTables(con);
        }
    }

    @BeforeEach
    void setUp() throws UnknownHostException {

        con = Database.getDBConnection();
        if (con != null) {
            user1 = new ConnectedUser("annatest", (InetAddress.getByName("192.168.69.69")));
            user2 = new ConnectedUser("ronantest", InetAddress.getByName("192.168.42.42"));
            self = new ConnectedUser("self", InetAddress.getByName("localhost"));
        }
    }

    @Test
    void addToHistoryDB() throws DAOException, SQLException {
        //Create a TCP Message
        TCPMessage msg = new TCPMessage(UUID.randomUUID(), "ceci est un message", user1, user2, new Timestamp(24859L));
        //Add it to the History table
        jdao.addToHistoryDB(msg);
        LOGGER.info("[TEST] - Message to add in DB : " + msg.content() + " from: " + msg.sender().getUsername() + " to: " + msg.receiver().getUsername());


        //Check that it has been successfully added
        String query = "SELECT * FROM message_history WHERE uuid=?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, msg.uuid().toString());
        ResultSet rs = ps.executeQuery();
        assertTrue(rs.next());

        assertEquals(msg.uuid().toString(), rs.getString("uuid"));
        assertEquals(msg.content(), rs.getString("content"));
        assertEquals(msg.date(), rs.getTimestamp("date"));
        assertEquals(msg.sender().getUsername(), rs.getString("sender_username"));
        String uuidMSG = rs.getString("uuid");

        LOGGER.info("[TEST] - Added message to DB: " + rs.getString("content") + " from: " + rs.getString("sender_username") + " to: " + rs.getString("receiver_username"));
        rs.close();
        ps.close();

        //Delete added message from DB
        String delQuery = "DELETE FROM message_history WHERE uuid=?";
        PreparedStatement psDel = con.prepareStatement(delQuery);
        psDel.setString(1, uuidMSG);
        psDel.executeUpdate();
        psDel.close();
    }

    @Test
    void deleteFromHistory() throws DAOException, SQLException {
        //Create a TCP Message
        TCPMessage msg = new TCPMessage(UUID.randomUUID(), "ceci est un message", user1, user2, new Timestamp(24859L));
        //Add it to the History table
        jdao.addToHistoryDB(msg);
        //Delete it from history table
        jdao.deleteFromHistory(msg);
        //check that it has been deleted
        String query = "SELECT * FROM message_history WHERE uuid=?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, msg.uuid().toString());
        ResultSet rs = ps.executeQuery();
        assertFalse(rs.next());
    }


    @Test
    void updateHistoryDB() throws DAOException, SQLException {
        if (con != null) {
            //create two messages from user1
            TCPMessage msg1 = new TCPMessage(UUID.randomUUID(), "ceci est un message", user1, user2, new Timestamp(24859L));
            TCPMessage msg2 = new TCPMessage(UUID.randomUUID(), "ceci est un message 2", user2, user1, new Timestamp(24859L));
            TCPMessage msg3 = new TCPMessage(UUID.randomUUID(), "ceci est un message 3", user1, user2, new Timestamp(24859L));

            //add messages to history
            jdao.addToHistoryDB(msg1);
            jdao.addToHistoryDB(msg2);
            jdao.addToHistoryDB(msg3);

            //update a username
            String newUsername = "NOUVEAU";
            jdao.updateHistoryDB(user1, newUsername);

            LOGGER.info("[TEST] - User " + user1.getUsername() + " has new username:\n" + newUsername);

            //check that history db has been updated
            String query1 = "SELECT * FROM message_history WHERE sender_username = ?";
            PreparedStatement ps1 = con.prepareStatement(query1);
            ps1.setString(1, newUsername);
            ResultSet rs1 = ps1.executeQuery();
            ArrayList<String> senderIsUpdated = new ArrayList<>();
            while (rs1.next()) {
                senderIsUpdated.add(rs1.getString("content"));

                LOGGER.info("[TEST] - History has been updated for " + user1.getUsername() + " with new username:\n" + rs1.getString("sender_username"));
            }
            rs1.close();
            ps1.close();

            String query2 = "SELECT * FROM message_history WHERE receiver_username = ?";
            PreparedStatement ps2 = con.prepareStatement(query2);
            ps2.setString(1, newUsername);
            ResultSet rs2 = ps2.executeQuery();
            ArrayList<String> receiverIsUpdated = new ArrayList<>();
            while (rs2.next()) {
                receiverIsUpdated.add(rs2.getString("content"));

                LOGGER.info("[TEST] - History has been updated for " + user1.getUsername() + " with new username:\n" + rs2.getString("receiver_username"));
            }
            rs2.close();
            ps2.close();

            assertEquals(2, senderIsUpdated.size());
            assertEquals(1, receiverIsUpdated.size());
            assertEquals("ceci est un message", senderIsUpdated.get(0));
            assertEquals("ceci est un message 3", senderIsUpdated.get(1));
            assertEquals("ceci est un message 2", receiverIsUpdated.get(0));


            //delete messages from history
            jdao.deleteFromHistory(msg1);
            jdao.deleteFromHistory(msg2);
            jdao.deleteFromHistory(msg3);
        }
    }

    @Test
    void getHistoryWith() throws DAOException, SQLException {
        if (con != null) {
            //create two messages from user1
            TCPMessage msg1 = new TCPMessage(UUID.randomUUID(), "ceci est un message", user1, user2, new Timestamp(24859L));
            TCPMessage msg2 = new TCPMessage(UUID.randomUUID(), "ceci est un message 2", user2, user1, new Timestamp(24859L));
            TCPMessage msg3 = new TCPMessage(UUID.randomUUID(), "ceci n'est pas un message voulu", new User("spy"), user2, new Timestamp(24859L));

            //add messages to history
            jdao.addToHistoryDB(msg1);
            jdao.addToHistoryDB(msg2);
            jdao.addToHistoryDB(msg3);

            LOGGER.info("[TEST] - Added message in history:  sent by: " + msg1.sender().getUsername() + "  content:" + msg1.content());
            LOGGER.info("[TEST] - Added message in history:  sent by: " + msg2.sender().getUsername() + "  content:" + msg2.content());
            LOGGER.info("[TEST] - Added message in history:  sent by: " + msg3.sender().getUsername() + "  content:" + msg3.content());


            //check that messages are in history
            String query = "SELECT * FROM message_history WHERE uuid=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, msg1.uuid().toString());
            ResultSet rs1 = ps.executeQuery();
            assertTrue(rs1.next());
            assertEquals(msg1.content(), rs1.getString("content"));
            String msg1UUID = rs1.getString("uuid");
            rs1.close();

            ps.setString(1, msg2.uuid().toString());
            ResultSet rs2 = ps.executeQuery();
            assertTrue(rs2.next());
            assertEquals(msg2.content(), rs2.getString("content"));
            String msg2UUID = rs2.getString("uuid");
            rs2.close();

            ps.setString(1, msg3.uuid().toString());
            ResultSet rs3 = ps.executeQuery();
            assertTrue(rs3.next());
            assertEquals(msg3.content(), rs3.getString("content"));
            String msg3UUID = rs3.getString("uuid");
            rs3.close();

            ps.close();

            //retreive messages from history
            ArrayList<TCPMessage> hist = jdao.getHistoryWith(user1, user2);
            //test that the message list corresponds
            assertEquals(2, hist.size());
            for (TCPMessage message : hist) {
                assertTrue(message.equals(msg1) || message.equals(msg2));
            }


            //delete inserted messages from history
            String delQuery = "DELETE FROM message_history WHERE uuid=?";
            PreparedStatement psDel = con.prepareStatement(delQuery);
            //del msg1
            psDel.setString(1, msg1UUID);
            psDel.executeUpdate();
            //del msg2
            psDel.setString(1, msg2UUID);
            psDel.executeUpdate();
            //del msg3
            psDel.setString(1, msg3UUID);
            psDel.executeUpdate();
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (con != null) {
            con.close();
        }
    }
}