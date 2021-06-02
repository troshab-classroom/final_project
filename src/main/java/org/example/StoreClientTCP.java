package org.example;
import lombok.SneakyThrows;

import java.net.*;
import java.io.*;
import java.util.Arrays;
//public class StoreClientTCP {
//}

class EchoClient {
    private Socket clientSocket;
    private OutputStream out;
    private InputStream in;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = clientSocket.getOutputStream();
        in = clientSocket.getInputStream();
    }

    public String sendMessage(Packet p) throws Exception {
        out.write(p.encodePackage());
        Packet packet = new Packet(in.readAllBytes());
        String resp = packet.getBMsq().getMessage();
        return resp;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}
