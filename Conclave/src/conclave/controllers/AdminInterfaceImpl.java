/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.controllers;

import conclave.ConclaveHandlers.RoomManager;
import conclave.db.Account;
import conclaveinterfaces.IAdminInterface;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Message;

/**AdminInterface implementation classe, this implements needed Admin functionality.
 * As the admin interface is also responsible for normal user interactions, we
 * extend the UserInterfaceImpl object which implements these interactions.
 *
 * @author BradleyW
 */
public class AdminInterfaceImpl extends UserInterfaceImpl implements IAdminInterface{
    
    //AdminInterface uses a RoomManager to modify rooms.
    private final RoomManager roomManager;
    //AdminInterface uses a ServerController objec to control Conclave.
    private final ServerController serverController;
    private Logger log = Logger.getLogger(AdminInterfaceImpl.class.getName());
    
    /**
     * Initiating a AdminInterfaceImpl requires a usual Account object, and the ServerController instance.
     * @param account
     * @param instance
     * @throws RemoteException 
     */
    public AdminInterfaceImpl(Account account, ServerController instance) throws RemoteException {
        super(account);
        roomManager = RoomManager.getInstance();
        serverController = instance;
    }

    /**
     * Adds an open room to the registry. It takes in a roomname and a roomtype,
     * the RoomManager is responsible for creating the right Object
     * @param roomname
     * @param roomType
     * @throws RemoteException 
     */
    @Override
    public void addRoom(String roomname, int roomType) throws RemoteException{
        try {
            roomManager.mountOpenRoom(roomname, roomType);
            serverController.updateAllClientsConnections(); //Update all clients connections of the new state change.
            log.log(Level.FINE, "Admin: {0} has mounted a new open room: {1}", new Object[] {this.getUsername(), roomname});
        } catch (RemoteException ex) {
           log.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Adds a room with a password to the Database and registry, it uses the same parameters
     * as the method it overloads, except a password.
     * @param roomname
     * @param roomType
     * @param roompassword
     * @throws RemoteException 
     */
    @Override
    public void addRoom(String roomname, int roomType, String roompassword) throws RemoteException{
        try {
            roomManager.createRoom(roomname, roompassword, roomType);
            roomManager.loadRoom(roomname);
            serverController.updateAllClientsConnections(); //Update all clients connections of the new state change.
            log.log(Level.FINE, "Admin: {0} has persisted a new room: {1}", new Object[] {this.getUsername(), roomname});
        } catch (RemoteException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Sends an Admin message to the username, it users the Message class as this is more interactive.
     * @param msg
     * @param username
     * @throws RemoteException 
     */
    @Override
    public void sendAdminMessage(Message msg, String username) throws RemoteException {
        serverController.alertUser(msg, username);
        log.log(Level.FINE, "Admin: {0} has sent a admin message [{1}]: to {2}.", new Object[] {this.getUsername(), msg, username});
    }

    /**
     * Removes a room from the registry.
     * 
     * @param roomname
     * @throws RemoteException 
     */
    @Override
    public void removeRoom(String roomname) throws RemoteException{
        try {
            roomManager.deleteRoom(roomname);
            serverController.updateAllClientsConnections(); //Update all clients connections of the new state change.
            log.log(Level.FINE, "Admin: {0} has removed room: {1}",new Object[] {this.getUsername(), roomname});
        } catch (RemoteException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Kicks a user from the server, and if the banned flag is set, it bans them from ever
     * making a connection under that username
     * 
     * @param username
     * @param banned
     * @throws RemoteException 
     */
    @Override
    public void kickUser(String username, boolean banned) throws RemoteException{
        try {
            roomManager.kickUser(username, banned);
            if (banned)
            {
                 serverController.banUser(username);
            }
            serverController.updateAllClientsConnections(); //Updates all clients connections
            log.log(Level.FINE, "Admin: {0} has kicked the user: {1}. Banned? ({3})",new Object[] {this.getUsername(), username, banned});
        } catch (RemoteException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Closes a room to new connections.
     * 
     * @param roomname
     * @throws RemoteException 
     */
    @Override
    public void closeRoom(String roomname) throws RemoteException{
        try {
            roomManager.closeRoom(roomname);
            serverController.updateAllClientsConnections();//Update all clients connections of the new state change.
            log.log(Level.FINE, "Admin: {0}, has close the room: {1}", new Object[] {this.getUsername(), roomname});
        } catch (RemoteException e)
        {
            log.log(Level.SEVERE, null, e);
        }
    }

    /**
     * Closes a room to new connections.
     * 
     * @param roomname
     * @throws RemoteException 
     */
    @Override
    public void openRoom(String roomname) throws RemoteException{
        try {
            roomManager.openRoom(roomname);
            serverController.updateAllClientsConnections();//Update all clients connections of the new state change.
            log.log(Level.FINE, "Admin: {0}, has opened the room: {1}", new Object[] {this.getUsername(), roomname});
        } catch (RemoteException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Unmutes a user, allowing them to chat and stream.
     * 
     * @param username
     * @throws RemoteException 
     */
    @Override
    public void uncensorUser(String username) throws RemoteException {
        roomManager.uncensorUser(username);
        serverController.alertUser(new Message(getUsername(), username, "You have been unmuted", 3), username);
    }

    /**
     * Mutes a user, preventing them from chatting and streaming.
     * @param username
     * @throws RemoteException 
     */
    @Override
    public void censorUser(String username) throws RemoteException{
        roomManager.censorUser(username);
        serverController.alertUser(new Message(getUsername(), username, "You have been muted", 3), username);
        log.log(Level.FINE, "Admin: {0}, has censored the user: {1}", new Object[] {this.getUsername(), username});
    }
    
    /**
     * Returns the type of the User object, this is used by the client interaction object.
     * UserInterface returns 1, and Admin returns 2.
     * @return
     * @throws RemoteException 
     */
    @Override
    public int getType() throws RemoteException
    {
        return 2;
    }

    /**
     * Returns a collection of all roomnames.
     * @return
     * @throws RemoteException 
     */
    @Override
    public List<String> getRoomNames() throws RemoteException {
        return roomManager.getAllLoadedRoomnames();
    }
    
    /**
     * Gets all supported room types, this is used by the Admin Interface view
     * to determine what rooms the admin can create.
     * @return
     * @throws RemoteException 
     */
    @Override
    public List<String> getSupportedRoomTypes() throws RemoteException {
        return roomManager.getAllSupportedRoomTypes();
    }
    
    /**
     * Returns a collection of all connected usernames to Conclave, used by the
     * Admin Interface view to determine connections they can control.
     * @return
     * @throws RemoteException 
     */
    @Override
    public List<String> getAllConnectedUsernames() throws RemoteException {
        return serverController.getAllConnectedUsernames() ;
    }
    
    /**
     * Posts an announcement to the Frontpage, this is only a String; but in the
     * future could use an Announcment object to allow Files to be downloaded.
     * @param msg
     * @throws RemoteException 
     */
    @Override
    public void postAnnouncment(String msg) throws RemoteException {
        serverController.updateFrontpage(getUsername(), msg);
    }

    /**
     * returns if the user is muted, this is used to determine if a controller should unmute/mute.
     * @param username
     * @return
     * @throws RemoteException 
     */
    @Override
    public boolean isMuted(String username) throws RemoteException {
        boolean mutedStatus = roomManager.isMuted(username);
        return mutedStatus;
    }
}
