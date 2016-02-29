/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.ConclaveHandlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 *
 * @author BradleyW
 */
public class SecurityManager {
    
    private Encryptor cipherAgent;
    private SecretKey skey;
    private String encryptionlocation;
    
    private static SecurityManager instance;
    
    private SecurityManager()
    {
        try {
            encryptionlocation = "encryptedUsers.txt";
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
    
    
    /**
    public boolean validatePassword(String identifier, String propPassword)
    {
        boolean ok = false;
        try {
        propPassword = SALT + propPassword;
        String pwEntry = identifier + " " + cipherAgent.encrypt(propPassword);
        BufferedReader reader = new BufferedReader(new FileReader(encryptionlocation));
        String line = "";
        while (reader.ready()) {
            line = reader.readLine();
            if (line.equals(pwEntry))
            { 
                ok = true;
                break;
            }
        }
        } catch (Exception e) {
            System.out.println(e);
        }
        return ok;
    }
   
    public boolean addPassword(String identifier, String ptPassword)
    {
        boolean ok = false;
        try {
        boolean existingCombo = validatePassword(identifier, ptPassword);
        ptPassword = SALT + ptPassword;
        String encString = cipherAgent.encrypt(ptPassword);
        if (ptPassword.length() < 27 && identifier.length() < 20 && !existingCombo)
        {
            String pwEntry = identifier + " " + encString;
            PrintWriter outer = new PrintWriter(new BufferedWriter(new FileWriter(encryptionlocation, true)));
            outer.println(pwEntry);
            outer.close();
            ok = true;
        } else {
            ok = false;
        }
        } catch (Exception e) {
            System.out.println(e);
        }
        return ok;
    }
    
}
* **/
