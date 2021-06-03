package org.example;
import static org.junit.Assert.*;
import com.google.common.primitives.UnsignedLong;
import org.example.TCP.StoreClientTCP;
import org.example.UDP.StoreClientUDP;
import org.example.UDP.StoreServerUDP;
import org.junit.Test;
//commit for branch
import java.net.SocketException;

public class AppTest
{
//can be tested when server is offline
    @Test
    public void given100Clients_whenServerResponds_thenCorrect() throws Exception {
        for(int i =0;i<3;++i) {
            StoreClientTCP client1 = new StoreClientTCP("127.0.0.1", 5555);
            try {
                StoreClientTCP client2 = new StoreClientTCP("127.0.0.1", 5555);
                Thread t1 = new Thread(client1);
                t1.start();
                t1.join();
                Thread t2 = new Thread(client2);
                t2.start();
                t2.join();
                String msg12="";
                String msg22="";
                msg12 = client2.sendMessage(Generator.generate());
                msg22 = client2.sendMessage(Generator.generate());
                String msg1="";
                String msg2="";
                msg1 = client1.sendMessage(Generator.generate());
                msg2 = client1.sendMessage(Generator.generate());
                Packet end = new Packet((byte) 0, UnsignedLong.fromLongBits(7L), new Message(1, 2, "bye"));
                end.encodePackage();
                String msg = client1.sendMessage(end);
                String ms = client2.sendMessage(end);
                assertEquals(msg1, "Ok");
                assertEquals(msg2, "Ok");
                assertEquals(msg12, "Ok");
                assertEquals(msg22, "Ok");
                assertEquals(msg, "bye");
                assertEquals(ms, "bye");
            }catch(Exception e)
            {
                client1.reconnect("127.0.0.1", Generator.generate(),1);
            }

        }
    }


    @Test
    public void UDPTest() throws SocketException {
      //  StoreServerUDP server = new StoreServerUDP(3000);
      //  server.start();
     for(byte i =0;i<5;i++){
         byte finalI = i;
         byte finalI1 = i;
         new Thread(() ->{
             try{
                 StoreClientUDP client = new StoreClientUDP(finalI);
                 client.send("Hello from client " + finalI);
                 Packet pac = client.receive();
                 System.out.println("response for client " + finalI + " - " + new String(pac.getBMsq().getMessage()));
                 assertEquals(pac.getBMsq().getMessage(),"ok "+ finalI1);
             } catch (Exception e) {
             }
         }).start();
    }

}

}
