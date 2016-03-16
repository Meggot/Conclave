/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.interfaces;

import conclave.model.ConclaveRoom;
import java.awt.Dimension;
import java.net.InetSocketAddress;
import java.rmi.RemoteException;

/**
 *
 * @author BradleyW
 */
public interface IConferenceRoom extends ConclaveRoom {
    
    public Dimension getDimension() throws RemoteException;
    public boolean isStreaming() throws RemoteException;
    public void startBroadcasting(String username, InetSocketAddress networkloc, Dimension d) throws RemoteException;
    public void stopBroadcasting(String streamerName) throws RemoteException;
    public InetSocketAddress getStreamerIp() throws RemoteException;
    public String getStreamerName() throws RemoteException;
}
