package org.example.UDP;

import org.example.entities.Packet;

import java.net.*;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

public class StoreServerUDP {

    public static final Queue<Packet> PACKETS_TO_SEND = new ConcurrentLinkedQueue<>();

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
                Packet pac = PACKETS_TO_SEND.poll();
                if (pac != null) {
                    byte[] bytes = pac.encodePackage();
                    DatagramPacket datagramPacket = new DatagramPacket(bytes,bytes.length,clientsAddress.get(pac.getBSrc()));
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
                DatagramPacket datagramPacket = new DatagramPacket(new byte[Packet.MAX_SIZE], Packet.MAX_SIZE);
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
