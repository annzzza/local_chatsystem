package com.insa.GUI;

import com.insa.database.LocalDatabase;
import com.insa.network.ConnectedUser;
import com.insa.network.NetworkManager;
import com.insa.utils.MyLogger;

import java.util.Scanner;

import static java.lang.System.exit;

public class NoGui {
    private static volatile NoGui instance;

    boolean connected;
    String[] options;

    private NoGui() {
        connected = false;
        options = new String[]{
                "u - Choose username",
                "q - Quit",
        };
        menuManager();
    }

    synchronized static public NoGui getInstance() {
        NoGui result = instance;
        if (result != null) {
            return result;
        }
        synchronized (NoGui.class) {
            if (instance == null) {
                instance = new NoGui();
            }
            return instance;
        }
    }

    public void menuManager() {
        Scanner scanner = new Scanner(System.in);
        char selectedOption = '1';
        while (selectedOption != 'q' || selectedOption != 'Q') {
            printMenu(options);
            try {
                selectedOption = (char) System.in.read();
                switch (selectedOption) {
                    case 'u', 'U' -> {
                        usernameOption();
                    }
                    case 'c', 'C' -> {
                        changeUsernameOption();
                    }
                    case  'd', 'D' -> {
                        disconnectOption();
                    }
                    case  'l', 'L' -> {
                        showConnectedUserOption();
                    }
                    case 'Q', 'q' -> {
                        quitOption();
                    }
                }
            } catch (Exception e) {
                System.out.println("Please enter the letter associated to your option.");
                scanner.next();
            }
        }
    }

    public void printMenu(String[] options) {
        for (String option : options) {
            System.out.println(option);
        }
        System.out.println("Choose your option: ");
    }

    public void usernameOption() {
        Scanner scanner = new Scanner(System.in);
        String selectedUsername = "";
        while (selectedUsername.isBlank()) {
            System.out.println("Write your username: ");
            selectedUsername = scanner.next();
        }
        NetworkManager nwm = NetworkManager.getInstance();
        MyLogger.getInstance().info("Begin client discovery");
        if (nwm.discoverNetwork(selectedUsername)) {
            System.out.println("Username not available, please choose a new one.");
        } else {
            connected = true;
            options = new String[]{
                    "c - Change username",
                    "l - Show connectedUser",
                    "d - Disconnect",
                    "q - Quit",
            };
            System.out.println("Connected");
        }
    }

    public void changeUsernameOption() {

    }

    public void showConnectedUserOption() {
        System.out.println("Display connected users:");
        for(ConnectedUser u : LocalDatabase.Database.connectedUserList) {
            System.out.println("\t" +u.getUsername());
        }
        System.out.println();
    }
    public void disconnectOption() {
    }

    public void quitOption() {
        if(connected) {
            disconnectOption();
        }
        exit(0);
    }
}
