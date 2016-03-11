/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.interfaces;

import conclave.ConclaveHandlers.RoomManager;
import conclave.ServerController;
import conclave.db.Account;
import conclave.model.Message;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BradleyW
 */
public class AdminInterfaceImpl extends UserInterfaceImpl implements AdminInterface{
    
    private RoomManager roomManager;
    private ServerController serverController;
    
    public AdminInterfaceImpl(Account account) throws RemoteException {
        super(account);
        roomManager = RoomManager.getInstance();
        serverController = ServerController.getInstance();
    }

    @Override
    public void addRoom(String roomname, int roomType) throws RemoteException{
        try {
            roomManager.mountOpenRoom(roomname, roomType);
            roomManager.loadRoom(roomname);
        } catch (RemoteException ex) {
            Logger.getLogger(AdminInterfaceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void addRoom(String roomname, int roomType, String roompassword) throws RemoteException{
        try {
            roomManager.createRoom(roomname, roompassword, roomType);
            roomManager.loadRoom(roomname);
        } catch (RemoteException ex) {
            Logger.getLogger(AdminInterfaceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void sendAdminMessage(Message msg, String username) throws RemoteException {
        serverController.alertUser(msg, username);
    }

    @Override
    public void removeRoom(String roomname) throws RemoteException{
        try {
            roomManager.deleteRoom(roomname);
        } catch (RemoteException ex) {
            Logger.getLogger(AdminInterfaceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void kickUser(String username, boolean banned) throws RemoteException{
        try {
            roomManager.kickUser(username, banned);
        } catch (RemoteException ex) {
            Logger.getLogger(AdminInterfaceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void closeRoom(String roomname) throws RemoteException{
        try {
            roomManager.closeRoom(roomname);
        } catch (RemoteException e)
        {
            
        }
    }

    @Override
    public void openRoom(String roomname) throws RemoteException{
        try {
            roomManager.openRoom(roomname);
        } catch (RemoteException ex) {
            Logger.getLogger(AdminInterfaceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void censorUser(String username) throws RemoteException{
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public int getType() throws RemoteException
    {
        return 2;
    }

    @Override
    public List<String> getRoomNames() throws RemoteException {
        return roomManager.getAllRoomnames();
    }
    
    @Override
    public List<String> getSupportedRoomTypes() throws RemoteException {
        return roomManager.getAllSupportedRoomTypes();
    }
    
    @Override
    public List<String> getAllConnectedUsernames() throws RemoteException {
        return serverController.getAllConnectedUsernames() ;
    }
    
    @Override
    public void postAnnouncment(String msg) throws RemoteException {
        serverController.updateFrontpage(getUsername(), msg);
    }
}
