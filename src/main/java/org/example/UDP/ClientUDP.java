package org.example.UDP;

import com.google.common.primitives.UnsignedLong;
import org.example.Message;
import org.example.Packet;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ClientUDP {
    private final DatagramSocket socket;
    public  ClientUDP() throws SocketException{
        socket = new DatagramSocket();
    }
    public void send(String message) throws IOException {
        Packet response = new Packet((byte) 10, UnsignedLong.fromLongBits(3L), new Message(1,1,message));
        byte[] responseBytes = response.encodePackage();
        DatagramPacket responseDatagramPacket = new DatagramPacket(responseBytes,responseBytes.length, InetAddress.getByName(null),3000);
        socket.send(responseDatagramPacket);
    }

    public Packet receive() throws Exception {
        DatagramPacket datagramPacket = new DatagramPacket(new byte[200],200);
        socket.receive(datagramPacket);
        Packet request =new Packet(Arrays.copyOfRange(datagramPacket.getData(),0,datagramPacket.getLength()));
        request.decode();
        return request;


    }
}
