package org.example;

import static org.junit.Assert.*;

import com.google.common.primitives.UnsignedLong;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class AppTest 
{
    @Test
    public void givenClient1_whenServerResponds_thenCorrect() throws Exception {
        for(int i =0;i<50;++i) {
            EchoClient client1 = new EchoClient();
            client1.startConnection("127.0.0.1", 5555);
            EchoClient client2 = new EchoClient();
            client2.startConnection("127.0.0.1", 5555);
            String msg12="";
            String msg22="";
            try {
                msg12 = client2.sendMessage(Generator.generate());
                msg22 = client2.sendMessage(Generator.generate());
            }catch(Exception e)
            {
                msg12 = client2.reconnect("127.0.0.1", Generator.generate(),1);
            }
            String msg1="";
            String msg2="";
            try {
                msg1 = client1.sendMessage(Generator.generate());
                msg2 = client1.sendMessage(Generator.generate());
            }catch(Exception e)
            {
                msg1=client1.reconnect("127.0.0.1", Generator.generate(), 1);
            }
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
        }
    }
}
