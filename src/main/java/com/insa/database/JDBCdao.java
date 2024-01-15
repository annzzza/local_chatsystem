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

    public void deleteFromHistory(TCPMessage msg) throws SQLException{
        String query = "DELETE FROM message_history WHERE uuid=?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, msg.uuid().toString());
        ps.executeUpdate();
        ps.close();
    }

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

    public void deleteFromConnectedUserDB(ConnectedUser user) throws SQLException{
        String query = "DELETE FROM connected_users WHERE uuid = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, user.getUuid().toString());
        ps.executeUpdate();
    }

    public void updateConnectedUserDB(ConnectedUser user, String newUsername) throws  SQLException{
        String query = "UPDATE connected_users SET username = ? WHERE uuid = ?;";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, newUsername);
        ps.setString(2, user.getUuid().toString());
        ps.executeUpdate();
    }

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
