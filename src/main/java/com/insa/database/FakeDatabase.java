package com.insa.database;

import com.insa.network.ConnectedUser;
import com.insa.network.Message;
import com.insa.network.User;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FakeDatabase {

    public static class Database{

        public static User currentUser;

        public static InetAddress currentIP;

        static ConnectedUser user1;

        static {
            try {
                user1 = new ConnectedUser("anna", InetAddress.getByName("0.0.0.2"));
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }

        static ConnectedUser user2;

        static {
            try {
                user2 = new ConnectedUser("ronan", InetAddress.getByName("localhost"));
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }

        public static List<ConnectedUser> makeConnectedUserList() {
            List<ConnectedUser> connectedUserList = new ArrayList<>();
            connectedUserList.add(user1);
            connectedUserList.add(user2);
            return connectedUserList;
        }

        public static ArrayList<Message> getHistoryMessages(){
            ArrayList<Message> testListChats = new ArrayList<>();
            testListChats.add(new Message(Message.MessageType.TEXT_MESSAGE, "Salut cv? tfq?", new Date(), user1, user2));
            testListChats.add(new Message(Message.MessageType.TEXT_MESSAGE, "Oue et toi? je f r toi", new Date(), user2, user1));
            testListChats.add(new Message(Message.MessageType.TEXT_MESSAGE, "je v mangé", new Date(), user1, user2));
            testListChats.add(new Message(Message.MessageType.TEXT_MESSAGE, "ah oue moi j deha mangé mdr", new Date(), user2, user1));
            testListChats.add(new Message(Message.MessageType.TEXT_MESSAGE, "*deja", new Date(), user2, user1));

            return testListChats;
        }

        public Database() throws UnknownHostException {
        }
    }
}
