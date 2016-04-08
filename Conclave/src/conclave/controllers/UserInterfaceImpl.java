/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.controllers;

import com.github.sarxos.webcam.Webcam;
import conclave.ConclaveHandlers.RoomManager;
import conclave.db.Account;
import conclaveinterfaces.IConclaveRoom;
import conclaveinterfaces.IConferenceRoom;
import conclaveinterfaces.IUserInterface;
import java.awt.Dimension;
import java.net.InetSocketAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Announcement;
import model.Chatlog;
import model.ConnectionsLog;
import model.Message;
import model.ServerFrontpage;

/**User interface interacts with the server with general interactions
 * Also is responsible for providing state updates and view information.
 * Consider this the Controller in the MVC paradigm.
 *
 * @author BradleyW
 */
public class UserInterfaceImpl extends UnicastRemoteObject implements IUserInterface {

    //Model variables used to model the state
    private Account account;
    private ConnectionsLog connectionsLog;
    private IConclaveRoom activeRoom;
    private RoomManager roomListingsRoom;
    private final Chatlog chatLog;
    private ServerFrontpage ownFrontpage;
    
    //These state flags represent Server and Room state changes.
    //These are primarilly used by the GUI to build an updated view.
    private int lastMessageLine;
    private boolean inRoom;
    private boolean connected;
    private boolean connectionsUpdate;
    private boolean frontpageUpdated;
    private boolean activeWebcamUpdated;

    public UserInterfaceImpl(Account account) throws RemoteException {
        lastMessageLine = 0;
        chatLog = new Chatlog();
        this.account = account;
        connected = false;
        inRoom = false;
        connectionsUpdate = true;
        connectionsLog = new ConnectionsLog();
        roomListingsRoom = RoomManager.getInstance();
        ownFrontpage = new ServerFrontpage();
        frontpageUpdated = true;
    }

