package com.insa.database;

import com.insa.network.ConnectedUser;
import com.insa.network.TCPMessage;
import com.insa.network.User;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class JDBCdao {

    static Connection con = JDBC.getDBConnection();

    /**
     * adds a Message to the history table
     * @param msg TCP message that has been received
     * @throws SQLException incorrect query
     */
    public void addToHistoryDB(TCPMessage msg) throws SQLException {
        String query = "INSERT INTO message_history "
                +"(uuid, content, date, sender_username, receiver_username) "
                + "VALUES (?, ?, ?, ?, ?);";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, msg.uuid().toString());
        ps.setString(2, msg.content());
        ps.setDate(3, new Date(msg.date().getTime()));
        ps.setString(4, msg.sender().getUsername());
        ps.setString(5, msg.receiver().getUsername());
        ps.executeUpdate();
        ps.close();
    }

    /**
     * Deletes message from the history db
     * @param msg TCP message to be deleted
     * @throws SQLException incorrect query
     */
    public void deleteFromHistory(TCPMessage msg) throws SQLException{
        String query = "DELETE FROM message_history WHERE uuid=?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, msg.uuid().toString());
        ps.executeUpdate();
        ps.close();
    }

    /**
     * Adds ConnectedUser (on connection) to connected user table
     * @param user Connected User to be added in DB
     * @throws SQLException incorrect query
     */
    public void addToConnectedUserDB(ConnectedUser user) throws SQLException {
        String query = "INSERT INTO connected_users "
                + "(uuid, username, ip) "
                + "VALUES (?, ?, ?);";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, user.getUuid().toString());
        ps.setString(2, user.getUsername());
        ps.setString(3, user.getIP().toString().substring(1));
        ps.executeUpdate();
        ps.close();
    }

    /**
     * delete (on disconnection) a user from the connected user table
     * @param user ConnectedUser that disconnects
     * @throws SQLException incorrect query
     */
    public void deleteFromConnectedUserDB(ConnectedUser user) throws SQLException{
        String query = "DELETE FROM connected_users WHERE uuid = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, user.getUuid().toString());
        ps.executeUpdate();
    }

    /**
     * updates connected user table with new username for a given user (on username change)
     * @param user User whose username has been changed
     * @param newUsername new username for user
     * @throws SQLException incorrect query
     */
    public void updateConnectedUserDB(ConnectedUser user, String newUsername) throws  SQLException{
        String query = "UPDATE connected_users SET username = ? WHERE uuid = ?;";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, newUsername);
        ps.setString(2, user.getUuid().toString());
        ps.executeUpdate();
    }

    /**
     * Update 'receiver' and 'sender' fields of messages in history DB (on change username)
     * @param userToUpdate user sending the new username
     * @param newUsername new username for user
     * @throws SQLException incorrect query
     */
    public void updateHistoryDB(User userToUpdate, String newUsername) throws SQLException{
        //update username in message_history receiver field
        String query1 = "UPDATE message_history SET receiver_username = ? WHERE receiver_username = ?";
        PreparedStatement ps1 = con.prepareStatement(query1);
        ps1.setString(1, newUsername);
        ps1.setString(2, userToUpdate.getUsername());
        ps1.executeUpdate();
        ps1.close();

        //update username in message_history sender field
        String query2 = "UPDATE message_history SET sender_username=? WHERE sender_username= ?";
        PreparedStatement ps2 = con.prepareStatement(query2);
        ps2.setString(1, newUsername);
        ps2.setString(2, userToUpdate.getUsername());
        ps2.executeUpdate();
        ps2.close();
    }

    /**
     * Retrieve a list of TCP messages corresponding to a conversation with a selected user
     * @param selectedUser user selected by self
     * @param self user requesting the history list
     * @return arraylist containing messages sent by selectedUser to self AND messages sent by self to selectedUser
     * @throws SQLException incorrect query
     */
    public ArrayList<TCPMessage> getHistoryWith(User selectedUser, User self) throws SQLException {
        ArrayList<TCPMessage> res = new ArrayList<>();
        String query = "SELECT * from message_history WHERE (sender_username = ? or receiver_username = ?) ORDER BY date;";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, selectedUser.getUsername());
        ps.setString(2, selectedUser.getUsername());
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            if (rs.getString("sender_username").equals(self.getUsername())){
                TCPMessage msg = new TCPMessage(UUID.fromString(rs.getString("uuid")), rs.getString("content"), self, selectedUser, new Timestamp(rs.getDate("date" ).getTime()));
                res.add(msg);
            } else {
                TCPMessage msg = new TCPMessage(UUID.fromString(rs.getString("uuid")), rs.getString("content"), selectedUser, self, new Timestamp(rs.getDate("date" ).getTime()));
                res.add(msg);
            }
        }
        return res;
    }


    /**
     * @param self user requesting the list of connected users
     * @return List of connected users, excluding the connected user self
     * @throws SQLException incorrect query
     * @throws UnknownHostException unrecognized IP address
     */
    public ArrayList<ConnectedUser> getConnectedUserList(ConnectedUser self) throws SQLException, UnknownHostException {
        ArrayList<ConnectedUser> res = new ArrayList<>();
        String query = "SELECT * FROM connected_users WHERE uuid !=?;";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, self.getUuid().toString());
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            ConnectedUser cu = new ConnectedUser(rs.getString(2), UUID.fromString(rs.getString(1)), InetAddress.getByName(rs.getString(3)));
            res.add(cu);
        }
        return res;
    }

}
