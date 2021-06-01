package org.example;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;

public class myCipher {
    private static final String key = "my_unbreakable_key_no_one_should_know";
    private static final String helper = "interesting";
    private static final byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public static String encode(String strToEn) {
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

    public static String decode(String strToDec) {
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
}
