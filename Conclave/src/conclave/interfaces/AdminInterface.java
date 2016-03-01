/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.interfaces;

import java.rmi.RemoteException;

/**
 *
 * @author BradleyW
 */
public interface AdminInterface extends UserInterface{
    public void addRoom(String roomName, int roomtype) throws RemoteException;
    public void mountOpenRoom(String roomName) throws RemoteException;
    public void removeRoom(String roomname) throws RemoteException;
    public void kickUser(String username) throws RemoteException;
    public void banUser(String username) throws RemoteException;
    public void unbanUser(String username) throws RemoteException;
}
