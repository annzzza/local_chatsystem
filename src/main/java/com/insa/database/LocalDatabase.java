package com.insa.database;
import com.insa.network.ConnectedUser;
import com.insa.network.User;

import java.util.ArrayList;
import java.util.List;


public class LocalDatabase {

    public static class Database{

        public static User currentUser;
        public static List<ConnectedUser> connectedUserList = new ArrayList<>();
    }
}
