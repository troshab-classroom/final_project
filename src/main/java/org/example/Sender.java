package org.example;
import org.example.entities.Packet;

import java.util.concurrent.*;

public class Sender implements Runnable{
    private static ExecutorService service4 = Executors.newFixedThreadPool(6);
    Packet packet;
    //made static to access in client
    public static BlockingQueue<Packet> queue;
    Sender(BlockingQueue<Packet> queue) throws InterruptedException {
        //this.packet = queue.take();
        Sender.queue =queue;
    }

    public static void sen(BlockingQueue<Packet> queue) throws Exception {
        service4.submit(new Sender(queue));
    }

    public void sendMessage() {
//        packet.getBMsq().setMessage(decrypt(packet.getBMsq().getMessage()));
//        System.out.println(packet.getBPktId()+": "+packet.getBMsq());
    }

    @Override
    public void run() {
        try {
            sendMessage();
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void shutdown(){
        try{
            service4.shutdown();
            while(!service4.awaitTermination(24L, TimeUnit.HOURS)){
                System.out.println("waiting for termination...");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
