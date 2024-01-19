package com.insa.network.discovery;

/**
 * Exception thrown when a username is already taken
 */
public class UsernameAlreadyTakenException extends Exception {
    private final String username;

    /**
     * Constructor of UsernameAlreadyTakenException
     *
     * @param username Username already taken
     */
    public UsernameAlreadyTakenException(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "UsernameAlreadyTaken{" +
                "username='" + username + '\'' +
                '}';
    }
}
