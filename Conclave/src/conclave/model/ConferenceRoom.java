/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.model;

import conclave.interfaces.IConferenceRoom;
import conclave.interfaces.UserInterface;
import java.awt.Dimension;
import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BradleyW
 */
public class ConferenceRoom extends TextRoom implements IConferenceRoom {

    private String streamerName;
    private boolean streaming;
    private Dimension streamingDimension;
    private InetSocketAddress streamerIp;
    private static final Logger log = Logger.getLogger(ConferenceRoom.class.getName());

    public ConferenceRoom(String iroomName) throws RemoteException {
        super(iroomName);
        this.roomType = 2;
        streaming = false;
        streamerName = "";
    }

    @Override
    public InetSocketAddress getStreamerIp() throws RemoteException {
        if (streaming) {
            return streamerIp;
        }
        return null;
    }

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

    public void updateActiveListeners() throws RemoteException {
        for (UserInterface ui : roomConnections.values()) {
            ui.updateStreamer();
        }
    }

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

    @Override
    public String getInfo() throws RemoteException {
        return "[ConferenceRoom] " + currentConnections + "/" + roomLimit + " {" + online + "}";
    }

    @Override
    public boolean isStreaming() throws RemoteException {
        return streaming;
    }

    @Override
    public Dimension getDimension() throws RemoteException {
        if (streaming) {
            return streamingDimension;
        }
        return null;
    }

    @Override
    public String getStreamerName() throws RemoteException {
        if (streaming) {
            return streamerName;
        }
        return null;
    }
}
