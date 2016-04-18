/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclaveclient;

import conclaveclient.Handlers.utlity.PacketUtil;
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
    private static String seckey;
    private static String seckeyUser;
    
    InetAddress ip;
    int port;

    public LoginController(String skey, String skeyUser) {
        connected = false;
        LoadDefaultPolicy();
        
        this.seckey = skey;
        this.seckeyUser = skeyUser;
    }

    public String connect(String iip, String iport) {
        String returnString = "";
        connected = false;
        if (!connected) {
            try {
                if (!iport.matches("[0-9]+") || !iip.matches("[0-9]+") && iip.length() <= 4) {
                    returnString = "Bad format of IP/Port";
                } else {
                    ip = InetAddress.getByName(iip);
                    port = Integer.parseInt(iport);
                    packetUtil = new PacketUtil(ip, port, seckey, seckeyUser);
                    packetUtil.sendPacketRequest("PING");
                    returnString = packetUtil.readStream();
                    if (returnString.contains("100")) {
                        connected = true;
                        returnString = "Connected to server at: " + ip.toString() + ":" + port;
                    } else if (returnString!=null)
                    {
                        //we do not change the returnstring. This will usually be a respnose
                        //such as invalid key.
                    } else {
                        packetUtil = null;
                        returnString = "Failure to connect";
                    }
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
        if (packetUtil != null) {
            packetUtil.close();
            packetUtil = null;
        }
        return returnString;
    }

    public String login(String username, String password) throws RemoteException {
        String returnString = "Error initiating login";
        if (connected) {
            try {
                packetUtil = new PacketUtil(ip, port, seckey, seckeyUser);
                packetUtil.sendPacketRequest("LOGIN " + username + " " + password);
                String responseString = packetUtil.readStream();
                String[] commandWords = responseString.split("//s+");
                if (commandWords[0].contains("100")) {
                    Registry registry = LocateRegistry.getRegistry(ip.getHostAddress(), 9807);
                    IUserInterface ui = (IUserInterface) registry.lookup(username);
                    SwingGUI newSwingGui = new SwingGUI(ui);
                    newSwingGui.setVisible(true);
                }
                returnString = responseString;
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
        if (packetUtil != null) {
            packetUtil.close();
            packetUtil = null;
        }
        return returnString;
    }

    public boolean isConnected() {
        return connected;
    }

    public String createAccount(String username, String password) {
        String returnString = "";
        if (connected) {
            try {
                packetUtil = new PacketUtil(ip, port, seckey, seckeyUser);
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
        if (packetUtil != null) {
            packetUtil.close();
            packetUtil = null;
        }
        return returnString;
    }
}
