package com.insa.GUI.view;

import com.insa.database.FakeDatabase;
import com.insa.database.LocalDatabase;
import com.insa.network.ConnectedUser;
import com.insa.utils.MyLogger;

import javax.swing.*;
import java.util.*;

public class ChatGUI {

    /////////PEut etre à abandonner

        public JPanel oneChatBuilder(ConnectedUser user) {
            JPanel chat = new JPanel();
            JLabel usernameLabel = new JLabel(user.getUsername());
            chat.add(usernameLabel);
            MyLogger.getInstance().info("panel formed");

            System.out.println(chat);
            return chat;
        }



        public DefaultListModel<JPanel> chatListBuilder() {

            ////TO CHANGE for LocalDatabase!!!!!!
            List<ConnectedUser> connectedUserList = FakeDatabase.Database.makeConnectedUserList();
            DefaultListModel<JPanel> jPanelList = new DefaultListModel<>();

            MyLogger.getInstance().info("building list of chats");
            for (ConnectedUser connectedUser : connectedUserList) {
                MyLogger.getInstance().info("one chat added");
                jPanelList.addElement(oneChatBuilder((connectedUser)));
            }
            return jPanelList;
        }


        public DefaultListModel<String> chatListBuilderCorrectVersion(){
            List<ConnectedUser> connectedUserList = FakeDatabase.Database.makeConnectedUserList();
            DefaultListModel<String> chatList = new DefaultListModel<>();

            MyLogger.getInstance().info("building list of chats");
            for (ConnectedUser connectedUser : connectedUserList) {
                ChatClass chat = new ChatClass(connectedUser.getUsername());
                chatList.addElement(chat.toString());
            }

            return chatList;
        }

}
