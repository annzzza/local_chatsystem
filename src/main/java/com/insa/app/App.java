package com.insa.app;

import com.insa.GUI.LoginWindow;
import com.insa.utils.MyLogger;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    LoginWindow loginWindow = new LoginWindow();
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Initalize logger
        MyLogger logger = MyLogger.getInstance();
        MyLogger.getInstance().info("Launching app");

        loginWindow.start();
    }

    public static void main(String[] args){
        launch(args);
    }
}
