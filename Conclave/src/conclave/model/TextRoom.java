/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.model;

import conclave.interfaces.UserInterface;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

/**
 *
 * @author BradleyW
 */
public class TextRoom extends UnicastRemoteObject implements ConclaveRoom {
    
    public String roomName;
    public int roomLimit;
    public HashMap<String, UserInterface> roomConnections;
    public ConnectionsLog connectionsLog;

    public boolean visiblity;
    public boolean online;
    public int roomType;
    
    public TextRoom(String iroomName) throws RemoteException{
        this.roomName = iroomName;
        this.visiblity = false;
        this.online = false;
        roomConnections = new HashMap<>();
        connectionsLog = new ConnectionsLog();
        roomLimit = 20;
        roomType = 1;
    }
    /**
     * CONTROL METHODS
     */
    @Override
    public void startRoom() throws RemoteException
    {
       online = true;
    }
    
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
    public void addUser(String username, UserInterface user) throws RemoteException
    {
        if (roomConnections.size() <= roomLimit)
        {
            roomConnections.put(username, user);
            connectionsLog.addConnection(username, "User");
            String msg = "You have joined room: " + roomName;
            Message welcomeMessage = new Message(roomName, username, msg, 2);
            whisper(welcomeMessage);
            updateAllClientsConnections();
        }
    }
    
    @Override
    public void removeUser(String username) throws RemoteException
    {
        String msg = "You have been removed from the room";
        Message removeMessage = new Message(roomName, username, msg, 2);
        whisper(removeMessage);
        roomConnections.remove(username);
        connectionsLog.removeConnection(username);
        updateAllClientsConnections();
    }

    @Override
    public void postMessage(Message message)throws RemoteException
    {
        updateAllClientsChatlog(message);
    }
    @Override
    public void setLimit(int limit)throws RemoteException
    {
        this.roomLimit = limit;
    }
    
    @Override
    public void updateAllClientsChatlog(Message msg) throws RemoteException
    {
            for (UserInterface ui : roomConnections.values())
            {
                try {
                    ui.updateChatLog(msg);
                } catch (IOException e)
                {
                    String username = ui.getUsername();
                    roomConnections.remove(username);
                    connectionsLog.removeConnection(username);
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
    public boolean isVisiblity() throws RemoteException{
        return visiblity;
    }

    @Override
    public boolean isOnline() throws RemoteException{
        return online;
    }
    @Override
    public String getInfo() throws RemoteException {
        return "[TextRoom] " + roomConnections.size() + "/" + roomLimit + " {" + visiblity + "}";
    }
    @Override   
    public ConnectionsLog getAllConnections() throws RemoteException
    {
        return connectionsLog;
    }
    
    @Override
    public void updateAllClientsConnections() throws RemoteException
    {
        for (UserInterface ui : roomConnections.values())
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
    public void whisper(Message msg) throws RemoteException 
    {
        String recipientUsername = msg.getRecipientId();
        UserInterface ui = roomConnections.get(recipientUsername);
        ui.updateChatLog(msg);
    }
    
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
        visiblity = true;
        online = true;
    }

    @Override
    public void openRoom() throws RemoteException {
        visiblity = true;
        online = true;
    }
}
