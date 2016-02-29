/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.rmiPolicy;

import java.rmi.RemoteException;

/**
 *
 * @author BradleyW
 */
class RegistryLoader {
private static boolean loaded=false;
public static void Load() throws RemoteException
{
    if (loaded) return;
        java.rmi.registry.LocateRegistry.createRegistry( 8090 );
        System.out.println( "Started Registry" );
        loaded=true;
}
private RegistryLoader() {}
}

