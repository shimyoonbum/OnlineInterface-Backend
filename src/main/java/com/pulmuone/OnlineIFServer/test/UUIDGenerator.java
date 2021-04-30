package com.pulmuone.OnlineIFServer.test;

import java.util.UUID;

public class UUIDGenerator {

    public static void main(String[] args) {
        for(int i=0; i<9; i++) {
            UUID uuid = UUID.randomUUID(); 

        	System.out.println("UUID : " + uuid.toString());
        }
    }
}
