/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.interfaces;


import conclave.model.ConnectionsLog;
import conclave.db.Account;
import conclave.model.ConclaveRoom;
import conclave.model.Message;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author BradleyW
 */
public interface UserInterface extends Remote {

public boolean joinRoom (String roomName) throws RemoteException;
public boolean joinRoom(String entryName, String password) throws RemoteException;
public boolean hasPassword(String entryname) throws RemoteException;
public void leaveRoom() throws RemoteException;
public String exportChatLog() throws RemoteException;
public void postMessage(String message) throws RemoteException;
public void updateChatLog(Message message)throws RemoteException; //Called by ConclaveRoom
public Account getAccount()throws RemoteException;
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
public LinkedList<Message> getChatlogUpdates(int lstMsgRecieved) throws RemoteException;
public int getLastMessageLine() throws RemoteException;
}
