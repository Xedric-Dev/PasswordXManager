package com.xedric_tech.password_manager.utils;

import org.springframework.stereotype.Component;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class AESUtil {

    private static final String ALGORITHM =  "AES/CBC/PKCS5Padding";
    private static final String DELIMITER = "<DELIM>";

    private final KeyStoreUtil keyStoreUtil;

    public AESUtil(KeyStoreUtil keyStoreUtil) {
        this.keyStoreUtil = keyStoreUtil;
    }

    public String encrypt(String toEncrypt){

        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            byte[] iv = generateIv();
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, keyStoreUtil.getAesKey(), ivSpec);

            byte[] encryptedMessageInBytes = cipher.doFinal(toEncrypt.getBytes(StandardCharsets.UTF_8));
            String base64Encoded = Base64.getEncoder().encodeToString(encryptedMessageInBytes);

            return Base64.getEncoder().encodeToString(iv) + DELIMITER + base64Encoded;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public String decrypt(String toDecrypt){

        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            String[] splittedEncoded = toDecrypt.split(DELIMITER, 2);

            IvParameterSpec ivSpec = new IvParameterSpec(Base64.getDecoder().decode(splittedEncoded[0]));

            cipher.init(Cipher.DECRYPT_MODE, keyStoreUtil.getAesKey(), ivSpec);

            byte[] encryptedMessageInBytes = Base64.getDecoder().decode(splittedEncoded[1]);
            byte[] encoded = cipher.doFinal(encryptedMessageInBytes);

            return new String(encoded);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private byte[] generateIv (){

        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        return iv;

    }

    private String bytesToString (byte[] bytes){

        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes){
            stringBuilder.append(b);
        }
        return stringBuilder.toString();

    }

}
