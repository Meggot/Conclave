/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclaveclient;

import conclaveclient.Handlers.PacketUtil;
import conclave.interfaces.UserInterface;
import conclave.model.ConnectionsLog;
import static conclaveclient.rmi.RMISecurityPolicyLoader.LoadDefaultPolicy;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.IllegalFormatException;

/**
 *
 * @author BradleyW
 */
public class LoginController {
    
    private boolean connected;
    private PacketUtil packetUtil;
    
    public LoginController()
    {
        connected = false;
        LoadDefaultPolicy();
    }
    
    public String connect(String iip, String iport)
    {
        String returnString = "ERROR CONNECTING 2";
        try {
        InetAddress ip = InetAddress.getByName(iip);
        int port = Integer.parseInt(iport);
        packetUtil = new PacketUtil(ip, port);
        packetUtil.sendPacketRequest("PING");
        returnString = packetUtil.readStream();
        if (returnString.contains("100"))
        {
            connected = true;
        }
        return returnString;
        } catch (UnknownHostException e)
        {
            returnString = "Cannot find a conclave server on that IP or Port";
        } catch (IOException e) {
            returnString = "Cannot find a conclave server on that IP or Port";
        } catch (IllegalFormatException e)
        {
            returnString = "Bad format of IP/Port";
        }
        return returnString;
    }
    
    public String login(String username, String password) throws RemoteException {
        String returnString = "Error initiating login";
        if (connected)
        {
        try {
        packetUtil.sendPacketRequest("SETUP-CONNECTION " + username + " " + password);
        String responseString = packetUtil.readStream();
        String[] commandWords = responseString.split("//s+");
        Registry reg = LocateRegistry.getRegistry(9807);
        UserInterface ui = (UserInterface) reg.lookup(username);
        if (commandWords[0].contains("100") && (ui != null))
        { 
            SwingGUI newSwingGui = new SwingGUI(ui);
            returnString = "100 Login Successful";
        } else if (commandWords[0].contains("401"))
        {
            returnString = "401 Login Unsuccessful";
        } else if (commandWords[0].contains("403"))
        {
            returnString = "403 You have been banned from this server";
        }
        } catch (UnknownHostException e) {
            returnString = "That conclave server is currently down";
        } catch (NotBoundException e)
        {
            returnString = "Cannot connect to server registry";
        }
        } else {
            returnString = "You must first connect to a conclave server";
        }
        System.out.println(returnString);
        return returnString;
    }
    
    public boolean isConnected() {
        return connected;
    }
    

    public String createAccount(String username, String password) {
        String returnString = "";
        if (connected)
        {
        try {
        packetUtil.sendPacketRequest("SETUP-ACCOUNT " + username + " " + password);
        returnString = packetUtil.readStream();
        } catch (UnknownHostException e) {
            returnString = "That conclave server is currently down";
        }
        } else {
            returnString = "You must first connect to a conclave server";
        }
        return returnString;
    }
}
