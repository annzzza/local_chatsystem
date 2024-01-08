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
                +"(uuid, content, date, sender_ip) "
                + "VALUES (?, ?, ?, ?);";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, msg.uuid().toString());
        ps.setString(2, msg.content());
        ps.setDate(3, new Date(msg.date().getTime()));
        ps.setString(4, msg.sender().getIP().toString());
        int n = ps.executeUpdate();
    }

    public void deleteFromHistory(TCPMessage msg) throws SQLException{
        String query = "DELETE FROM message_history "
                + "WHERE uuid=" + msg.uuid().toString() + ";";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
    }

    public void addToConnectedUserDB(ConnectedUser user) throws SQLException {
        String query = "INSERT INTO connected_users "
                + "(uuid, username, ip) "
                + "VALUES (?, ?, ?);";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, user.getUuid().toString());
        ps.setString(2, user.getUsername());
        ps.setString(3, user.getIP().toString());
        int n = ps.executeUpdate();
    }

    public void deleteFromConnectedUSerDB(ConnectedUser user) throws SQLException{
        String query = "DELETE FROM connected_users "
                + "WHERE uuid = " + user.getUuid().toString() + ";";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
    }

    public ArrayList<TCPMessage> getHistoryWith(ConnectedUser user) throws SQLException, UnknownHostException {
        ArrayList<TCPMessage> res = new ArrayList<>();
        String query = "SELECT * from message_history "
                + "WHERE sender_ip = " + user.getIP().toString() + ";";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            User blankFiller = new User("blank");
            ConnectedUser cu = new ConnectedUser("user", InetAddress.getByName(rs.getString(4)));
            TCPMessage msg = new TCPMessage(UUID.fromString(rs.getString(1)), rs.getString(2), cu, blankFiller,new Timestamp(rs.getDate(3 ).getTime()));
            res.add(msg);
        }
        return res;
    }


    public ArrayList<ConnectedUser> getConnectedUserList() throws SQLException, UnknownHostException {
        ArrayList<ConnectedUser> res = new ArrayList<>();
        String query = "SELECT * from connected_users;";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            ConnectedUser cu = new ConnectedUser(rs.getString(2), UUID.fromString(rs.getString(1)), InetAddress.getByName(rs.getString(3)));
            res.add(cu);
        }
        return res;
    }

}
