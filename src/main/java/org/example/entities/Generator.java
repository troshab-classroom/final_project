package org.example.entities;
import com.google.common.primitives.UnsignedLong;
import org.example.entities.Message;
import org.example.entities.Packet;
import org.example.entities.Warehouse;

import java.util.Random;

public class Generator {
    private static UnsignedLong packetId = UnsignedLong.fromLongBits(0L);
    public static Packet generate() {
        Random random = new Random();
        int command = random.nextInt(Warehouse.cTypes.values().length-1);
        String commandMsg = (Warehouse.cTypes.values()[command]).toString();
        Message testMessage = new Message(command ,random.nextInt(100), commandMsg);
        Packet packet = new Packet((byte)1, packetId, testMessage);
        packet.encodePackage();
        packetId = UnsignedLong.fromLongBits(packetId.longValue()+1L);
        return packet;
    }
}