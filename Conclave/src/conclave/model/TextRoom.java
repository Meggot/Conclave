/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.model;

import model.ConnectionsLog;
import model.Message;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import conclaveinterfaces.IConclaveRoom;
import conclaveinterfaces.IUserInterface;

/**
 *TextRoom, provides the basic room interactions. All other rooms should extend this,
 * as it provides ChatLog + ConnectionLog functionality.
 * @author BradleyW
 */
public class TextRoom extends UnicastRemoteObject implements IConclaveRoom {
    
    //Basic room variables.
    public String roomName;
    public int roomLimit;
    public int currentConnections;
    
    public HashMap<String, IUserInterface> roomConnections;
    public ConnectionsLog connectionsLog;
    public ArrayList<String> censorList;
    
    public boolean online;
    public int roomType;
    
    private final Logger log = Logger.getLogger(TextRoom.class.getName());
    
    public TextRoom(String iroomName) throws RemoteException{
        this.roomName = iroomName;
        this.online = true;
        roomConnections = new HashMap<>();
        censorList = new ArrayList<>();
        connectionsLog = new ConnectionsLog();
        roomLimit = 20;
        roomType = 1;
        currentConnections = 0;
    }
    
    /**
     * Stops a room, preventing new connections and then kicking everyone from the room.
     * @throws RemoteException 
     */
    @Override
    public void stopRoom() throws RemoteException
    {
        online = false;
        System.out.println("Stopping room.");
        for (String username : roomConnections.keySet())
        {
            removeUser(username);
        }
    }
    
    /**
     * Adds a user to the room, used when joining a room.
     * @param username
     * @param user
     * @throws RemoteException 
     */
    @Override
    public void addUser(String username, IUserInterface user) throws RemoteException
    {
        if (currentConnections < roomLimit)
        {
            roomConnections.put(username, user);
            connectionsLog.addConnection(username, "User");
            String msg = "You have joined room: " + roomName;
            Message welcomeMessage = new Message(roomName, username, msg, 2);
            whisper(welcomeMessage);
            updateAllClientsConnections();
            log.log(Level.INFO, "User: {0} has joined the room: {1}", new Object[] {username, roomName});
            currentConnections++;
        }
    }
    
    /**
     * Removes a user from the room, used when kicking or leaving a room.
     * @param username
     * @throws RemoteException 
     */
    @Override
    public void removeUser(String username) throws RemoteException
    {
        String msg = "You have been removed from the room";
        Message removeMessage = new Message(roomName, username, msg, 2);
        IUserInterface ui = roomConnections.get(username);
        if (ui!=null)
        {
            ui.updateChatLog(removeMessage);
        }
        roomConnections.remove(username);
        connectionsLog.removeConnection(username);
        updateAllClientsConnections();
        log.log(Level.INFO, "User: {0} has been removed from the room: {1}", new Object[] {username, roomName});
        currentConnections--;
    }

    /**
     * Posts a chatlog message to all users in the room, if they are not muted.
     * 
     * @param message
     * @throws RemoteException 
     */
    @Override
    public void postMessage(Message message)throws RemoteException
    {
        if (!censorList.contains(message.getSenderName()))
        {
            updateAllClientsChatlog(message);
        } else {
            whisper(new Message(roomName, message.getSenderName(), "You are currently muted", 2));
        }
    }
    /**
     * Edit the roomlimit of a room. No longer used in implementation.
     * @param limit
     * @throws RemoteException 
     */
    @Override
    public void setLimit(int limit)throws RemoteException
    {
        this.roomLimit = limit;
    }
    
    /**
     * Updates all the chatlogs of users with the message, used
     * during chatlog updates, or system messages.
     * 
     * @param msg
     * @throws RemoteException 
     */
    @Override
    public void updateAllClientsChatlog(Message msg) throws RemoteException
    {
            for (IUserInterface ui : roomConnections.values())
            {
                try {
                    ui.updateChatLog(msg);
                } catch (IOException e)
                {
                    String username = ui.getUsername();
                    removeUser(username);
                    updateAllClientsConnections();
                    e.printStackTrace();
                }   
            }
    }

