package org.example.TCP;
import com.google.common.primitives.UnsignedLong;
import lombok.SneakyThrows;
import org.example.DataBase;
import org.example.entities.Message;
import org.example.entities.Packet;
import org.example.Receiver;
import org.example.Sender;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

//(all steps are done using multithreading, except client) the main idea of what`s going on in this code....:
// 1. we got ENCODED message from client
// 2. then sent it to receiver (de facto, it is server in our inner structure)
// to get access to connect method, which do all the further work.
//  This work includes:
//   1) Decrypt message we have got
//   2) Process requests (now it just forms the answer okay and writes it to queue with responses)
//   3) Encrypt response
//   4) Send response to the server (sender class can be omitted, we just need it`s queue)
//   (de facto, it is client in our inner structure)
//3.Send response to the user

//What can be better in this code:
//1. We can use poison pill to have only one queue (BUT in this case we will have to process everything
// gradually: got 10 requests, got 10 responses, then again to receiver, so user will wait longer)
//2. We can remove receiver and sender classes, because its functionality implemented in server and in
// client
//3. We should remember client's addresses in ConcurrentMap to know exactly whose packek are sent
//(BUT in this realisation it is generating randomly)

//Maybe, i have written something illogical, but it is my attempt to explain....
class StoreServerTCP {
    private static final int SERVER_PORT = 5555;
    private ServerSocket serverSocket;
    static ExecutorService service7 = Executors.newFixedThreadPool(6);
    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        while (true)
            new StoreClientHandler(serverSocket.accept()).start();
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    private static class StoreClientHandler extends Thread {
        private Socket clientSocket;
        private OutputStream out;
        private InputStream in;

        public StoreClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @SneakyThrows
        public void run() {
            service7.submit(()-> {
                try {
                    out = clientSocket.getOutputStream();
                    in = clientSocket.getInputStream();
                    while (in.available()>=0) {
                        byte[] b = new byte[Packet.MAX_SIZE];
                        in.read(b);
                        BlockingQueue<Packet> queue = new LinkedBlockingQueue<>(5);
                        Packet p = new Packet(b);
                        if (p.getBSrc() == 0) {
                            out.write(new Packet((byte) 0, UnsignedLong.fromLongBits(7L), new Message(1, 2, "bye")).encodePackage());
                            break;
                        }
                        p.encodePackage();
                        queue.put(p);
                        Receiver r1 = new Receiver(queue);
                        new Thread(r1).start();
                        r1.connect();
                        Packet pac = Sender.queue.take();
                        out.write(pac.toBytes());
                    }
                    in.close();
                    out.close();
                    clientSocket.close();
                }catch(Exception e){}
            });
        }

        public static void main(String[] args) throws IOException {
            DataBase.connect();
            StoreServerTCP server = new StoreServerTCP();
            server.start(SERVER_PORT);
        }
    }
}
