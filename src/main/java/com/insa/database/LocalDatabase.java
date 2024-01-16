package com.insa.database;
import com.insa.users.User;

import java.net.InetAddress;


public class LocalDatabase {

    public static class Database{

        public static User currentUser;

        public static InetAddress currentIP;
    }
}
