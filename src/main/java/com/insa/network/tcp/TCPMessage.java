package com.insa.network.tcp;

import com.insa.users.User;
import com.insa.utils.MyLogger;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

/**
 * This class represents a TCP message
 */
public record TCPMessage(UUID uuid, String content, User sender, User receiver, Timestamp date) {
    private static final MyLogger LOGGER = new MyLogger(TCPMessage.class.getName());

    /**
     * Method overriding equals to compare two TCPMessage objects
     *
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
        if (!(other instanceof TCPMessage msg)) {
            LOGGER.fine("[TEST] - Other is not a TCPMessage");
            return false;
        }

        return msg.content.equals(this.content) && msg.sender.equals(this.sender) && msg.receiver.equals(this.receiver) && msg.date.equals(this.date);
    }

    /**
     * Method overriding hashCode to compare two TCPMessage objects
     *
     * @return the hashcode of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(content, sender, receiver, date);
    }

}
