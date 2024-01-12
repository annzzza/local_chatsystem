package com.insa.database;

import com.insa.network.ConnectedUser;
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
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JDBCdaoTest {

    JDBC jdbc = new JDBC();
    JDBCdao jdao = new JDBCdao();
    static Connection con;

    ConnectedUser user1;
    ConnectedUser user2;

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
    void setUp() throws SQLException, UnknownHostException {

        con = JDBC.getDBConnection();
        if (con != null){
            user1 = new ConnectedUser("annatest", (InetAddress.getByName("192.168.69.69")));
            user2 = new ConnectedUser("ronantest", InetAddress.getByName("192.168.42.42"));
        }
    }

    @Test
    void addToHistoryDB() throws SQLException{
        //Create a TCP Message
        TCPMessage msg = new TCPMessage(UUID.randomUUID(), "ceci est un message", user1, user2, new Timestamp(24859L));
        //Add it to the History table
        jdao.addToHistoryDB(msg);
        //Check that it has been successfully added
        String query = "SELECT * FROM message_history WHERE uuid=?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, msg.uuid().toString());
        ResultSet rs = ps.executeQuery();
        assertTrue(rs.next());
        assertEquals(msg.uuid().toString(), rs.getString("uuid"));
        assertEquals(msg.content(), rs.getString("content"));
        assertEquals(msg.date(), rs.getTimestamp("date"));
        assertEquals(msg.sender().getIP().toString().substring(1), rs.getString("sender_ip"));
        String uuidMSG = rs.getString("uuid");
        rs.close();
        ps.close();

        MyLogger.getInstance().info("[TEST] - Added message to DB:\n" + msg.toString());

        //Delete added message from DB
        String delQuery = "DELETE FROM message_history WHERE uuid=?";
        PreparedStatement psDel = con.prepareStatement(delQuery);
        psDel.setString(1, uuidMSG);
        psDel.executeUpdate();
        psDel.close();
    }

    @Test
    void addToConnectedUserDB() throws SQLException {
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

            //delete added user from DB
            String delQuery = "DELETE FROM connected_users WHERE uuid=?";
            PreparedStatement psDel = con.prepareStatement(delQuery);
            psDel.setString(1, uuidUser1);
            psDel.executeUpdate();
            psDel.close();
        }
    }

    @Test
    void getHistoryWith() throws SQLException, UnknownHostException{
        if (con != null){
            //create two messages from user1
            TCPMessage msg1 = new TCPMessage(UUID.randomUUID(), "ceci est un message", user1, user2, new Timestamp(24859L));
            TCPMessage msg2 = new TCPMessage(UUID.randomUUID(), "ceci est un message 2", user2, user1, new Timestamp(24859L));
            ConnectedUser espion = new ConnectedUser("espion", InetAddress.getByName("192.168.55.55"));
            TCPMessage msg3 = new TCPMessage(UUID.randomUUID(), "ceci n'est pas un message voulu", espion, user2, new Timestamp(24859L));

            //add messages to history
            jdao.addToHistoryDB(msg1);
            jdao.addToHistoryDB(msg2);
            jdao.addToHistoryDB(msg3);
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
            ArrayList<TCPMessage> hist = jdao.getHistoryWith(user1);
            //test that the message list corresponds
            assertEquals( 2, hist.size());
            for (TCPMessage message: hist){
                assertTrue(message.uuid().equals(msg1.uuid()) || message.uuid().equals(msg2.uuid()));
                assertTrue(message.content().equals(msg1.content()) || message.content().equals(msg2.content()));
                assertTrue(message.sender().equals(msg1.sender()) || message.sender().equals(msg2.sender()));

                MyLogger.getInstance().info("[TEST] - Message from history with " + user1.getUsername()  + ":\n" + message.content());

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

        ps.close();

        //retrieve list of connected users
        ArrayList<ConnectedUser> conUsers = jdao.getConnectedUserList();

        //check that list corresponds
        assertEquals(2, conUsers.size());
        for (ConnectedUser us : conUsers){
            assertTrue(us.equals(user1) || us.equals(user2));

            MyLogger.getInstance().info("[TEST] - Connected user in DB:\n" + us.toString());

        }

        //delete users from connected user table
        String delQuery = "DELETE FROM connected_users WHERE uuid=?";
        PreparedStatement psDel = con.prepareStatement(delQuery);
        psDel.setString(1, user1UUID);
        psDel.executeUpdate();
        psDel.setString(1, user2UUID);
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