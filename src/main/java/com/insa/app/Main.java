package com.insa.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.insa.network.*;
import com.insa.utils.Constants;
import com.insa.utils.MyLogger;
import javafx.stage.Stage;

import java.net.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception, UnknownHostException, InterruptedException {

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
        App app = new App();
        app.start(new Stage());
    }
}
