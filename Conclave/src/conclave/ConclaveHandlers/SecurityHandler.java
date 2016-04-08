/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.ConclaveHandlers;

import util.Encryptor;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/** This class is responsible for hashing passwords and encrypting Strings using DES
 * Encryption.
 *
 * @author BradleyW
 */
public class SecurityHandler {
    
    private Encryptor cipherAgent;
    private SecretKey skey;
    
    private static SecurityHandler instance;
    
    /**
     * Initiates a DES key to initiate session keys.
     */
    private SecurityHandler()
    {
        try {
            skey = KeyGenerator.getInstance("DES").generateKey();
            cipherAgent = new Encryptor(skey);
        } catch (NoSuchAlgorithmException e){
            System.out.println(e);
        } 
    }
    
    /**\
     * As we want only one unique secret key, we should employ the
     * Singleton pattern here and only have one inistantiated object.
     * @return 
     */
    public static synchronized SecurityHandler getInstance() 
    {
        if (instance == null) {
                instance = new SecurityHandler();}
        return instance;
    }
    
    /**
     * SecurityHandler employs an Encryptor object to hash passwords.
     * It takes a byte[] array as an argument, which the CipherAgent 
     * decodes and hashes the password with it.
     * @param password
     * @param salt
     * @return 
     */
    
    public String hashPassword(String password, byte[] salt)
    {
        return cipherAgent.hashPassword(password, salt);
    }
    
    /**Encrypts a key, commonly a session key, or a message
     * using DES encryption.
     * 
     * @param encryptThis
     * @return 
     */
    public String encrypt(String encryptThis)
    {
        return cipherAgent.encrypt(encryptThis);
    }
    
    /**
     * Decrypt a string using a DES decryption algorithm
     * @param decryptThis
     * @return 
     */
    public String decrypt(String decryptThis)
    {
        return cipherAgent.encrypt(decryptThis);
    }
}