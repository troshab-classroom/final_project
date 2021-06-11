package org.example.UDP;

import org.example.entities.Packet;


public class ClientUDPMain {
    public static void main(String[] args) throws Exception {
    for(byte i =0;i<5;i++){
        client(i);
    }
    }

    private static void client(byte clientId){
        new Thread(() ->{
            try{
                StoreClientUDP client = new StoreClientUDP(clientId);
                client.send("Hello from client " + clientId);
                Packet pac = client.receive();
                System.out.println("response for client " + clientId + " - " + new String(pac.getBMsq().getMessage()));

            } catch (Exception e) {
            }
        }).start();
    }
}
