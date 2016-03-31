/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.model;

import conclaveinterfaces.IConferenceRoom;
import conclaveinterfaces.IUserInterface;
import model.Message;
import java.awt.Dimension;
import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**Conference room will host Conclave Conference rooms, this room is different from textroom in that
 * it provides an IP and Socket of the streamer; as well as a Dimension. It extends a text room as 
 * it provides the same basic functionality but adds to it.
 *
 * @author BradleyW
 */
public class ConferenceRoom extends TextRoom implements IConferenceRoom {

    //Streamer Variables.
    private String streamerName; // streamer name
    private boolean streaming; //is a user currently streaming
    private Dimension streamingDimension; //streaming dimension
    private InetSocketAddress streamerIp; //location of streaming network
    
    //Logger.
    private static final Logger log = Logger.getLogger(ConferenceRoom.class.getName());

    /**
     * Conference Room is initiated with a name, like a textroom.
     * @param iroomName
     * @throws RemoteException 
     */
    public ConferenceRoom(String iroomName) throws RemoteException {
        super(iroomName);
        this.roomType = 2; //The type is 2, TextRoom is 1.
        streaming = false; 
        streamerName = "";
    }

    /**
     * Returns the streamer socketIP of the current broadcaster.
     * @return
     * @throws RemoteException 
     */
    @Override
    public InetSocketAddress getStreamerIp() throws RemoteException {
        if (streaming) {
            return streamerIp;
        }
        return null;
    }

    /**
     * This is used by a user to start streaming to a room, it sets a couple of variables for listeners to use
     * to recieve the video stream. Muted users cannot stream, however.
     * @param username
     * @param networkloc
     * @param d
     * @throws RemoteException 
     */
    @Override
    public void startBroadcasting(String username, InetSocketAddress networkloc, Dimension d) throws RemoteException {
        if (censorList.contains(username)) {
            whisper(new Message(roomName, username, "Muted users cannot stream.", 2));
        } else if (!streaming) {
            this.streamerIp = networkloc;
            this.streamerName = username;
            this.streamingDimension = d;
            this.streaming = true;
            updateActiveListeners();
            log.log(Level.INFO, "User: {0} has started to broadcast on room: {1}", new Object[]{username, roomName});
        }
    }

    /**
     * Updates all the streamer updated flag, this forces the view to update the streamer panel.
     * Used for stopping or starting a broadcast.
     * @throws RemoteException 
     */
    public void updateActiveListeners() throws RemoteException {
        for (IUserInterface ui : roomConnections.values()) {
            ui.updateStreamer();
        }
    }

    /**
     * Stops a broadcast, by setting all the streamer variables to null.
     * @param streamerName
     * @throws RemoteException 
     */
    @Override
    public void stopBroadcasting(String streamerName) throws RemoteException {
        if (streamerName.equals(this.streamerName)) {
            streamingDimension = null;
            this.streamerName = null;
            streamerIp = null;
            streaming = false;
            updateActiveListeners();
            log.log(Level.INFO, "User: {0} has stopped broadcasting at room: {1}", new Object[]{streamerName, roomName});
        }
    }

    /**
     * Gets a info view, this is used to display connection entries.
     * @return
     * @throws RemoteException 
     */
    @Override
    public String getInfo() throws RemoteException {
        return "[ConferenceRoom] " + currentConnections + "/" + roomLimit + " {" + online + "}";
    }

    /**
     * Returns if the room currently has an active streamer.
     * @return
     * @throws RemoteException 
     */
    @Override
    public boolean isStreaming() throws RemoteException {
        return streaming;
    }

    /**
     * Returns the dimension of the currently active stream, used for listeners
     * to know how wide to paint a panel.
     * @return
     * @throws RemoteException 
     */
    @Override
    public Dimension getDimension() throws RemoteException {
        if (streaming) {
            return streamingDimension;
        }
        return null;
    }

    /**
     * Returns the current streamer name, used for the view.
     * @return
     * @throws RemoteException 
     */
    @Override
    public String getStreamerName() throws RemoteException {
        if (streaming) {
            return streamerName;
        }
        return null;
    }
}
