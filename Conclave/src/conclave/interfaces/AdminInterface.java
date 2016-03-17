/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.interfaces;

import conclave.model.Message;
import java.rmi.RemoteException;
import java.util.List;

/**
 *
 * @author BradleyW
 */
public interface AdminInterface extends UserInterface{
    public void addRoom(String roomname, int roomType) throws RemoteException;
    public void addRoom(String roomname, int roomType, String roompassword) throws RemoteException;
    public void removeRoom(String roomname) throws RemoteException;
    public List<String> getRoomNames() throws RemoteException;
    public void kickUser(String username, boolean banned) throws RemoteException;
    public void closeRoom(String roomname) throws RemoteException;
    public void openRoom(String roomname) throws RemoteException;
    public void censorUser(String username) throws RemoteException;
    public void uncensorUser(String username) throws RemoteException;
    public boolean isMuted(String username) throws RemoteException;
    public List<String> getSupportedRoomTypes() throws RemoteException;
    public List<String> getAllConnectedUsernames() throws RemoteException;
    public void sendAdminMessage(Message message, String username) throws RemoteException;
    public void postAnnouncment(String msg) throws RemoteException;
}
