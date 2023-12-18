package com.insa.network;

import java.util.UUID;

public class User {

    private String username;
    private UUID uuid;


    /** Single arg constructor   **/
    public User(String name){
        this.username=name;
        this.uuid = UUID.randomUUID();
    }

    /** Double arg constructor   **/
    public User(String name, UUID uuid){
        this.username=name;
        this.uuid=uuid;
    }


    /**
     * Test Equality of Users through UUID equality
     *
     * @param user to which the instance of User is comp)ared
     * @return boolean 1 if a User is equal to the instance of User by UUID
     */
    public boolean equals(User user){
        return (this.uuid==user.uuid);
    }


    /**
     * Setter for property Username
     *
     * @param name chosen username
     */
    public void setUsername(String name) {
        this.username=name;
    }

    /**
     * Getter for property type Username
     *
     * @return String Username of instance of User
     */
    public String getUsername(){
        return this.username;
    }

    /**
     * Getter for property UUID
     *
     * @return UUID, unique identifier of instance of User
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Serializer of User
     *
     * @return String containing fields of User
     */
    public String toString(){
        return "username: " + this.username + "\nUUID: " + this.uuid.toString();
    }


}
