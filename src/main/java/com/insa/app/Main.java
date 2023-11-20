package com.insa.app;

import com.insa.database.LocalDatabase;
import com.insa.network.*;
import com.insa.utils.Constants;
import com.insa.utils.MyLogger;

import java.net.*;

public class Main {
    public static void main(String[] args) throws UnknownHostException, InterruptedException {

        // Initalize logger
        MyLogger logger = MyLogger.getInstance();
        MyLogger.info("Launching app");

        // Network manager creation
        NetworkManager networkManager = NetworkManager.getInstance();

        MyLogger.info("Begin client discovery");
        LocalDatabase.Database.currentUser = new User("Ronan");
        networkManager.discoverNetwork("Ronan");

        MyLogger.info("Waiting for responses");
        Thread.sleep(Constants.DISCOVERY_TIMEOUT);

        MyLogger.info("Discovery finished");


        MyLogger.info("Display contacts list");
    }
}