    @Override
    public String getActiveRoomName() throws RemoteException {
        if (inRoom) {
            return activeRoom.getRoomName();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasPassword(String entryname) throws RemoteException {
        return roomListingsRoom.hasPassword(entryname);
    }

    @Override
    public boolean joinRoom(String entryName, String password) throws RemoteException {
        boolean ok = false;
        try {
            if (connected && !inRoom && roomListingsRoom.hasPassword(entryName) && roomListingsRoom.valdiateRoom(entryName, password)) {
                final Registry registry = LocateRegistry.getRegistry(9807);
                activeRoom = (IConclaveRoom) registry.lookup(entryName);
                String username = account.getUsername();
                if (activeRoom.isOnline()) {
                    System.out.println("Reached.");
                    activeRoom.addUser(username, this);
                    connectionsLog = activeRoom.getAllConnections();
                    inRoom = true;
                    ok = true;
                    connectionsUpdate = true;
                    if (activeRoom.getType() == 2) {
                        activeWebcamUpdated = true;
                    }
                }
            }
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        return ok;
    }

    @Override
    public boolean joinRoom(String entryName) throws RemoteException {
        boolean ok = false;
        try {
            if (connected && !inRoom && !roomListingsRoom.hasPassword(entryName)) {
                final Registry registry = LocateRegistry.getRegistry(9807);
                activeRoom = (IConclaveRoom) registry.lookup(entryName);
                String username = account.getUsername();
                if (activeRoom.isOnline()) {
                    activeRoom.addUser(username, this);
                    connectionsLog = activeRoom.getAllConnections();
                    inRoom = true;
                    ok = true;
                    connectionsUpdate = true;
                    if (activeRoom.getType() == 2) {
                        activeWebcamUpdated = true;
                    }
                }
            }
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        return ok;
    }
    
    @Override
    public boolean inRoom() throws RemoteException {
        return inRoom;
    }

    @Override
    public void leaveRoom() throws RemoteException {
        if (inRoom && connected) {
            String username = account.getUsername();
            //String roomname = activeRoom.getRoomName();
            if (activeRoom.getType() == 2) {
                stopBroadcasting();
            }
            activeRoom.removeUser(username);
            activeRoom = null;
            inRoom = false;
            connectionsLog = roomListingsRoom.returnRooms();
            connectionsUpdate = true;
            frontpageUpdated = true;
        }
    }

    @Override
    public void stopBroadcasting() {
        try {
            if (activeRoom.getType() == 2) {
                String username = account.getUsername();
                IConferenceRoom confRoom = (IConferenceRoom) activeRoom;
                if (confRoom.getStreamerName() != null) {
                    if (confRoom.getStreamerName().equals(username)) {
                        confRoom.stopBroadcasting(username);
                    }
                }
            }
        } catch (RemoteException ex) {
            Logger.getLogger(UserInterfaceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void postMessage(String message) throws RemoteException {
        if (inRoom && connected) {
            String username = getUsername();
            String roomId = activeRoom.getRoomName();
            Message newMessage;
            newMessage = new Message(username, roomId, message, 1);
            activeRoom.postMessage(newMessage);
        }
    }

    @Override
    public void updateChatLog(Message message) throws RemoteException {
        if (message != null) {
            chatLog.addMessage(message);
            lastMessageLine++;
        } else {
            System.out.println("Message is null.");
        }
    }
    
    @Override
    public void updateConnections(ConnectionsLog newLog) throws RemoteException {
        connectionsLog = newLog;
        connectionsUpdate = true;
    }

    @Override
    public ConnectionsLog viewAllConnections() throws RemoteException {
        connectionsUpdate = false;
        if (!inRoom) {
            return roomListingsRoom.returnRooms();
        } else {
            return activeRoom.getAllConnections();
        }
    }

    @Override
    public void sendPrivateMessage(String message, String recipientID) throws RemoteException {
        if (connected && inRoom) {
            Message privateSentMessage = new Message(account.getUsername(), recipientID, message, 4);
            activeRoom.whisper(privateSentMessage);
        }
    }

    @Override
    public void recievePrivateMessage(Message message) throws RemoteException {
        chatLog.addMessage(message);
        lastMessageLine++;
    }

    @Override
    public void leaveServer() throws RemoteException {
        if (inRoom) {
            leaveRoom();
        }
        disconnect();
    }

    @Override
    public boolean isConnected() throws RemoteException {
        return connected;
    }

    @Override
    public void connect() throws RemoteException {
        connected = true;
    }

    @Override
    public void disconnect() throws RemoteException {
        connected = false;
    }

    @Override
    public String getUsername() throws RemoteException {
        return account.getUsername();
    }

    @Override
    public String exportChatLog() {
       return chatLog.viewEntries();
    }

    @Override
    public LinkedList<Message> getChatlogUpdates(int lstMsgRecieved) throws RemoteException {
        LinkedList<Message> returnArray = chatLog.getAllEntriesAfter(lstMsgRecieved);
        return returnArray;
    }

    @Override
    public int getLastMessageLine() throws RemoteException {
        return lastMessageLine;
    }

    @Override
    public boolean hasConnectionsUpdated() throws RemoteException {
        return connectionsUpdate;
    }

    @Override
    public int getRoomType() throws RemoteException {
        return activeRoom.getType();
    }

    @Override
    public int getType() throws RemoteException {
        return 1;
    }

    @Override
    public List<Announcement> getFrontpage() throws RemoteException {
        frontpageUpdated = false;
        return new ArrayList(ownFrontpage.getFrontpage());
    }

    @Override
    public void setFrontpage(List<Announcement> frontPage) throws RemoteException {
        ownFrontpage.setFrontpage(frontPage);
        if (!inRoom) {
            frontpageUpdated = true;
        }
    }

    @Override
    public void updateFrontpage(String username, String announcment) throws RemoteException {
        ownFrontpage.addNewAnnouncment(username, announcment);
        if (!inRoom) {
            frontpageUpdated = true;
        }
    }

    @Override
    public boolean hasFrontpageUpdated() throws RemoteException {
        return frontpageUpdated;
    }

    @Override
    public Dimension getConferenceDimension() throws RemoteException {
        Dimension returnDimension = null;
        if (activeRoom.getType() == 2) {
            IConferenceRoom conference = (IConferenceRoom) activeRoom;
            returnDimension = conference.getDimension();
        }
        return returnDimension;
    }

    @Override
    public boolean isConferenceStreaming() throws RemoteException {
        boolean isStreaming = false;
        if (activeRoom.getType() == 2) {
            IConferenceRoom conference = (IConferenceRoom) activeRoom;
            if (conference.isStreaming()) {
                isStreaming = true;
            }
        }
        return isStreaming;
    }

    @Override
    public InetSocketAddress getStreamerLocation() throws RemoteException {
        if (activeRoom.getType() == 2) {
            IConferenceRoom conference = (IConferenceRoom) activeRoom;
            return conference.getStreamerIp();
        }
        return null;
    }

    @Override
    public void broadcastToConference(InetSocketAddress loc, Dimension d) throws RemoteException {
        if (activeRoom.getType() == 2) {
            IConferenceRoom conference = (IConferenceRoom) activeRoom;
            if (!conference.isStreaming()) {
                conference.startBroadcasting(getUsername(), loc, d);
                activeWebcamUpdated = true;
            }
        }
    }

    @Override
    public boolean hasStreamerUpdated() throws RemoteException {
        boolean hasUpdated = activeWebcamUpdated;
        activeWebcamUpdated = false;
        return hasUpdated;
    }

    @Override
    public String getStreamerName() throws RemoteException {
        String name = null;
        if (activeRoom.getType() == 2) {
            IConferenceRoom conference = (IConferenceRoom) activeRoom;
            if (conference.isStreaming()) {
                name = conference.getStreamerName();
            }
        }
        return name;
    }

    @Override
    public void updateStreamer() throws RemoteException {
        activeWebcamUpdated = true;
    }
}
