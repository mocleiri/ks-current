package com.sigmasys.kuali.ksa.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;

/**
 * Contains encryption/decryption methods of commonly used algorithms.
 *
 * @author Michael Ivanov
 */
@SuppressWarnings("all")
public class CryptUtils {

    private static final String SECRET_KEY =
            "rO0ABXNyABRqYXZhLnNlY3VyaXR5LktleVJlcL35T7OImqVDAgAETAAJYWxnb3JpdGhtdAASTGph\n" +
                    "dmEvbGFuZy9TdHJpbmc7WwAHZW5jb2RlZHQAAltCTAAGZm9ybWF0cQB+AAFMAAR0eXBldAAbTGph\n" +
                    "dmEvc2VjdXJpdHkvS2V5UmVwJFR5cGU7eHB0AANERVN1cgACW0Ks8xf4BghU4AIAAHhwAAAACH/O\n" +
                    "4/SM1Z1KdAADUkFXfnIAGWphdmEuc2VjdXJpdHkuS2V5UmVwJFR5cGUAAAAAAAAAABIAAHhyAA5q\n" +
                    "YXZhLmxhbmcuRW51bQAAAAAAAAAAEgAAeHB0AAZTRUNSRVQ=";

    public static final String RSA = "RSA";
    public static final String DES = "DES";
    public static final String SHA1 = "SHA-1";
    public static final String SHA256 = "SHA-256";

    private static final BASE64Encoder encoder = new BASE64Encoder();
    private static final BASE64Decoder decoder = new BASE64Decoder();

    private CryptUtils() {
    }


    private static String keyToString(Key key) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(key);
            oos.close();
            return encoder.encode(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateDesKey() {
        try {
            return keyToString(KeyGenerator.getInstance(DES).generateKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String[] generateRsaKey(int keySize) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
            kpg.initialize(keySize);
            KeyPair keyPair = kpg.generateKeyPair();
            String[] keys = new String[2];
            keys[0] = keyToString(keyPair.getPrivate());
            keys[1] = keyToString(keyPair.getPublic());
            return keys;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String encryptRsa(String input, String publicKey) {
        return encoder.encode(crypt(Cipher.ENCRYPT_MODE, input.getBytes(), RSA, publicKey));
    }

    public static String decryptRsa(String input, String privateKey) {
        try {
            return new String(crypt(Cipher.DECRYPT_MODE, decoder.decodeBuffer(input), RSA, privateKey));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String encryptDes(String input, String key) {
        return encoder.encode(crypt(Cipher.ENCRYPT_MODE, input.getBytes(), DES, key));
    }

    public static String decryptDes(String input, String key) {
        try {
            return new String(crypt(Cipher.DECRYPT_MODE, decoder.decodeBuffer(input), DES, key));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String encryptDes(String input) {
        return encryptDes(input, SECRET_KEY);
    }

    public static String decryptDes(String input) {
        return decryptDes(input, SECRET_KEY);
    }

    private static byte[] crypt(int cipherMode, byte[] input, String algorithm, String key) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(decoder.decodeBuffer(key)));
            Key keyObject = (Key) ois.readObject();
            ois.close();
            cipher.init(cipherMode, keyObject);
            return cipher.doFinal(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String sha1(byte[] bytes) {
        return hash(bytes, SHA1);
    }

    public static String sha256(byte[] bytes) {
        return hash(bytes, SHA256);
    }

    public static String hash(byte[] bytes, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] messageDigest = md.digest(bytes);
            return encoder.encode(messageDigest);
        } catch (Exception e) {
            throw new RuntimeException("Exception occured while performing " + algorithm, e);
        }
    }

    public static String getDesKey() {
        return SECRET_KEY;
    }

    /**
     * Test main function
     *
     * @param args
     */
    /*public static void main(String[] args) {
        String[] keys = generateRsaKey(2048);
        System.out.println("private key = \n" + keys[0] + "\n");
        System.out.println("public key = \n" + keys[1] + "\n");

        String password = "Sonar123_ &$%%%gagaga[]]]]{{{}}}@~+|||+===";
        System.out.println("original password=" + password);
        String encPassword = encryptRsa(password, keys[1]);
        System.out.println("encoded password=" + encPassword);
        String decPassword = decryptRsa(encPassword, keys[0]);
        System.out.println("decoded password=" + decPassword);
    }*/

}
