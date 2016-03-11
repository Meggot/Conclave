/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.model;

import conclave.interfaces.IConferenceRoom;
import io.netty.channel.Channel;
import java.awt.Dimension;
import java.net.InetSocketAddress;
import java.rmi.RemoteException;

/**
 *
 * @author BradleyW
 */
public class ConferenceRoom extends TextRoom implements IConferenceRoom {

    private String streamerName;
    private boolean streaming;
    private Dimension streamingDimension;
    private InetSocketAddress streamerIp;
    
    public ConferenceRoom(String iroomName) throws RemoteException {
        super(iroomName);
        this.roomType = 2;
        streaming = false;
        streamerName = "";
    }

    @Override
    public InetSocketAddress getStreamerIp() throws RemoteException
    {
        return streamerIp;
    }
    
    @Override
    public void startBroadcasting(String username, InetSocketAddress networkloc, Dimension d) throws RemoteException
    {
        this.streamerIp = networkloc;
        this.streamerName = username;
        streamingDimension = d;
        streaming = true;
    }
    
    @Override
    public void stopBroadcasting(String streamerName) throws RemoteException
    {
        if (streamerName.equals(this.streamerName))
        {
            streamingDimension = null;
            streamerIp = null;
            streaming = false;
        }
    }

    @Override
    public String getInfo() throws RemoteException {
        return "[ConferenceRoom] " + roomConnections.size() + "/" + roomLimit + " {" + visiblity + "}";
    }

    @Override
    public boolean isStreaming() throws RemoteException {
        return streaming;
    }


    @Override
    public Dimension getDimension() throws RemoteException 
    {
        return streamingDimension;
    }

}
