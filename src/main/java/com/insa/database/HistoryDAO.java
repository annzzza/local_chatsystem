package com.insa.database;

import com.insa.network.tcp.TCPMessage;
import com.insa.users.User;
import com.insa.utils.MyLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

/**
 * DAO for the history table
 */
public class HistoryDAO {

    private static final MyLogger LOGGER = new MyLogger(HistoryDAO.class.getName());

    /**
     * Connection to the database
     */
    Connection con = Database.getInstance().getConnection();

    /**
     * Constructor
     * Creates the history table if it does not exist
     */
    public HistoryDAO() {
        try {
            if (!doesTableExist(con, "message_history")) {
                Database.createTables(con);
            }
        } catch (SQLException e) {
            LOGGER.severe("Tables could not be created");
        }
    }

    /**
     * Check if a table exists in the database
     *
     * @param connection connection to the database
     * @param tableName  name of the table to check
     * @return true if the table exists, false otherwise
     */
    public  boolean doesTableExist(Connection connection, String tableName) {
        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, tableName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // If the result set has at least one row, the table exists
            } catch (SQLException e) {
                LOGGER.severe("No table found");
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * adds a Message to the history table
     *
     * @param msg TCP message that has been received
     * @throws DAOException message could not be added to history
     */
    public void addToHistoryDB(TCPMessage msg) throws DAOException {
        LOGGER.info("Adding message to history DB " + msg.content());

        String query = "INSERT INTO message_history "
                + "(uuid, content, date, sender_username, receiver_username) "
                + "VALUES (?, ?, ?, ?, ?);";

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, msg.uuid().toString());
            ps.setString(2, msg.content());
            ps.setDate(3, new Date(msg.date().getTime()));
            ps.setString(4, msg.sender().getUsername());
            ps.setString(5, msg.receiver().getUsername());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new DAOException("Error adding message to history", e);
        }

    }

    /**
     * Deletes message from the history db
     *
     * @param msg TCP message to be deleted
     * @throws DAOException message could not be deleted from history
     */
    public void deleteFromHistory(TCPMessage msg) throws DAOException {
        LOGGER.info("Deleting message from history DB " + msg.toString());

        try {
            String query = "DELETE FROM message_history WHERE uuid=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, msg.uuid().toString());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new DAOException("Error deleting message from history", e);
        }
    }


    /**
     * Update 'receiver' and 'sender' fields of messages in history DB (on change username)
     *
     * @param userToUpdate user sending the new username
     * @param newUsername  new username for user
     * @throws DAOException history could not be updated
     */
    public void updateHistoryDB(User userToUpdate, String newUsername) throws DAOException {
        LOGGER.info("Updating history DB for user " + userToUpdate.getUsername() + " with new username " + newUsername);

        try {
            //make sure the new username is not already used in the DB
            String query = "SELECT * FROM message_history WHERE receiver_username = ? or sender_username = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, newUsername);
            ps.setString(2, newUsername);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {

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
        } catch (SQLException e) {
            throw new DAOException("Error updating history with new username " + newUsername, e);
        }
    }

    /**
     * Retrieve a list of TCP messages corresponding to a conversation with a selected user
     *
     * @param selectedUser user selected by self
     * @param self         user requesting the history list
     * @return arraylist containing messages sent by selectedUser to self AND messages sent by self to selectedUser
     * @throws DAOException history could not be retrieved
     */
    public ArrayList<TCPMessage> getHistoryWith(User selectedUser, User self) throws DAOException {
        LOGGER.info("Getting history with " + selectedUser.getUsername() + " for user " + self.getUsername());

        ArrayList<TCPMessage> res = new ArrayList<>();

        try {
            String query = "SELECT * from message_history WHERE (sender_username = ? and receiver_username = ? or sender_username = ? and receiver_username = ?) ORDER BY date;";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, selectedUser.getUsername());
            ps.setString(2, self.getUsername());
            ps.setString(3, self.getUsername());
            ps.setString(4, selectedUser.getUsername());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("sender_username").equals(self.getUsername())) {
                    TCPMessage msg = new TCPMessage(UUID.fromString(rs.getString("uuid")), rs.getString("content"), self, selectedUser, new Timestamp(rs.getDate("date").getTime()));
                    res.add(msg);
                } else {
                    TCPMessage msg = new TCPMessage(UUID.fromString(rs.getString("uuid")), rs.getString("content"), selectedUser, self, new Timestamp(rs.getDate("date").getTime()));
                    res.add(msg);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error retrieving history with " + selectedUser.getUsername(), e);
        }
        return res;
    }

}
