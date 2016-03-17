/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

/**
 *
 * @author BradleyW
 */
public class TestClass {
    
    public static void main(String args[]) throws RemoteException, UnknownHostException
    {
        ServerController testClave = ServerController.getInstance();
        testClave.addAdmin("MeggotZolu");
        testClave.startServer();
    }
}
