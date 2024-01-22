package com.insa.gui.controller;

import com.insa.database.DAOException;
import com.insa.database.HistoryDAO;
import com.insa.network.tcp.TCPClient;
import com.insa.network.tcp.TCPMessage;
import com.insa.network.tcp.TCPServer;
import com.insa.utils.MyLogger;

/**
 * Observer for TCP client and server that adds any incoming or outgoing message into the message history
 */
public class AddMessageToHistory  implements TCPClient.TCPClientObserver, TCPServer.TCPServerObserver {
    private static final MyLogger LOGGER = new MyLogger(AddMessageToHistory.class.getName());

    private final HistoryDAO historyDAO = new HistoryDAO();

    private void messageToDB(TCPMessage message) {
        //Add message to history DB
        try {
            historyDAO.addToHistoryDB(message);
            LOGGER.info("successfully added msg in DB");
        } catch (DAOException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    /**
     * Update history when a message is sent
     *
     * @param message the message that was sent
     */
    @Override
    public void sendMessage(TCPMessage message) {
        messageToDB(message);
    }

    /**
     * Update history on a received message
     *
     * @param message received message
     */
    @Override
    public void onNewMessage(TCPMessage message) {
        messageToDB(message);
    }
}
