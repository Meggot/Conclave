/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.interfaces;

import com.github.sarxos.webcam.Webcam;
import conclave.model.ConnectionsLog;
import conclave.db.Account;
import conclave.model.Announcement;
import conclave.model.ConclaveRoom;
import conclave.model.Message;
import java.awt.Dimension;
import java.net.InetSocketAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author BradleyW
 */
public interface UserInterface extends Remote {
    
    public boolean inRoom() throws RemoteException;

    public boolean joinRoom(String roomName) throws RemoteException;

    public boolean joinRoom(String entryName, String password) throws RemoteException;

    public boolean hasPassword(String entryname) throws RemoteException;

    public void leaveRoom() throws RemoteException;

    public String exportChatLog() throws RemoteException;

    public void postMessage(String message) throws RemoteException;

    public void updateChatLog(Message message) throws RemoteException; //Called by ConclaveRoom

    public Account getAccount() throws RemoteException;

    public void updateConnections(ConnectionsLog newLog) throws RemoteException; //Called

    public ConnectionsLog viewAllConnections() throws RemoteException;

    public void sendPrivateMessage(String message, String recipientID) throws RemoteException;

    public void recievePrivateMessage(Message message) throws RemoteException;

    public void leaveServer() throws RemoteException;

    public boolean isConnected() throws RemoteException;

    public void connect() throws RemoteException;

    public void disconnect() throws RemoteException;

    public boolean hasConnectionsUpdated() throws RemoteException;

    public String getUsername() throws RemoteException;

    public String getActiveRoomName() throws RemoteException;

    public int getRoomType() throws RemoteException;

    public LinkedList<Message> getChatlogUpdates(int lstMsgRecieved) throws RemoteException;

    public int getLastMessageLine() throws RemoteException;

    public int getType() throws RemoteException;

    public List<Announcement> getFrontpage() throws RemoteException;

    public void setFrontpage(List<Announcement> frontPage) throws RemoteException;

    public void updateFrontpage(String username, String announcment) throws RemoteException;

    public void updateStreamer() throws RemoteException;
    
    public boolean hasFrontpageUpdated() throws RemoteException;

    public Dimension getConferenceDimension() throws RemoteException;

    public String getStreamerName() throws RemoteException;
    
    public void stopBroadcasting() throws RemoteException;
    
    public boolean isConferenceStreaming() throws RemoteException;
    
    public InetSocketAddress getStreamerLocation() throws RemoteException;
    
    public void broadcastToConference(InetSocketAddress loc, Dimension d) throws RemoteException;
    
    public boolean hasStreamerUpdated() throws RemoteException;
}
