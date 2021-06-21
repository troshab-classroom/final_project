package org.example.TCP;

import lombok.SneakyThrows;
import org.example.entities.Packet;

import java.net.*;
import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StoreClientTCP extends Thread {
    private Socket clientSocket;
    private OutputStream out;
    private InputStream in;
    private String ip;
    private int port;
    private static final int RECONNECT_MAX = 3;
    private static final AtomicInteger NUMBER_DEAD = new AtomicInteger(0);
    public StoreClientTCP(String ip, int port)
    {
        this.ip = ip;
        this.port = port;
    }
    public void startConnection() throws IOException {
        clientSocket = new Socket(ip, port);
        out = clientSocket.getOutputStream();
        in = clientSocket.getInputStream();
    }

    public Packet sendMessage(Packet p) throws Exception {
        out.write(p.toBytes());
        byte[] res = new byte[Packet.MAX_SIZE];
        in.read(res);
        System.out.println(res);
        Packet packet = new Packet(res);
        return packet;
    }
    public Packet reconnect(String ip, Packet pa, int reconnect_num) {
        try {
            final Socket socket = new Socket(ip, port);
            socket.setSoTimeout(3_000*reconnect_num);
            return sendMessage(pa);
        } catch (Exception e) {
            System.out.println("Reconnecting. Server is offline");
            if(reconnect_num == RECONNECT_MAX){
                NUMBER_DEAD.incrementAndGet();
                System.out.println("Server is dead. Number of dead connections: "+ NUMBER_DEAD);
                return null;
            }
            else{
                int reconnect = reconnect_num + 1;
                return reconnect(ip,pa,reconnect);
            }
        }
    }
    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
    @SneakyThrows
    @Override
    public void run() {
        try {
            startConnection();
        } catch (IOException e) {

            System.out.println("Couldn`t connect");
        }
    }
}
