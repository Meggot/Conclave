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
    
    private String roomName;
    private int roomLimit;
    private HashMap<String, UserInterface> roomConnections;
    private ConnectionsLog connectionsLog;

    private boolean visiblity;
    private boolean online;
    
    public TextRoom(String iroomName) throws RemoteException{
        this.roomName = iroomName;
        this.visiblity = false;
        this.online = false;
        roomConnections = new HashMap<>();
        connectionsLog = new ConnectionsLog();
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
        roomConnections.put(username, user);
        connectionsLog.addConnection(username, "User");
        updateAllClientsConnections();
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
        return "TextRoom: [" + roomName + "] " + roomConnections.size() + "/" + roomLimit;
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
}
