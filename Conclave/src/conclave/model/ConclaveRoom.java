/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.model;

import conclave.interfaces.UserInterface;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author BradleyW
 */
public interface ConclaveRoom extends Remote{
    
    public void startRoom() throws RemoteException;
    public void stopRoom() throws RemoteException;
    public void addUser(String userId, UserInterface user) throws RemoteException;
    public void removeUser(String userId) throws RemoteException;
    public void postMessage(Message message) throws RemoteException;
    public void setLimit(int limit) throws RemoteException;
    public void updateAllClientsChatlog(Message msg) throws RemoteException;
    public String getRoomName() throws RemoteException;
    public int getRoomLimit() throws RemoteException;
    public boolean isVisiblity() throws RemoteException;
    public boolean isOnline() throws RemoteException;
    public String getInfo() throws RemoteException;
    public int getType() throws RemoteException;
    public void updateAllClientsConnections() throws RemoteException;
    public ConnectionsLog getAllConnections() throws RemoteException;
    public void whisper(Message msg) throws RemoteException;
}
