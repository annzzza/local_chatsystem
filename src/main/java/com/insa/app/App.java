package com.insa.app;

import com.insa.gui.LoginWindow;
import com.insa.utils.MyLogger;


import javax.swing.*;

public class App {

    private final LoginWindow loginWindow = new LoginWindow();
    private static final MyLogger LOGGER = new MyLogger(App.class.getName());

    public void start() {
        LOGGER.info("Launching app");

        SwingUtilities.invokeLater(loginWindow::start);
    }

    public static void main(String[] args) {
        new App().start();
    }
}