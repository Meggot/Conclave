/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclaveclient.Conference.interfaces;

import org.jboss.netty.channel.Channel;

public interface StreamClientListener {
	public void onConnected(Channel channel); //Called when connected.
	public void onDisconnected(Channel channel); //Called when disconnected.
	public void onException(Channel channel,Throwable t); //Called when exception is thrown.
}