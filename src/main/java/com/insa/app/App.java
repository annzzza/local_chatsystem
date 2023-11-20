package com.insa.app;


import com.insa.GUI.MainWindow;
import com.insa.database.LocalDatabase;
import com.insa.network.NetworkManager;
import com.insa.network.User;
import com.insa.utils.Constants;
import com.insa.utils.MyLogger;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    MainWindow mainWindow = new MainWindow();
    @Override
    public void start(Stage primaryStage) throws Exception {

        mainWindow.start(new Stage());

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

    public static void main(String[] args){
        launch(args);
    }
}
