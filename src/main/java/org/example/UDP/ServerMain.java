package org.example.UDP;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        StoreServerUDP server = new StoreServerUDP(3000);
        server.start();
    }
}
