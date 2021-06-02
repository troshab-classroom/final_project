package org.example;

import java.util.concurrent.*;

public class Processor implements Runnable{
    private static ExecutorService service2 = Executors.newFixedThreadPool(6);
    private Packet packet;
    BlockingQueue<Packet> queue;
    BlockingQueue<Packet> queueResponse;
    final static int CODE_OF_OKAY = 7;

    public Processor(BlockingQueue<Packet> queue, BlockingQueue<Packet> queueResponse) throws InterruptedException {
        this.packet = queue.take();
        this.queue = queue;
        this.queueResponse = queueResponse;
    }

    public static void pro(BlockingQueue<Packet> queue,BlockingQueue<Packet> queueResponse) throws Exception {
        service2.submit(new Processor(queue,queueResponse));
    }

    @Override
    public void run() {
        try {
            Packet response = response();
            queueResponse.put(response);
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Packet response(){

        Message answer = new Message(CODE_OF_OKAY, packet.getBMsq().getBUserId(),"Ok");
        return new Packet((byte)1,
                packet.getBPktId(),
                answer);
    }

    public static void shutdown(){
        try{
            service2.shutdown();
            while(!service2.awaitTermination(24L, TimeUnit.HOURS)){
                System.out.println("waiting for termination...");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Decryptor.shutdown();
        Encryptor.shutdown();
        Sender.shutdown();
    }
}
