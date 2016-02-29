/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.ConclaveHandlers;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 *
 * @author BradleyW
 */
public class Encryptor{
    
    private SecretKey key;
    private Cipher ecipher;
    private Cipher dcipher;
    
    public Encryptor(SecretKey ikey)
    {
        try {
            key = ikey;
            ecipher = Cipher.getInstance("DES");
            dcipher = Cipher.getInstance("DES");
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            dcipher.init(Cipher.DECRYPT_MODE, key);
        } catch (NoSuchAlgorithmException e){
            System.out.println(e);
        } catch (NoSuchPaddingException e){
            System.out.println(e);
        } catch (InvalidKeyException e) {   
            System.out.println(e);
        }  
    }
    
    public String encrypt(String toBeEncrypted)
    {
        try {
        byte[] encoded = toBeEncrypted.getBytes("UTF8");
        byte[] enc = ecipher.doFinal(encoded);
        return new sun.misc.BASE64Encoder().encode(enc); // this will convert to a string.
        } catch (IOException e) {
          System.out.println(e);}
          catch (IllegalBlockSizeException e) {
          System.out.println(e); }
          catch (BadPaddingException e) {
          System.out.println(e);}
        return null;
    }
    
    public String decrypt(String toBeDecrypted)
    {
        try {
        byte[] decoded = new sun.misc.BASE64Decoder().decodeBuffer(toBeDecrypted);
        byte[] dec = dcipher.doFinal(decoded);
        return new String(dec, "UTF8");
        } catch (IOException ioe) {}
          catch (IllegalBlockSizeException ie) {}
          catch (BadPaddingException be) {}
        return null;
    }
    
    public String hashPassword(String password, byte[] salt)
    {
        String hashedString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashbytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0;i<hashbytes.length;i++)
            {
                sb.append(Integer.toString((hashbytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            hashedString = sb.toString();
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return hashedString;
    }
}
