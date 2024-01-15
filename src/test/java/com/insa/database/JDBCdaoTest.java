package com.insa.database;

import com.insa.network.connectedusers.ConnectedUser;
import com.insa.network.TCPMessage;
import com.insa.network.User;
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

class JDBCdaoTest {

    JDBCdao jdao = new JDBCdao();
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
        con = JDBC.getDBConnection();
        if (!doesTableExist(con, "connected_users") && !(doesTableExist(con, "message_history"))){
            JDBC.createTables(con);
        }
    }

    @BeforeEach
    void setUp() throws UnknownHostException {

        con = JDBC.getDBConnection();
        if (con != null){
            user1 = new ConnectedUser("annatest", (InetAddress.getByName("192.168.69.69")));
            user2 = new ConnectedUser("ronantest", InetAddress.getByName("192.168.42.42"));
            self = new ConnectedUser("self", InetAddress.getByName("localhost"));
        }
    }

    @Test
    void addToHistoryDB() throws SQLException{
        //Create a TCP Message
        TCPMessage msg = new TCPMessage(UUID.randomUUID(), "ceci est un message", user1, user2, new Timestamp(24859L));
        //Add it to the History table
        jdao.addToHistoryDB(msg);
        MyLogger.getInstance().info("[TEST] - Message to add in DB : " + msg.content() + " from: " + msg.sender().getUsername() + " to: " + msg.receiver().getUsername());


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

        MyLogger.getInstance().info("[TEST] - Added message to DB: " + rs.getString("content") + " from: " + rs.getString("sender_username") + " to: " + rs.getString("receiver_username"));
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
    void deleteFromHistory() throws SQLException {
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
    void addToConnectedUserDB() throws SQLException {
        if (con!= null){
            //add user to connectedUser table
            jdao.addToConnectedUserDB(user1);

            MyLogger.getInstance().info("[TEST] - Connected user to add in DB: " + user1.getUsername());


            //check that user has been successfully added
            String query = "SELECT * FROM connected_users WHERE uuid=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, user1.getUuid().toString());
            ResultSet rs = ps.executeQuery();
            assertTrue(rs.next());
            assertEquals(user1.getUuid().toString(), rs.getString("uuid"));
            assertEquals(user1.getUsername(), rs.getString("username"));
            assertEquals(user1.getIP().toString().substring(1), rs.getString("ip"));
            String uuidUser1 = rs.getString("uuid");

            MyLogger.getInstance().info("[TEST] - Connected user added to DB: " + rs.getString("username"));
            rs.close();


            //delete added user from DB
            String delQuery = "DELETE FROM connected_users WHERE uuid=?";
            PreparedStatement psDel = con.prepareStatement(delQuery);
            psDel.setString(1, uuidUser1);
            psDel.executeUpdate();
            psDel.close();
        }
    }

    @Test
    void deleteFromConnectedUserDB() throws SQLException {
        if (con!= null) {
            //add user to connectedUser table
            jdao.addToConnectedUserDB(user1);

            MyLogger.getInstance().info("[TEST] - Connected user to add in DB: " + user1.getUsername());

            //check that user has been successfully added
            String query = "SELECT * FROM connected_users WHERE uuid=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, user1.getUuid().toString());
            ResultSet rs = ps.executeQuery();
            assertTrue(rs.next());
            assertEquals(user1.getUuid().toString(), rs.getString("uuid"));
            assertEquals(user1.getUsername(), rs.getString("username"));
            assertEquals(user1.getIP().toString().substring(1), rs.getString("ip"));
            rs.close();

            //delete added user from DB
            jdao.deleteFromConnectedUserDB(user1);

            //check that user has been successfully deleted
            String query2 = "SELECT * FROM connected_users WHERE uuid=?";
            PreparedStatement ps2 = con.prepareStatement(query2);
            ps2.setString(1, user1.getUuid().toString());
            ResultSet rs2 = ps2.executeQuery();
            assertFalse(rs2.next());

            MyLogger.getInstance().info("[TEST] - Deleted connected user from DB:" + user1.getUsername());
            rs2.close();
        }

    }

    @Test
    void updateConnectedUserDB() throws SQLException {
        if (con!= null){
            //add user to connectedUser table
            jdao.addToConnectedUserDB(user1);

            //check that user has been successfully added
            String query = "SELECT * FROM connected_users WHERE uuid=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, user1.getUuid().toString());
            ResultSet rs = ps.executeQuery();
            assertTrue(rs.next());
            assertEquals(user1.getUuid().toString(), rs.getString("uuid"));
            assertEquals(user1.getUsername(), rs.getString("username"));
            assertEquals(user1.getIP().toString().substring(1), rs.getString("ip"));
            String uuidUser1 = rs.getString("uuid");
            rs.close();

            //update username
            String newUsername = "new";
            jdao.updateConnectedUserDB(user1, newUsername);

            MyLogger.getInstance().info("[TEST] - Connected user " + user1.getUsername() + " has new username:" + newUsername);


            //check that user has been updated with correct new username
            String query2 = "SELECT * FROM main.connected_users WHERE uuid=?";
            PreparedStatement ps2 = con.prepareStatement(query2);
            ps2.setString(1, user1.getUuid().toString());
            ResultSet rs2 = ps2.executeQuery();
            assertTrue(rs2.next());
            assertEquals(newUsername, rs2.getString("username"));

            MyLogger.getInstance().info("[TEST] - Updated connected user " + user1.getUsername() + " in DB with new username:" + rs2.getString("username"));
            rs2.close();

            //delete added user from DB
            String delQuery = "DELETE FROM connected_users WHERE uuid=?";
            PreparedStatement psDel = con.prepareStatement(delQuery);
            psDel.setString(1, uuidUser1);
            psDel.executeUpdate();
            psDel.close();
        }
    }

    @Test
    void updateHistoryDB() throws SQLException {
        if (con != null){
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

            MyLogger.getInstance().info("[TEST] - User " + user1.getUsername() + " has new username:\n" + newUsername);

            //check that history db has been updated
            String query1 = "SELECT * FROM message_history WHERE sender_username = ?";
            PreparedStatement ps1 = con.prepareStatement(query1);
            ps1.setString(1, newUsername);
            ResultSet rs1 = ps1.executeQuery();
            ArrayList<String> senderIsUpdated = new ArrayList<>();
            while (rs1.next()){
                senderIsUpdated.add(rs1.getString("content"));

                MyLogger.getInstance().info("[TEST] - History has been updated for " + user1.getUsername() + " with new username:\n" + rs1.getString("sender_username"));
            }
            rs1.close();
            ps1.close();

            String query2 = "SELECT * FROM message_history WHERE receiver_username = ?";
            PreparedStatement ps2 = con.prepareStatement(query2);
            ps2.setString(1, newUsername);
            ResultSet rs2 = ps2.executeQuery();
            ArrayList<String> receiverIsUpdated = new ArrayList<>();
            while (rs2.next()){
                receiverIsUpdated.add(rs2.getString("content"));

                MyLogger.getInstance().info("[TEST] - History has been updated for " + user1.getUsername() + " with new username:\n" + rs2.getString("receiver_username"));
            }
            rs2.close();
            ps2.close();

            assertEquals(2, senderIsUpdated.size());
            assertEquals(1, receiverIsUpdated.size());
            assertEquals("ceci est un message",senderIsUpdated.get(0));
            assertEquals("ceci est un message 3",senderIsUpdated.get(1));
            assertEquals("ceci est un message 2",receiverIsUpdated.get(0));


            //delete messages from history
            jdao.deleteFromHistory(msg1);
            jdao.deleteFromHistory(msg2);
            jdao.deleteFromHistory(msg3);
        }
    }

    @Test
    void getHistoryWith() throws SQLException{
        if (con != null){
            //create two messages from user1
            TCPMessage msg1 = new TCPMessage(UUID.randomUUID(), "ceci est un message", user1, user2, new Timestamp(24859L));
            TCPMessage msg2 = new TCPMessage(UUID.randomUUID(), "ceci est un message 2", user2, user1, new Timestamp(24859L));
            TCPMessage msg3 = new TCPMessage(UUID.randomUUID(), "ceci n'est pas un message voulu", new User("spy"), user2, new Timestamp(24859L));

            //add messages to history
            jdao.addToHistoryDB(msg1);
            jdao.addToHistoryDB(msg2);
            jdao.addToHistoryDB(msg3);

            MyLogger.getInstance().info("[TEST] - Added message in history:  sent by: " + msg1.sender().getUsername() + "  content:" + msg1.content());
            MyLogger.getInstance().info("[TEST] - Added message in history:  sent by: " + msg2.sender().getUsername() + "  content:" + msg2.content());
            MyLogger.getInstance().info("[TEST] - Added message in history:  sent by: " + msg3.sender().getUsername() + "  content:" + msg3.content());


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
            assertEquals( 2, hist.size());
            for (TCPMessage message: hist){
                assertTrue(message.equals(msg1) || message.equals(msg2));
            }


            //delete inserted messages from history
            String delQuery = "DELETE FROM message_history WHERE uuid=?";
            PreparedStatement psDel = con.prepareStatement(delQuery);
            //del msg1
            psDel.setString(1,msg1UUID);
            psDel.executeUpdate();
            //del msg2
            psDel.setString(1, msg2UUID);
            psDel.executeUpdate();
            //del msg3
            psDel.setString(1, msg3UUID);
            psDel.executeUpdate();
        }
    }



    @Test
    void getConnectedUserList() throws SQLException, UnknownHostException {
        //insert connected users in table
        jdao.addToConnectedUserDB(self);
        jdao.addToConnectedUserDB(user1);
        jdao.addToConnectedUserDB(user2);

        //check that they have been added to the table
        String query = "SELECT * FROM connected_users WHERE uuid=?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, user1.getUuid().toString());
        ResultSet rs1 = ps.executeQuery();
        assertTrue(rs1.next());
        assertEquals(user1.getUsername(), rs1.getString("username"));
        String user1UUID = rs1.getString("uuid");
        rs1.close();

        ps.setString(1, user2.getUuid().toString());
        ResultSet rs2 = ps.executeQuery();
        assertTrue(rs2.next());
        assertEquals(user2.getUsername(), rs2.getString("username"));
        String user2UUID = rs2.getString("uuid");
        rs2.close();

        ps.setString(1, self.getUuid().toString());
        ResultSet rs3 = ps.executeQuery();
        assertTrue((rs3.next()));
        assertEquals(self.getUsername(), rs3.getString("username"));
        String selfUUID = rs3.getString("uuid");
        rs3.close();

        ps.close();

        //retrieve list of connected users
        ArrayList<ConnectedUser> conUsers = jdao.getConnectedUserList(self);

        //check that list corresponds
        assertEquals(2, conUsers.size());
        for (ConnectedUser us : conUsers){
            assertTrue(us.equals(user1) || us.equals(user2));

            MyLogger.getInstance().info("[TEST] - Connected user in DB:\n" + us);
        }

        //delete users from connected user table
        String delQuery = "DELETE FROM connected_users WHERE uuid=?";
        PreparedStatement psDel = con.prepareStatement(delQuery);
        psDel.setString(1, user1UUID);
        psDel.executeUpdate();
        psDel.setString(1, user2UUID);
        psDel.executeUpdate();
        psDel.setString(1, selfUUID);
        psDel.executeUpdate();
        psDel.close();
    }

    @AfterEach
    void tearDown() throws SQLException{
        if (con !=null){
            con.close();
        }
    }


}