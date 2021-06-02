package org.example.UDP;

import com.google.common.primitives.UnsignedLong;
import org.example.Message;
import org.example.Packet;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class StoreClientUDP {
    private final DatagramSocket socket;
    private final byte clientId;

    public StoreClientUDP(byte clientId) throws SocketException{
        socket = new DatagramSocket();
        this.clientId=clientId;
    }
    public void send(String message) throws IOException {
        Packet response = new Packet(clientId, UnsignedLong.fromLongBits(3L), new Message(1,1,message));
        byte[] responseBytes = response.encodePackage();
        DatagramPacket responseDatagramPacket = new DatagramPacket(responseBytes,responseBytes.length, InetAddress.getByName(null),3000);
        socket.send(responseDatagramPacket);
    }

    public Packet receive() throws Exception {
        DatagramPacket datagramPacket = new DatagramPacket(new byte[200],200);
        socket.receive(datagramPacket);
        Packet request =new Packet(Arrays.copyOfRange(datagramPacket.getData(),0,datagramPacket.getLength()));

        return request;


    }
}