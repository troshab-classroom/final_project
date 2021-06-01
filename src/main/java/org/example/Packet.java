
package org.example;

import com.google.common.primitives.UnsignedLong;
import lombok.Data;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import static org.example.CRC16.crc16;

@Data
public class Packet {

    private final static Byte bMagic = 0x13;
    private Byte bSrc;
    private UnsignedLong bPktId;
    private Integer wLen;
    private Message bMsq;
    private short wCrc16_1;
    private short wCrc16_2;

    public Packet (Byte bSrc, UnsignedLong bPktId, Message bMsq){
        this.bSrc = bSrc;
        this.bPktId = bPktId;
        this.bMsq = bMsq;
        wLen = bMsq.getMessageLen();
    }

    public Packet(byte[] bytes) throws Exception{
        //length check
        if(bytes.length<18)
        {
            throw new IllegalArgumentException("Too small");
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);
        Byte expectedBMagic = buffer.get();
        System.out.println("Magic, mysterious byte: " + bMagic);
        //magic byte check
        if(!expectedBMagic.equals(bMagic)){
            throw new Exception("Unexpected bMagic");
        }
        bSrc = buffer.get();
        System.out.println("Client: "+bSrc);
        bPktId = UnsignedLong.fromLongBits(buffer.getLong());
        System.out.println("Packet Id: "+bPktId);
        wLen = buffer.getInt();
        System.out.println("Length of encoded message: "+wLen);
        wCrc16_1 = buffer.getShort();
        System.out.println("CRC head: "+wCrc16_1);
        byte[] FirstPart = ByteBuffer.allocate(14)
                .order(ByteOrder.BIG_ENDIAN)
                .put(bMagic)
                .put(bSrc)
                .putLong(bPktId.longValue())
                .putInt(wLen)
                .array();
        //crc_1 check
        if(crc16(FirstPart)!=wCrc16_1) {
            throw new IllegalArgumentException("CRC16 head");
        }
        bMsq = new Message();
        bMsq.setCType(buffer.getInt());
        System.out.println("Command type: "+bMsq.getCType());
        bMsq.setBUserId(buffer.getInt());
        System.out.println("User id: "+bMsq.getBUserId());
        byte[] messageBody = new byte[wLen-8];
        //length checks
        if(bytes.length<18+wLen)
        {
            throw new IllegalArgumentException("Insufficient number of bytes");
        }
        if(bytes.length>18+wLen)
        {
            throw new IllegalArgumentException("Too long");
        }
        buffer.get(messageBody);
        bMsq.setMessage(new String(messageBody));
        bMsq.decode();
        System.out.println("Decoded message: "+bMsq.getMessage());
        byte[] message = Arrays.copyOfRange(bytes,16,16+wLen);
        wCrc16_2 = buffer.getShort();
        System.out.println("CRC message: "+ wCrc16_2);
        //crc_2 check
        if(crc16(message)!=wCrc16_2){
            throw new IllegalArgumentException("CRC16 message");
        }
        //get decoded message length
        wLen = bMsq.getMessageLen();
    }
    //basically, this method decodes message (but crcs stay as for encoded message),
    //it is useful for testing
    public void decode()
    {
        bMsq.decode();
        wLen = bMsq.getMessageLen();
    }
    public byte[] encodePackage(){
        Message message = getBMsq();
        message.encode();
        wLen = message.getMessageLen();
        byte[] FirstPart = ByteBuffer.allocate(14)
                .order(ByteOrder.BIG_ENDIAN)
                .put(bMagic)
                .put(bSrc)
                .putLong(bPktId.longValue())
                .putInt(message.getBytesLength())
                .array();
        wCrc16_1 =crc16(FirstPart);
        wCrc16_2 =crc16(message.packetPart());
        return ByteBuffer.allocate(16+message.getBytesLength()+2)
                .order(ByteOrder.BIG_ENDIAN)
                .put(FirstPart)
                .putShort(wCrc16_1)
                .put(message.packetPart())
                .putShort(wCrc16_2)
                .array();
    }
}
