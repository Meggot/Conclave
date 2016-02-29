/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.ConclaveHandlers;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RMISecurityManager;
import java.util.ArrayList;

/**
 *
 * @author BradleyW
 */
public class ServerManager extends Thread{
    
    private InetAddress ip;
    private int port;
    private boolean open;
    
    private ArrayList<ConnectionHandler> connections = new ArrayList<>();
    
    public ServerManager(InetAddress ip, int port)
    {
        this.ip = ip;
        this.port = port;
        open = false;
    }
    public void start()
    {
        open = true;
        try {
            ServerSocket servSock = new ServerSocket(port, 50, ip);
            //servSock.setReuseAddress(true);
            int activeThreads = 0;
            Socket newConnectionSocket;
            Runnable newHandler;
            while (open)
            {
                System.out.println("Server is online on port: " + servSock.getLocalPort() + ". IP: " + servSock.getInetAddress());
                newConnectionSocket = servSock.accept();
                System.out.println("New Connection! Connection IP: " + newConnectionSocket.getInetAddress());
                newHandler = new ConnectionHandler(newConnectionSocket, activeThreads);
                Thread connectionThread = new Thread(newHandler);
                connectionThread.start();
            }
            } catch (BindException e) {
                System.out.println("A conclave server is already running on that IP and port.");
            }catch (IOException e) {
                e.printStackTrace();
            }
    }
    public void stopServer() {
        open = false;
    }
}
