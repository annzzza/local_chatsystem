package com.example.contactdiscovery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {
    Message msg = new Message(Message.MessageType.USER_CONNECTED, "Test");
    GsonBuilder builder = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .setPrettyPrinting();
    Gson gson = builder.create();

    @Test
    void getType() {

    }

    @Test
    void setType() {
    }

    @Test
    void getContent() {
    }

    @Test
    void setContent() {
    }

    @Test
    void toBytes() {
    }

    @Test
    void toJson() {

    }
}