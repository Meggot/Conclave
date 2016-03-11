/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave;

import conclave.db.Account;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author BradleyW
 */
public class TestClass {
    
    public static void main(String args[]) throws RemoteException, UnknownHostException
    {
        //try {
        ServerController testClave = ServerController.getInstance();
        testClave.setPort(20003);
        testClave.setIp(InetAddress.getLocalHost());
        testClave.addAdmin("MeggotZolu");
        testClave.startServer();
        //} catch (UnknownHostException e)
       // {
        //    e.printStackTrace();
        //}
    }
}


/**
 *      SecurityManager sm  = SecurityManager.getInstance();
        String toBe = "This is my encrypted message";
        String encryptedToBe = sm.encrypt(toBe);
        String decryptedToBe = sm.decrypt(encryptedToBe);
        System.out.println("Encrypted Message: " + toBe);
        System.out.println("Encrypted Message: " + encryptedToBe);
        System.out.println("Decrypted Message: " + decryptedToBe);
 */
