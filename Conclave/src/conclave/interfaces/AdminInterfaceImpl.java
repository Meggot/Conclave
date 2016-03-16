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
    
    private final RoomManager roomManager;
    private final ServerController serverController;
    private Logger log = Logger.getLogger(AdminInterfaceImpl.class.getName());
    
    public AdminInterfaceImpl(Account account, ServerController instance) throws RemoteException {
        super(account);
        roomManager = RoomManager.getInstance();
        serverController = instance;
    }

    @Override
    public void addRoom(String roomname, int roomType) throws RemoteException{
        try {
            roomManager.mountOpenRoom(roomname, roomType);
            roomManager.loadRoom(roomname);
            log.log(Level.FINE, "Admin: {0} has mounted a new open room: {1}", new Object[] {this.getUsername(), roomname});
        } catch (RemoteException ex) {
           log.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void addRoom(String roomname, int roomType, String roompassword) throws RemoteException{
        try {
            roomManager.createRoom(roomname, roompassword, roomType);
            roomManager.loadRoom(roomname);
            log.log(Level.FINE, "Admin: {0} has persisted a new room: {1}", new Object[] {this.getUsername(), roomname});
        } catch (RemoteException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void sendAdminMessage(Message msg, String username) throws RemoteException {
        serverController.alertUser(msg, username);
        log.log(Level.FINE, "Admin: {0} has sent a admin message [{1}]: to {2}.", new Object[] {this.getUsername(), msg, username});
    }

    @Override
    public void removeRoom(String roomname) throws RemoteException{
        try {
            roomManager.deleteRoom(roomname);
            log.log(Level.FINE, "Admin: {0} has removed room: {1}",new Object[] {this.getUsername(), roomname});
        } catch (RemoteException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void kickUser(String username, boolean banned) throws RemoteException{
        try {
            roomManager.kickUser(username, banned);
            serverController.banUser(username);
            log.log(Level.FINE, "Admin: {0} has kicked the user: {1}. Banned? ({3})",new Object[] {this.getUsername(), username, banned});
        } catch (RemoteException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void closeRoom(String roomname) throws RemoteException{
        try {
            roomManager.closeRoom(roomname);
            log.log(Level.FINE, "Admin: {0}, has close the room: {1}", new Object[] {this.getUsername(), roomname});
        } catch (RemoteException e)
        {
            log.log(Level.SEVERE, null, e);
        }
    }

    @Override
    public void openRoom(String roomname) throws RemoteException{
        try {
            roomManager.openRoom(roomname);
            log.log(Level.FINE, "Admin: {0}, has opened the room: {1}", new Object[] {this.getUsername(), roomname});
        } catch (RemoteException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void censorUser(String username) throws RemoteException{
        roomManager.censorUser(username);
        log.log(Level.FINE, "Admin: {0}, has censored the user: {1}", new Object[] {this.getUsername(), username});
    }
    
    @Override
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
