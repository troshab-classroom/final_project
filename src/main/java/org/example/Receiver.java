package org.example;

import java.util.concurrent.*;

public class Receiver implements Runnable, org.example.interfaces.Receiver {
    Packet packet;
    BlockingQueue<Packet> queueRequests;
    BlockingQueue<Packet> queueResponse;

    public Receiver(BlockingQueue<Packet> queue) throws InterruptedException {
        queueResponse = new LinkedBlockingQueue<>(5);
        //Packet packet = Generator.generate();
        this.queueRequests=queue;
        this.packet = queue.take();
    }
    @Override
    public void receiveMessage()
    {
        try {
            queueRequests.put(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connect() throws Exception {
        Decryptor.decr(queueRequests);
        Processor.pro(queueRequests, queueResponse);
        Encryptor.enc(queueResponse);
        Sender.sen(queueResponse);
    }

    @Override
    public void run() {
        try {
            receiveMessage();
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
