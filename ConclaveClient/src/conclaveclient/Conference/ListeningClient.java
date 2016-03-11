/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclaveclient.Conference;

import conclaveclient.Conference.interfaces.StreamFrameListener;
import conclaveclient.SwingGUI;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.net.InetSocketAddress;

public class ListeningClient {
	/**
	 * @author kerr
	 * */
	private static SwingGUI displayWindow;
        private static StreamClientAgent clientAgent = null;
        
	public static void run(SwingGUI windowFrame, InetSocketAddress addr, Dimension d) {
		//setup the videoWindow
                displayWindow = windowFrame;
		displayWindow.setVisible(true);
		//setup the connection
		//logger.info("setup dimension :{}",dimension);
		clientAgent = new StreamClientAgent(new StreamFrameListenerIMPL(),d);
		clientAgent.connect(addr);
	}
	
        public static void close()
        {
            clientAgent.stop();
        }
	
	protected static class StreamFrameListenerIMPL implements StreamFrameListener{
		private volatile long count = 0;
		@Override
		public void onFrameReceived(BufferedImage image) {
			//logger.info("frame received :{}",count++);
                        System.out.println(image.getClass() + ": recieved Frame.");
			displayWindow.updateImage(image);			
		}
		
	}
}
	
