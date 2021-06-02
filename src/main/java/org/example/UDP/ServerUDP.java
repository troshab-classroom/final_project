package org.example.UDP;

import com.google.common.primitives.UnsignedLong;
import org.example.Message;
import org.example.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ServerUDP {
    private final DatagramSocket socket;
    public ServerUDP(int port) throws SocketException{
        socket = new DatagramSocket(port);
    }
    public void send(){

    }

    public void receiveAndReply() throws Exception {
        DatagramPacket datagramPacket = new DatagramPacket(new byte[200],200);
        socket.receive(datagramPacket);

        Packet request =new Packet(Arrays.copyOfRange(datagramPacket.getData(),0,datagramPacket.getLength()));
        request.decode();
        System.out.println("receive "+new String(request.getBMsq().getMessage()));
        Packet response = new Packet((byte) 10, UnsignedLong.fromLongBits(3L), new Message(1,1,"request received"));

        byte[] responseBytes = response.encodePackage();
        DatagramPacket responseDatagramPacket = new DatagramPacket(responseBytes,responseBytes.length,datagramPacket.getSocketAddress());
        socket.send(responseDatagramPacket);

    }
}
