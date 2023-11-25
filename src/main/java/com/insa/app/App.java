package com.insa.app;

import com.insa.GUI.MainWindow;
import com.insa.utils.MyLogger;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    MainWindow mainWindow = new MainWindow();
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Initalize logger
        MyLogger logger = MyLogger.getInstance();
        MyLogger.getInstance().info("Launching app");

        mainWindow.start();
    }

    public static void main(String[] args){
        launch(args);
    }
}
