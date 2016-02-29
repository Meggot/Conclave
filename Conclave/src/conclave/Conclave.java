/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave;

import conclave.ConclaveHandlers.SecurityManager;
import conclave.ConclaveHandlers.ServerManager;
import conclave.ConclaveHandlers.AccountManager;
import com.github.sarxos.webcam.*;
import conclave.ConclaveHandlers.RoomManager;
import conclave.db.Account;
import conclave.interfaces.UserInterface;
import conclave.interfaces.UserInterfaceImpl;
import conclave.model.ConnectionsLog;
import conclave.rmiPolicy.RMISecurityPolicyLoader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.ConnectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;


public class Conclave implements Remote{
    
    private String name;
    private String myUrl;
    private InetAddress ip;
    private int port;
    private boolean open;
    private ArrayList<UserInterface> connections = new ArrayList<>();
    private ArrayList<String> serverAdmins = new ArrayList();
    
    ServerManager serverManager;
    AccountManager accountManager;
    SecurityManager securityManager;
    RoomManager roomManager;
    
    private static Conclave instance;
    
    private Conclave()
    {
        securityManager =  SecurityManager.getInstance();
        accountManager = new AccountManager();
        roomManager = RoomManager.getInstance();
        open = false;
        try {
            this.ip = InetAddress.getByName("192.168.0.20");
        } catch (UnknownHostException e)
        {
            System.out.println("A conclave server is already running on this IP and Port");
        }
    }
    
    public void loadRMI()
    {
        try {
        if (System.getSecurityManager() == null) {
            RMISecurityPolicyLoader.LoadPolicy("RMISecurity.policy");
        }
        LocateRegistry.createRegistry(9807);
        System.out.println( "Started Registry" );
        } catch (RemoteException e)
        {
            e.printStackTrace();
            stopServer();
        }
    }
    
    public static synchronized Conclave getInstance() 
    {
        if (instance == null) {
                instance = new Conclave();}
        return instance;
    }
    
    public void setName(String iname)
    {
        this.name = iname;
    }
    
    public void setUrl(String url)
    {
        myUrl = url;
    }
    
    public void setPort(int port)
    {
        this.port = port;
    }
    
    public void startServer()
    {
        loadRMI();
        startRooms();
        serverManager = new ServerManager(ip, port);
        serverManager.start();
    }
    
    public String viewAllConnections()
    {
        String returnString = "";
        for (UserInterface connection : connections)
        {
            //returnString = returnString + " Thread#: " + connection.getUserName();
        }
        return returnString;
    }
    
    public boolean connect()
    {
        return open;
    }
    
    public boolean isAUser(String name) throws java.net.ConnectException
    {
        return accountManager.isAUser(name);
    }
    
    public  boolean isAAdmin(String username)
    {
        if (serverAdmins.contains(username))
        {
            return true;
        }
        return false;
    }
    
    public void startRooms()
    {
        try {
            roomManager.createRoom("TestRoom", "Testicles", 1);
            roomManager.mountOpenRoom("Open Room", 1);
            roomManager.loadRoom("TestRoom");
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }
    
    public ConnectionsLog viewAllRooms()
    {
        try {
        return roomManager.returnRooms();
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean login(String username, String password) throws ConnectException
    {
        Account returnedAccount = null;
        if (accountManager.verifyUser(username, password))
        {
            returnedAccount = accountManager.getUserByName(username);
            if (returnedAccount!=null)
            {
                addNewInterface(returnedAccount);
                return true;
                //if (serverAdmins.contains(returnedAccount.getUserid()))
                //{
                // AdminController adminUser = new AdminController(user);
                //} else {
                //MemberController memberUser = new MemberController(user);
                //}
            }   
        }
        return false;
    }
    
    public void addNewInterface(Account returnedAccount) throws ConnectException
    {
        try {
        String username = returnedAccount.getUsername();
        if (isAUser(username))
        {
            UserInterfaceImpl newUI = new UserInterfaceImpl(returnedAccount);
            newUI.updateConnections(roomManager.returnRooms());
            newUI.connect();
            //if (isAAdmin(username))
            //{
                //newUI = new UserInterfaceImpl();
            //}
            Registry registry = LocateRegistry.getRegistry( 9807);
            registry.rebind(username, newUI);
            System.out.println("UserInterface for " + username + "is bound");
            connections.add(newUI);
        } else {
            System.out.println("User doesn't exist.");
        }
        } catch (RemoteException e)
        {
            e.printStackTrace();;
        }
    }
    
    public void addAdmin(String username)
    {
        serverAdmins.add(username);
        System.out.println("Admin Added: " + username);
    }
    
    public void createGuestAccount(String username) throws ConnectException
    {
        if (username.length() >= 5 && !accountManager.isAUser(username))
        {
            String guestPassword = "GUESTSESSION";
            accountManager.addAccount(username, guestPassword);
        } else {
            System.out.println("Cannot create guest due to invalid username");
        }
    }
    
    public void createAccount(String username, String password) throws ConnectException
    {
        if (username.length() >= 5 && password.length() >= 5)
        {
            accountManager.addAccount(username, password);
        } else {
            System.out.println("Cannot create account due to invalid username/password");
        }
    }

    private void stopServer() 
    {
        serverManager.stopServer();
    }
}
