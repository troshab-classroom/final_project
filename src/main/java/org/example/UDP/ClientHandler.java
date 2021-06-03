package org.example.UDP;

import com.google.common.primitives.UnsignedLong;
import org.example.Message;
import org.example.Packet;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientHandler extends Thread {
    private final byte clientId;
    private final Queue<Packet> queue;

    public ClientHandler(byte clientId){
        super("ClientHandler" + clientId);
        this.clientId=clientId;
        queue = new ConcurrentLinkedQueue<>();
        start();

    }

    public void accept(Packet pac){
        queue.add(pac);
       }
    @Override
    public void run(){
        while (true) {
            try {
                Packet pac = queue.poll();
                if (pac != null) {
                    System.out.println("receive: " + new String(pac.getBMsq().getMessage()));

                    Thread.sleep(new Random().nextInt(1000));

                    StoreServerUDP.PACKETS_TO_SEND.add(new Packet(pac.getBSrc(), UnsignedLong.fromLongBits(1000), new Message(1, 1, "ok " + pac.getBSrc())));
                }
            }catch(Exception e){

            }
        }
    }
}
