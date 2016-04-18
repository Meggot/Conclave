/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclaveclient.security;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author BradleyW
 */
public class Encryptor {

    public Encryptor() {
    }

    public static String encrypt(final String plainMessage,final String symKeyHex) {
        final byte[] symKeyData = DatatypeConverter.parseHexBinary(symKeyHex);

        final byte[] encodedMessage = plainMessage.getBytes(Charset
                .forName("UTF-8"));
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final int blockSize = cipher.getBlockSize();

            // create the key
            final SecretKeySpec symKey = new SecretKeySpec(symKeyData, "AES");

            // generate random IV using block size (possibly create a method for
            // this)
            final byte[] ivData = new byte[blockSize];
            final SecureRandom rnd = SecureRandom.getInstance("SHA1PRNG");
            rnd.nextBytes(ivData);
            final IvParameterSpec iv = new IvParameterSpec(ivData);

            cipher.init(Cipher.ENCRYPT_MODE, symKey, iv);

            final byte[] encryptedMessage = cipher.doFinal(encodedMessage);

            // concatenate IV and encrypted message
            final byte[] ivAndEncryptedMessage = new byte[ivData.length
                    + encryptedMessage.length];
            System.arraycopy(ivData, 0, ivAndEncryptedMessage, 0, blockSize);
            System.arraycopy(encryptedMessage, 0, ivAndEncryptedMessage,
                    blockSize, encryptedMessage.length);

            final String ivAndEncryptedMessageBase64 = DatatypeConverter
                    .printBase64Binary(ivAndEncryptedMessage);

            return ivAndEncryptedMessageBase64;
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException(
                    "key argument does not contain a valid AES key");
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "Unexpected exception during encryption", e);
        }
    }

    public static String decrypt(final String ivAndEncryptedMessageBase64,
            final String symKeyHex) {
        final byte[] symKeyData = DatatypeConverter.parseHexBinary(symKeyHex);

        final byte[] ivAndEncryptedMessage = DatatypeConverter
                .parseBase64Binary(ivAndEncryptedMessageBase64);
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final int blockSize = cipher.getBlockSize();

            // create the key
            final SecretKeySpec symKey = new SecretKeySpec(symKeyData, "AES");

            // retrieve random IV from start of the received message
            final byte[] ivData = new byte[blockSize];
            System.arraycopy(ivAndEncryptedMessage, 0, ivData, 0, blockSize);
            final IvParameterSpec iv = new IvParameterSpec(ivData);

            // retrieve the encrypted message itself
            final byte[] encryptedMessage = new byte[ivAndEncryptedMessage.length
                    - blockSize];
            System.arraycopy(ivAndEncryptedMessage, blockSize,
                    encryptedMessage, 0, encryptedMessage.length);

            cipher.init(Cipher.DECRYPT_MODE, symKey, iv);

            final byte[] encodedMessage = cipher.doFinal(encryptedMessage);

            // concatenate IV and encrypted message
            final String message = new String(encodedMessage,
                    Charset.forName("UTF-8"));

            return message;
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException(
                    "key argument does not contain a valid AES key");
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "Unexpected exception during decryption", e);
        }
    }

    public static String hashPassword(String password, byte[] salt) {
        String hashedString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashbytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hashbytes.length; i++) {
                sb.append(Integer.toString((hashbytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            hashedString = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashedString;
    }
}
