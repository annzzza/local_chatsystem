package com.insa.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.insa.network.*;
import com.insa.utils.Constants;
import com.insa.utils.Logger;
import javafx.stage.Stage;

import java.net.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {

        Logger logger = Logger.getInstance();
        logger.log("Launching app");

        // Network manager creation
        NetworkManager networkManager = NetworkManager.getInstance();

        logger.log("Begin client discovery");
        networkManager.discoverNetwork("Anna");

        logger.log("Waiting for responses");
        Thread.sleep(Constants.DISCOVERY_TIMEOUT);

        logger.log("Discovery finished");

        logger.log("Client disconnect");

        App app = new App();
        app.start(new Stage());


    }
}
