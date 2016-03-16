/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.ConclaveHandlers;

import conclave.ServerController;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RMISecurityManager;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BradleyW
 */
public class ServerManager extends Thread{
    
    private final InetAddress ip;
    private final int port;
    private boolean open;
    private static final Logger log= Logger.getLogger( ServerManager.class.getName() );
     
    public ServerManager(InetAddress ip, int port)
    {
        this.ip = ip;
        this.port = port;
        open = false;
    }
    
    public boolean isOpen()
    {
        return open;
    }
    
    public void start()
    {
        open = true;
        try {
            ServerSocket servSock = new ServerSocket(port, 50, ip);
            servSock.setReuseAddress(true);
            int activeThreads = 0;
            Socket newConnectionSocket;
            Runnable newHandler;
            while (open)
            {
                newConnectionSocket = servSock.accept();
                newHandler = new ConnectionHandler(newConnectionSocket, activeThreads);
                Thread connectionThread = new Thread(newHandler);
                connectionThread.start();
            }
            } catch (BindException e) {
                System.out.println("A conclave server is already running on that IP and port.");
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            } finally {
            stopServer();
        }
    }
    public void stopServer() {
        open = false;
        log.log(Level.INFO, "The server has been stopped");
    }
}
