package com.insa.network.discovery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.insa.users.User;

public class UDPMessage {
    public enum MessageType {DISCOVERY, USER_CONNECTED, USER_DISCONNECTED, USERNAME_CHANGED}

    /** Properties **/
    private MessageType type;
    private String content;
    private User sender;


    /** No-arg constructor **/
    public UDPMessage(){   }

    /** Six-args constructor **/
    public UDPMessage(MessageType type, String content, User sender) {
        this.type = type;
        this.content = content;
        this.sender = sender;
    }


    /**
     * Getter for property type
     *
     * @return type of the message
     */
    public MessageType getType() {
        return type;
    }

    /**
     * Setter for property type
     *
     * @param type MessageType
     */
    public void setType(MessageType type) {
        this.type = type;
    }

    /**
     * Getter for message content
     *
     * @return String content of the message
     */
    public String getContent() {
        return content;
    }

    /**
     * Setter for message content
     *
     * @param data String
     */
    public void setContent(String data) {
        this.content = data;
    }


    /**
     * Getter for property Sender
     *
     * @return User sender of the message
     */
    public User getSender() {
        return sender;
    }

    /**
     * Setter for property Sender
     *
     * @param sender User
     */
    public void setSender(User sender) {
        this.sender = sender;
    }


    /**
     * Serializer of the message
     *
     * @return String containing the serialized instance of Message
     */
    @Override
    public String toString() {
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        return g.toJson(this);
    }
}
