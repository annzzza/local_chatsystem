package com.insa.gui.controller;

import com.insa.database.LocalDatabase;
import com.insa.network.discovery.DiscoveryManager;
import com.insa.network.tcp.TCPServer;
import com.insa.users.ConnectedUser;
import com.insa.utils.MyLogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/*
 * Controller for disconnection
 */
public class DisconnectionController implements ActionListener {

    private static final MyLogger LOGGER = new MyLogger(DisconnectionController.class.getName());

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
        DiscoveryManager.getInstance().sendDisconnection(new ConnectedUser(LocalDatabase.Database.currentUser, LocalDatabase.Database.currentIP));
        try {
            tcpServer.close();
        } catch (IOException e) {
            LOGGER.severe("Error while closing TCP server" + e);
        }
        System.exit(0);
    }
}
