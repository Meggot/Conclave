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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.Exception;

/**
 *
 * @author BradleyW
 */
public class StreamingServer {

    private Webcam webcam = null;
    private Dimension dimension = null;
    StreamServerAgent serverAgent = null;
    InetSocketAddress socketAddress;
    
    public StreamingServer() throws Exception{
        try {
            socketAddress = new InetSocketAddress(InetAddress.getLocalHost(), 20000);
        } catch (UnknownHostException ex) {
            Logger.getLogger(StreamingServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (Webcam.getWebcams()!=null)
        {
            webcam = Webcam.getDefault();
            dimension = WebcamResolution.VGA.getSize();
            webcam.setViewSize(dimension);
            Webcam.setAutoOpenMode(true);
        } else {
            throw new Exception();
        }
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
    
    public String getName() {
        return webcam.getName();
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
            webcam.close();
        }
    }

}
