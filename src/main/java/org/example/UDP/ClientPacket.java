package org.example.UDP;

import org.example.Packet;

public class ClientPacket {
    private final Packet packet;
    private final byte clientId;

    public ClientPacket(Packet packet, byte clientId) {
        this.packet = packet;
        this.clientId = clientId;
    }
    public Packet getPacket(){
        return packet;
    }
    public byte getClientId(){
        return clientId;
    }
}
