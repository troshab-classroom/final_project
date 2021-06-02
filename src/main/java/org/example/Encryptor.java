package org.example;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.concurrent.*;

public class Encryptor implements Runnable {
    Packet packet;
    BlockingQueue<Packet> queue;
    private static ExecutorService service3 = Executors.newFixedThreadPool(6);
    private static final String key = "my_unbreakable_key_no_one_should_know";
    private static final String helper = "interesting";
    private static final byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    Encryptor( BlockingQueue<Packet> queue ) throws InterruptedException {
        this.queue=queue;
        this.packet = queue.take();
    }

    public static void enc(BlockingQueue<Packet> queue) throws Exception {
        service3.submit(new Encryptor(queue));
    }

    public static String encrypt(String strToEn) {
        try {
            IvParameterSpec sp_iv = new IvParameterSpec(iv);
            SecretKeyFactory secret = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(key.toCharArray(), helper.getBytes(), 65536, 256);
            SecretKey s_k = secret.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(s_k.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, sp_iv);
            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(strToEn.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static void shutdown(){
        try{
            service3.shutdown();
            while(!service3.awaitTermination(24L, TimeUnit.HOURS)){
                System.out.println("waiting for termination...");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
           // packet.getBMsq().setMessage(encrypt(packet.getBMsq().getMessage()));
            packet.encodePackage();
            queue.put(packet);
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
