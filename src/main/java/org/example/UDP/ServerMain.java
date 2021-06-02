package org.example.UDP;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        ServerUDP server = new ServerUDP(3000);
        server.receiveAndReply();
    }
}
