package org.example;
import java.net.*;
import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

class StoreClientTCP {
    private Socket clientSocket;
    private OutputStream out;
    private InputStream in;
    private static final int CLIENT_PORT = 5555;
    private static final int RECONNECT_MAX = 3;
    private static final AtomicInteger NUMBER_DEAD = new AtomicInteger(0);

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = clientSocket.getOutputStream();
        in = clientSocket.getInputStream();
    }

    public String sendMessage(Packet p) throws Exception {
        out.write(p.toBytes());
        byte[] res = new byte[Packet.MAX_SIZE];
        in.read(res);
        Packet packet = new Packet(res);
        String resp = packet.getBMsq().getMessage();
        return resp;
    }
    public String reconnect(String ip, Packet pa, int reconnect_num) {
        try {
            final Socket socket = new Socket(ip, CLIENT_PORT);
            socket.setSoTimeout(3_000*reconnect_num);
            return sendMessage(pa);
        } catch (Exception e) {
            System.out.println("Reconnecting. Server is offline");
            if(reconnect_num == RECONNECT_MAX){
                NUMBER_DEAD.incrementAndGet();
                System.out.println("Server is dead. Number of dead connections: "+ NUMBER_DEAD);
                return "";
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
}