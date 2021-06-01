package org.example;
import  lombok.Data;
import java.nio.ByteBuffer;

@Data
public class Message {
    private Integer cType;
    private Integer bUserId;
    private String message;
    public Message()
    {}
    public Message(Integer cType, Integer bUserId,String message){
        this.cType = cType;
        this.bUserId = bUserId;
        this.message = message;
    }
    public int getMessageLen()
    {
        return message.length();
    }
    public int getBytesLength()
    {
        return Integer.BYTES +Integer.BYTES +  message.length();
    }
    public byte[] packetPart()
    {
        return ByteBuffer.allocate(getBytesLength()).putInt(cType).putInt(bUserId).put(message.getBytes()).array();
    }
    public  void encode()  {
        message = myCipher.encode(message);
    }
    public  void decode(){
        message = myCipher.decode(message);
    }
}
