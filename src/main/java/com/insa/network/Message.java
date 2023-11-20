package com.insa.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.UUID;

public class Message {
    public enum MessageType {DISCOVERY, USER_CONNECTED, USER_DISCONNECTED, USERNAME_CHANGED, USERNAME_TAKEN, TEXT_MESSAGE};

    /** Properties **/
    private MessageType type;
    private String content;
    private Date date = new Date();
    private User sender;
    private User receiver;
    private UUID uuid;


    /** No-arg constructor **/
    public Message(){   }

    public Message(MessageType type, String content, Date date, User sender, User receiver, UUID uuid) {
        this.type = type;
        this.content = content;
        this.date = date;
        this.sender = sender;
        this.receiver = receiver;
        this.uuid = uuid;
    }
    public Message(MessageType type, String content, Date date, User sender, User receiver) {
        this.type = type;
        this.content = content;
        this.date = date;
        this.sender = sender;
        this.receiver = receiver;
        this.uuid = UUID.randomUUID();
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
     * @param type
     */
    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String data) {
        this.content = data;
    }
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        return g.toJson(this);
    }
}
