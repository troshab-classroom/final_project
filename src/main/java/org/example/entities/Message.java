package org.example.entities;
import  lombok.Data;
import org.example.Decryptor;
import org.example.Encryptor;

import java.nio.ByteBuffer;

@Data
public class Message {
    private Integer cType;
    private Integer bUserId;
    private String message;
    public static final int BYTES_WITHOUT_MESSAGE = Integer.BYTES + Integer.BYTES;
    static  int MAX_SIZE_FULL_MESSAGE = BYTES_WITHOUT_MESSAGE + 200;
    public Message()
    {}
    public Message(Integer cType, Integer bUserId, String message){
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
        return BYTES_WITHOUT_MESSAGE +  message.length();
    }
    public byte[] packetPart()
    {
        return ByteBuffer.allocate(getBytesLength()).putInt(cType).putInt(bUserId).put(message.getBytes()).array();
    }
    public  void encode()  {
        message = Encryptor.encrypt(message);
    }
    public  void decode(){

        message = Decryptor.decrypt(message);
    }
}
