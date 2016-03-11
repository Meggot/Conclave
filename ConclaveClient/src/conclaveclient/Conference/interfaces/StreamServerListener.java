/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclaveclient.Conference.interfaces;

import org.jboss.netty.channel.Channel;

public interface StreamServerListener {
	public void onClientConnectedIn(Channel channel); //Called on streamer side when client connects
	public void onClientDisconnected(Channel channel); //called on streamer side when client disconnects
	public void onExcaption(Channel channel,Throwable t); //When an exception is called.
}