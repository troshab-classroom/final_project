package org.example;
import com.google.common.primitives.UnsignedLong;
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
            System.out.println(in.available());
            while(in.available()==0) {
                try {
                    byte[] b = new byte[100];
                    in.read(b);
                    BlockingQueue<Packet> queue = new LinkedBlockingQueue<>(5);
                    Packet p = new Packet(b);
                    if (p.getBSrc() == 0) {
                        out.write(new Packet((byte) 0, UnsignedLong.fromLongBits(7L), new Message(1, 2, "bye")).encodePackage());
                        break;
                    }
                    queue.put(p);
                    Receiver r1 = new Receiver(queue);
                    new Thread(r1).start();
                    r1.connect();
                    out.write(Sender.queue.take().toBytes());
                }catch(Exception e)
                {}
            }
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
