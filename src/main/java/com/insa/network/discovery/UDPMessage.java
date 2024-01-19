package com.insa.network.discovery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.insa.users.User;
import com.insa.utils.MyLogger;

import java.net.InetAddress;
import java.util.Objects;

public class UDPMessage {
    private static final MyLogger LOGGER = new MyLogger(UDPMessage.class.getName());
    public enum MessageType {DISCOVERY, USER_CONNECTED, USER_DISCONNECTED, USERNAME_CHANGED}

    /** Properties **/
    private MessageType type;
    private String content;
    private User sender;
    private InetAddress senderIP;


    /** No-arg constructor **/
    public UDPMessage(){   }

    /** Three-args constructor **/
    public UDPMessage(MessageType type, String content, User sender) {
        this.type = type;
        this.content = content;
        this.sender = sender;
    }

    /** Four-args constructor **/
    public UDPMessage(MessageType type, String content, User sender, InetAddress senderIP) {
        this.type = type;
        this.content = content;
        this.sender = sender;
        this.senderIP = senderIP;
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
     * Getter for the IP address of the sender
     * @return InetAddress
     */
    public InetAddress getSenderIP() {
        return senderIP;
    }

    /**
     * Set the IP address of the sender
     * @param senderIP InetAddress
     */
    public void setSenderIP(InetAddress senderIP) {
        this.senderIP = senderIP;
    }


    /**
     * Method overriding equals to compare two UDPMessage objects
     * @param other the object to compare to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            LOGGER.fine("[TEST] - Same object");
            return true;
        }
        if (other == null) {
            LOGGER.fine("[TEST] - Other is null");
            return false;
        }
        if (!(other instanceof UDPMessage msg)) {
            LOGGER.fine("[TEST] - Other is not a TCPMessage");
            return false;
        }

        return msg.type.equals(this.type) && msg.content.equals(this.content) && msg.sender.equals(this.sender)  && msg.senderIP.equals(this.senderIP);
    }

    /**
     * Method overriding hashCode to compare two TCPMessage objects
     *
     * @return the hashcode of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(type,content,sender,senderIP);
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
