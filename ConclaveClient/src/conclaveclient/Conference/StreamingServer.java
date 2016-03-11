/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclaveclient.Conference;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.Dimension;
import java.net.InetSocketAddress;

/**
 *
 * @author BradleyW
 */
public class StreamingServer {

    private Webcam webcam = null;
    private Dimension dimension = null;
    StreamServerAgent serverAgent = null;
    InetSocketAddress socketAddress;
    
    public StreamingServer() {
        socketAddress = new InetSocketAddress("localhost", 20000);
        Webcam.setAutoOpenMode(true);
        webcam = Webcam.getDefault();
        dimension = WebcamResolution.VGA.getSize();
        webcam.setViewSize(dimension);
    }

    public void streamWebcam() {
        serverAgent = new StreamServerAgent(webcam, WebcamResolution.VGA.getSize());
        serverAgent.start(socketAddress);
    }

    public boolean webcamIsActive() {
        boolean active = false;
        if (webcam != null) {
            active = webcam.isOpen();
        }
        return active;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public InetSocketAddress getSocketIp() {
        return socketAddress;
    }
    
    public void stopStreaming()
    {
        if (serverAgent!=null)
        {
            serverAgent.stop();
        }
    }

}
