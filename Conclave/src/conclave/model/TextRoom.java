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
 *
 * @author BradleyW
 */
public class TextRoom extends UnicastRemoteObject implements IConclaveRoom {
    
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
     * CONTROL METHODS
     * @throws java.rmi.RemoteException
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
    @Override
    public void setLimit(int limit)throws RemoteException
    {
        this.roomLimit = limit;
    }
    
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

    @Override
    public String getRoomName() throws RemoteException{
        return roomName;
    }

    @Override
    public int getRoomLimit() throws RemoteException{
        return roomLimit;
    }

    @Override
    public boolean isOnline() throws RemoteException{
        return online;
    }
    @Override
    public String getInfo() throws RemoteException {
        return "[TextRoom] " + currentConnections + "/" + roomLimit + " {" + online + "}";
    }
    @Override   
    public ConnectionsLog getAllConnections() throws RemoteException
    {
        return connectionsLog;
    }
    
    @Override
    public void updateAllClientsConnections() throws RemoteException
    {
        for (IUserInterface ui : roomConnections.values())
        {
            try {
                ui.updateConnections(connectionsLog);
            } catch (RemoteException e)
            {
                removeUser(ui.getUsername());
            }
        }
     
    }
    
    @Override
    public void addCensoredUser(String username) throws RemoteException
    {
        censorList.add(username);
    }
    
    @Override
    public void uncensorUser(String username) throws RemoteException
    {
        censorList.remove(username);
    }

    @Override
    public void whisper(Message msg) throws RemoteException 
    {
        String recipientUsername = msg.getRecipientId();
        IUserInterface ui = roomConnections.get(recipientUsername);
        ui.updateChatLog(msg);
    }
    
    
    @Override
    public void kickUser(String username) throws RemoteException 
    {
        IUserInterface ui = roomConnections.get(username);
        ui.leaveRoom();
    }
    /**
     *
     * @return
     * @throws RemoteException
     */
    @Override
    public int getType() throws RemoteException
    {
        return roomType;
    }

    @Override
    public boolean hasUser(String username) throws RemoteException {
        boolean has = false;
        if (roomConnections.containsKey(username))
        {
            has = true;
        }
        return has;
    }

    @Override
    public void closeRoom() throws RemoteException {
        online = false;
    }

    @Override
    public void openRoom() throws RemoteException {
        online = true;
    }
}
