package com.insa.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindow  extends Application {


    public void start(Stage stage) throws Exception {
        //Parent root = FXMLLoader.load(getClass().getResource("mainwindow.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("mainwindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Welcome !");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }}
