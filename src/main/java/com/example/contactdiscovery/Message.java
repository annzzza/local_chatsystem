package com.example.contactdiscovery;



public class Message implements java.io.Serializable {


    public Message(MessageType type, String content) {
        this.type = type;
        this.content = content;
    }

    public enum MessageType {DISCOVERYSEND, USERNAMECHANGE, DISCOVERYACK};

    /** Properties **/
    private MessageType type;
    private String content;


    /** No-arg constructor **/
    public Message(){   }

    /**
     * Getter for property type
     *
     * @return
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

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] toBytes() {
        throw new UnsupportedOperationException();
    }

}
