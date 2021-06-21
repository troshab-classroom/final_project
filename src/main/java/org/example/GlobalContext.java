package org.example;

import org.example.TCP.StoreClientTCP;

public class GlobalContext {
    public static StoreClientTCP clientTCP = new StoreClientTCP("127.0.0.1", 5555);
    public static String role;
    public static int packetId = 0;

}
