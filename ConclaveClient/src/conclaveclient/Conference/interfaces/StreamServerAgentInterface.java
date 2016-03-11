/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclaveclient.Conference.interfaces;

import java.net.SocketAddress;

public interface StreamServerAgentInterface {
	public void start(SocketAddress streamAddress); //Called when the streamer begins streaming
	public void stop(); //Stops the streamers stream and kicks all connections.
}