/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.interfaces;

import conclave.db.Room;
import conclave.model.ConclaveRoom;
import conclave.model.ConnectionsLog;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author BradleyW
 */
public interface RoomManagerInterface extends Remote {
    
    public boolean createRoom(String roomname, String password, int roomType) throws RemoteException;
    public boolean mountOpenRoom(String roomname, int roomType) throws RemoteException;
    public boolean deleteRoom(String roomname) throws RemoteException;
    
    public boolean hasPassword(String roomname) throws RemoteException;
    public boolean valdiateRoom(String username, String password) throws RemoteException;
    public ConnectionsLog returnRooms() throws RemoteException;
    public ConclaveRoom getConclaveRoom(String roomname) throws RemoteException;
    public Room getRoom(String roomname) throws RemoteException;
    
    public boolean loadRoom(String roomName) throws RemoteException;
    public void stopRoom(String roomname) throws RemoteException;
    
    public void kickUser(String username, String roomname) throws RemoteException;

    public boolean isARoom(String roomname) throws RemoteException;
}
