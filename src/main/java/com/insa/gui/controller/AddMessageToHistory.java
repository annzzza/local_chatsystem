package com.insa.gui.controller;

import com.insa.database.HistoryDAO;
import com.insa.network.tcp.TCPClient;
import com.insa.network.tcp.TCPMessage;
import com.insa.network.tcp.TCPServer;
import com.insa.utils.MyLogger;

import java.sql.SQLException;

/**
 * Observer for TCP client and server that adds any incoming or outgoing message into the message history
 */
public class AddMessageToHistory  implements TCPClient.TCPClientObserver, TCPServer.TCPServerObserver {
    private static final MyLogger LOGGER = new MyLogger(AddMessageToHistory.class.getName());

    private final HistoryDAO historyDAO = new HistoryDAO();

    /**
     * Update history when a message is sent
     * @param message the message that was sent
     */
    @Override
    public void sendMessage(TCPMessage message) {
        //Add message to history DB
        try{
            historyDAO.addToHistoryDB(message);
            LOGGER.info("successfully added msg in DB");
        } catch (SQLException e){
            LOGGER.severe("SQL Error: " + e.getMessage());
            LOGGER.severe("Message was not added to DB");
        }
    }

    /**
     * Update history on a received message
     * @param message received message
     */
    @Override
    public void onNewMessage(TCPMessage message) {
        //Add message to history DB
        try{
            historyDAO.addToHistoryDB(message);
            LOGGER.info("successfully added msg in DB");
        } catch (SQLException e){
            LOGGER.severe("SQL Error: " + e.getMessage());
            LOGGER.severe("Message was not added to DB");
        }
    }
}
