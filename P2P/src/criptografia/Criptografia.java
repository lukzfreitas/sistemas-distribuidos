package criptografia;

import javax.crypto.*;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Criptografia {

    private Cipher ecipher;
    private Cipher dcipher;

    public Criptografia() {
        try {
            SecretKey chave = KeyGenerator.getInstance("DES").generateKey();
            ecipher = Cipher.getInstance("DES");
            dcipher = Cipher.getInstance("DES");
            ecipher.init(Cipher.ENCRYPT_MODE, chave);
            dcipher.init(Cipher.DECRYPT_MODE, chave);
        } catch (InvalidKeyException ex) {
            ex.printStackTrace();
        } catch (NoSuchPaddingException ex) {
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
    }

    public String encriptar(String str) {
        try {
            // Codifica a String usando UTF-8
            byte[] utf8 = str.getBytes("UTF-8");
            // Encripta
            byte[] enc = ecipher.doFinal(utf8);
            // Codifica os bytes usando Base64
            return new sun.misc.BASE64Encoder().encode(enc);
        } catch (javax.crypto.BadPaddingException ex) {
            ex.printStackTrace();
        } catch (IllegalBlockSizeException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
        // Caso nao consiga, retorna null
        return null;
    }

    public String decriptar(String str) {
        try {
            // Decodifica na base64 os bytes capturados
            byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
            // Decripta
            byte[] utf8 = dcipher.doFinal(dec);
            // Decodifica usando UTF-8
            return new String(utf8, "UTF-8");
        } catch (javax.crypto.BadPaddingException ex) {
            ex.printStackTrace();
        } catch (IllegalBlockSizeException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
        // Caso nao consiga, retorna null
        return null;
    }
}
