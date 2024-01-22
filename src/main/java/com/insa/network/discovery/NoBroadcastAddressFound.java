package com.insa.network.discovery;

public class NoBroadcastAddressFound extends Exception {

    public NoBroadcastAddressFound(){
        super();
    }

    @Override
    public String toString() {
        return "No Broadcast Address found";
    }
}