    /**
     * Returns the roomname.
     * 
     * @return
     * @throws RemoteException 
     */
    @Override
    public String getRoomName() throws RemoteException{
        return roomName;
    }

    /**
     * Returns the roomlimit.
     * 
     * @return
     * @throws RemoteException 
     */
    @Override
    public int getRoomLimit() throws RemoteException{
        return roomLimit;
    }

    /**
     * Returns a boolean based on if the room is open/closed.
     * @return
     * @throws RemoteException 
     */
    @Override
    public boolean isOnline() throws RemoteException{
        return online;
    }
    
    /**
     * Returns a connection info, used to display the room in the connections log.
     * This should be overridden by any future implementations of rooms.
     * @return
     * @throws RemoteException 
     */
    @Override
    public String getInfo() throws RemoteException {
        return "[TextRoom] " + currentConnections + "/" + roomLimit + " {" + online + "}";
    }
    
    /**
     * Returns a connectionlog of all users in the room, used when joining a room to get
     * all the users.
     * 
     * @return
     * @throws RemoteException 
     */
    @Override   
    public ConnectionsLog getAllConnections() throws RemoteException
    {
        return connectionsLog;
    }
    
    /**
     * Updates all clients connections, used when removing a player or a new player
     * joins the room.
     * @throws RemoteException 
     */
    @Override
    public void updateAllClientsConnections() throws RemoteException
    {
        for (IUserInterface ui : roomConnections.values())
        {
            try {
                ui.updateConnections(connectionsLog);
            } catch (RemoteException e)
            {
                removeUser(ui.getUsername()); //also as a failsafe, determines if the user is offline
            }
        }
     
    }
    
    /**
     * Mutes a user by adding them to the CensorList.
     * @param username
     * @throws RemoteException 
     */
    @Override
    public void addCensoredUser(String username) throws RemoteException
    {
        censorList.add(username);
    }
    
    /**
     * Unmutes a user by removing them from the censor list.
     * @param username
     * @throws RemoteException 
     */
    @Override
    public void uncensorUser(String username) throws RemoteException
    {
        if (censorList.contains(username))
        {
            censorList.remove(username);
        }
    }

    /**
     * Whispers a user with a private message.
     * @param msg
     * @throws RemoteException 
     */
    @Override
    public void whisper(Message msg) throws RemoteException 
    {
        String recipientUsername = msg.getRecipientId();
        IUserInterface ui = roomConnections.get(recipientUsername);
        ui.updateChatLog(msg);
    }
    
    
    /**
     * Kicks the user from the room.
     * @param username
     * @throws RemoteException 
     */
    @Override
    public void kickUser(String username) throws RemoteException 
    {
        IUserInterface ui = roomConnections.get(username);
        ui.leaveRoom(); //This calls the removeUser method, so no need to call it again.
    }
    
    /**returns the type, for textroom this is 1.
     *
     * @return
     * @throws RemoteException
     */
    @Override
    public int getType() throws RemoteException
    {
        return roomType;
    }

    /**
     * Does the room contain this username?
     * 
     * @param username
     * @return
     * @throws RemoteException 
     */
    @Override
    public boolean hasUser(String username) throws RemoteException {
        boolean has = false;
        if (roomConnections.containsKey(username))
        {
            has = true;
        }
        return has;
    }

    /**
     * Closes a room to new connections, does not interfere with current connections
     * 
     * @throws RemoteException 
     */
    @Override
    public void closeRoom() throws RemoteException {
        online = false;
    }

    /**
     * Opens a room to new connections.
     * 
     * @throws RemoteException 
     */
    @Override
    public void openRoom() throws RemoteException {
        online = true;
    }
}
