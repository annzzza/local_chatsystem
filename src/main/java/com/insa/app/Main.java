package com.insa.app;

import com.insa.users.ConnectedUserList;
import com.insa.utils.MyLogger;

import java.util.*;

public class Main {
    private static final MyLogger LOGGER = new MyLogger(Main.class.getName());

    public static void main(String[] args) {
        LOGGER.info("Args: " + Arrays.toString(args));

        // To initialize it
        ConnectedUserList connectedUserList = ConnectedUserList.getInstance();
        App.main(args);

    }
}
