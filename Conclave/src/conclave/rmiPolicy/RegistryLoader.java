/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.rmiPolicy;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

/**
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
        System.out.println("Started Registry");
        loaded = true;
    }

    private RegistryLoader() {
    }
}
