/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.rmiPolicy;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**This class create an RMI registry at port 9807. 
 *
 * @author BradleyW
 */
public class RegistryLoader {

    private static boolean loaded = false;

    public static void Load() throws RemoteException, UnknownHostException {
        if (loaded) {
            return;
        }
        System.setProperty("java.rmi.server.hostname",InetAddress.getLocalHost().getHostAddress());
        java.rmi.registry.LocateRegistry.createRegistry(9807);
        loaded = true;
    }
    
    private RegistryLoader() {
    }
}
