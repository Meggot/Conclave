/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclaveclient;

import conclaveclient.Handlers.PacketUtil;
import static conclaveclient.rmi.RMISecurityPolicyLoader.LoadDefaultPolicy;
import conclaveinterfaces.IUserInterface;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.IllegalFormatException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BradleyW
 */
public class LoginController {

    private boolean connected;
    private PacketUtil packetUtil;

    InetAddress ip;
    int port;

    public LoginController() {
        connected = false;
        LoadDefaultPolicy();
    }

    public String connect(String iip, String iport) {
        String returnString = "";
        connected = false;
        if (!connected) {
            try {
                ip = InetAddress.getByName(iip);
                port = Integer.parseInt(iport);
                packetUtil = new PacketUtil(ip, port);
                packetUtil.sendPacketRequest("PING");
                returnString = packetUtil.readStream();
                if (returnString != null) {
                    connected = true;
                    returnString = "Connected to server at: " + ip.toString() + ":" + port;
                } else {
                    packetUtil = null;
                    returnString = "Failure to connect";
                }
            } catch (UnknownHostException e) {
                returnString = "Cannot find a conclave server on that IP or Port";
            } catch (IOException e) {
                returnString = "Cannot find a conclave server on that IP or Port";
            } catch (IllegalFormatException e) {
                returnString = "Bad format of IP/Port";
            }
        } else {
            returnString = "You are already connected!";
        }
        packetUtil.close();
        packetUtil = null;
        return returnString;
    }

    public String login(String username, String password) throws RemoteException {
        String returnString = "Error initiating login";
        if (connected) {
            try {
                packetUtil = new PacketUtil(ip, port);
                packetUtil.sendPacketRequest("LOGIN " + username + " " + password);
                String responseString = packetUtil.readStream();
                String[] commandWords = responseString.split("//s+");
                if (commandWords[0].contains("100")) {
                    Registry registry = LocateRegistry.getRegistry(ip.getHostAddress(), 9807);
                    IUserInterface ui = (IUserInterface) registry.lookup(username);
                    SwingGUI newSwingGui = new SwingGUI(ui);
                    returnString = "100 Login Successful";
                } else {
                    returnString = responseString;
                }
            } catch (UnknownHostException e) {
                returnString = "That conclave server is currently down";
            } catch (NotBoundException e) {
                returnString = "Cannot connect to server registry";
            } catch (IOException ex) {
                returnString = "IOException: " + ex.getLocalizedMessage();
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            returnString = "You must first connect to a conclave server";
        }
        System.out.println(returnString);
        packetUtil.close();
        packetUtil = null;
        return returnString;
    }

    public boolean isConnected() {
        return connected;
    }

    public String createAccount(String username, String password) {
        String returnString = "";
        if (connected) {
            try {
                packetUtil = new PacketUtil(ip, port);
                packetUtil.sendPacketRequest("SETUP-ACCOUNT " + username + " " + password);
                returnString = packetUtil.readStream();
            } catch (UnknownHostException e) {
                returnString = "That conclave server is currently down";
            } catch (IOException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            returnString = "You must first connect to a conclave server";
        }
        packetUtil.close();
        packetUtil = null;
        return returnString;
    }
}
