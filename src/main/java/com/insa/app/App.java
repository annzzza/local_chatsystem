package com.insa.app;


import javafx.application.Application;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../GUI/view/set-username.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Welcome !");
        stage.setScene(scene);
        stage.show();
    }
}
