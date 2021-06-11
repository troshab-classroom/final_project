package org.example;
import lombok.Data;
import org.example.entities.Packet;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.concurrent.*;
@Data

public class Decryptor implements  Runnable{
    private static ExecutorService service1 = Executors.newFixedThreadPool(6);
    Packet packet;
    BlockingQueue<Packet> queue;
    private static final String key = "my_unbreakable_key_no_one_should_know";
    private static final String helper = "interesting";
    private static final byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    Decryptor(BlockingQueue<Packet> queue) throws InterruptedException {
        this.packet = queue.take();
        this.queue=queue;
    }

    public static void decr(BlockingQueue<Packet> queue) throws Exception {
        service1.submit(new Decryptor(queue));
    }

    @Override
    public void run() {
        try {
            packet.getBMsq().setMessage(decrypt(packet.getBMsq().getMessage()));
            System.out.println(packet.getBPktId()+ ": " + packet.getBMsq());
            queue.put(packet);
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String decrypt(String strToDec) {
        try {
            IvParameterSpec sp_iv = new IvParameterSpec(iv);
            SecretKeyFactory secret = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(key.toCharArray(), helper.getBytes(), 65536, 256);
            SecretKey tmp = secret.generateSecret(spec);
            SecretKeySpec s_k = new SecretKeySpec(tmp.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, s_k, sp_iv);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDec)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

    public static void shutdown(){
        try{
            service1.shutdown();
            while(!service1.awaitTermination(24L, TimeUnit.HOURS)){
                System.out.println("waiting for termination...");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
