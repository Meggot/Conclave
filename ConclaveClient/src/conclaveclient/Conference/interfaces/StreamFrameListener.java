/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclaveclient.Conference.interfaces;

import java.awt.image.BufferedImage;

public interface StreamFrameListener {
	public void onFrameReceived(BufferedImage image); //Called when the client recieves a frame.
}