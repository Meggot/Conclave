/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclaveclient.Conference.interfaces;

import java.net.SocketAddress;


public interface StreamClientAgentInterface {
	public void connect(SocketAddress streamServerAddress); //Connects to the stream via a SocketAddress
	public void stop(); //Stop recieving.
}