package com.insa.GUI.controller;

import com.insa.database.LocalDatabase;
import com.insa.network.NetworkManager;
import com.insa.network.TCPServer;
import com.insa.network.connectedusers.ConnectedUser;
import com.insa.utils.MyLogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;

/*
 * Controller for disconnection
 */
public class DisconnectionController implements ActionListener {

    TCPServer tcpServer;

    /*
        Constructor
        @param tcpServer: TCP server to close
     */
    public DisconnectionController(TCPServer server) {
        super();
        this.tcpServer = server;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        NetworkManager.getInstance().sendDisconnection(new ConnectedUser(LocalDatabase.Database.currentUser, LocalDatabase.Database.currentIP));
        try {
            tcpServer.close();
        } catch (IOException e) {
            MyLogger.getInstance().log("Error while closing TCP server" + e, Level.SEVERE);
        }
        System.exit(0);
    }
}
