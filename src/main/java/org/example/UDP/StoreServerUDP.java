package org.example.UDP;

import com.google.common.primitives.UnsignedLong;
import org.example.Message;
import org.example.Packet;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

public class StoreServerUDP {

    public static final Queue<ClientPacket> PACKETS_TO_SEND = new ConcurrentLinkedQueue<>();

    private final DatagramSocket socket;
    private final ConcurrentMap<Byte, ClientHandler> clients = new ConcurrentHashMap<>() ;
    private final ConcurrentMap<Byte, SocketAddress> clientsAddress = new ConcurrentHashMap<>() ;

    public StoreServerUDP(int port) throws SocketException{
        socket = new DatagramSocket(port);
    }
    public void start(){
        new Thread(this::receive, "Server Receiver").start();
        new Thread(this::sender, "Server Sender").start();
    }


    public void sender(){
        while (true){
            try {
                ClientPacket pac = PACKETS_TO_SEND.poll();
                if (pac != null) {
                    byte[] bytes = pac.getPacket().encodePackage();
                    DatagramPacket datagramPacket = new DatagramPacket(bytes,bytes.length,clientsAddress.get(pac.getClientId()));
                    socket.send(datagramPacket);
                    }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public void receive() {
        while (true) {
            try {
                DatagramPacket datagramPacket = new DatagramPacket(new byte[200], 200);
                socket.receive(datagramPacket);

                Packet request = new Packet(Arrays.copyOfRange(datagramPacket.getData(), 0, datagramPacket.getLength()));
                ClientHandler ch = clients.computeIfAbsent(request.getBSrc(), ClientHandler::new);
                ch.accept(request);
                clientsAddress.put(request.getBSrc(),datagramPacket.getSocketAddress());
            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }
}
