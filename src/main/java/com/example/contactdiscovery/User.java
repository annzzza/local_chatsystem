package com.example.contactdiscovery;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {

    public String username;
    private String password;

    /**Hash function for password**/
    public static String getMd5(String input)
    {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            // of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public User(String name, String pwd){
        this.username=name;
        this.password=getMd5(pwd);
    }


    public void setUsername(String name) {
        this.username=name;
    }
    public String getUsername(){
        return this.username;
    }


    public void setPassword(String password) {
        this.password = getMd5(password);
    }
    public String getPassword(){
        return this.password;
    }

}
