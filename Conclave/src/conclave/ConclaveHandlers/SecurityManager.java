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

/**
 *
 * @author BradleyW
 */
public class SecurityManager {
    
    private Encryptor cipherAgent;
    private SecretKey skey;
    
    private static SecurityManager instance;
    
    private SecurityManager()
    {
        try {
            skey = KeyGenerator.getInstance("DES").generateKey();
            cipherAgent = new Encryptor(skey);
        } catch (NoSuchAlgorithmException e){
            System.out.println(e);
        }
    }
    public static synchronized SecurityManager getInstance() 
    {
        if (instance == null) {
                instance = new SecurityManager();}
        return instance;
    }
    
    public String hashPassword(String password, byte[] salt)
    {
        return cipherAgent.hashPassword(password, salt);
    }
    
    public String encrypt(String encryptThis)
    {
        return cipherAgent.encrypt(encryptThis);
    }
    
    public String decrypt(String decryptThis)
    {
        return cipherAgent.encrypt(decryptThis);
    }
}