package org.example.UDP;

import org.example.Packet;


public class ClientMain {
    public static void main(String[] args) throws Exception {
    for(byte i =0;i<10;i++){
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
