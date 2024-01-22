package com.insa.users;

public class ConnectedUserAlreadyExists extends Exception {
    private final ConnectedUser connectedUser;

    public ConnectedUserAlreadyExists(ConnectedUser connectedUser) {
        this.connectedUser = connectedUser;
    }

    @Override
    public String toString() {
        return "ConnectedUserAlreadyExists{" +
                "username='" + connectedUser.getUsername() + '\'' +
                ", ip='" + connectedUser.getIP() + '\'' +
                '}';
    }
}
