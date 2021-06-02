package org.example;
import lombok.SneakyThrows;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

//public class StoreServerTCP {
//}
class EchoMultiServer {
    private static final AtomicInteger SENT = new AtomicInteger(0);
    private ServerSocket serverSocket;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        while (true)
            new EchoClientHandler(serverSocket.accept()).start();
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    private static class EchoClientHandler extends Thread {
        private Socket clientSocket;
        private OutputStream out;
        private InputStream in;

        public EchoClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @SneakyThrows
        public void run() {
            out = clientSocket.getOutputStream();
            in = clientSocket.getInputStream() ;
            while(in.available()>0) {
                byte[] input = in.readAllBytes();
                BlockingQueue<Packet> queue = new LinkedBlockingQueue<>(5);
                Packet p = new Packet(input);
                p.encodePackage();
                queue.put(p);
                Receiver r1 = new Receiver(queue);
                new Thread(r1).start();
                r1.connect();
                out.write(Sender.queue.take().encodePackage());
            }
//            while ((inputLine = in.readLine()) != null) {
//                System.out.println(inputLine);
//                if (".".equals(inputLine)) {
//                    out.println("bye");
//                    break;
//                }
//                out.println(inputLine);
//            }

            in.close();
            out.close();
            clientSocket.close();
        }
        public static void main(String[] args) throws IOException {
            EchoMultiServer server = new EchoMultiServer();
            server.start(5555);
        }
    }
}
