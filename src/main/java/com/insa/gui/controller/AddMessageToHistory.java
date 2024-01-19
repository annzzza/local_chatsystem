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

    private HistoryDAO historyDAO = new HistoryDAO();
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
