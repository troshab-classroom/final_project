package org.example.UDP;

import org.example.Packet;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        ClientUDP client = new ClientUDP();
        client.send("Hello");
        Packet pac =  client.receive();
        System.out.println("receive "+ pac.getBMsq().getMessage());
    }
}
