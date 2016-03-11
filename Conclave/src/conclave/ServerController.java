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
import conclave.interfaces.AdminInterfaceImpl;
import conclave.interfaces.UserInterface;
import conclave.interfaces.UserInterfaceImpl;
import conclave.model.ConnectionsLog;
import conclave.model.Message;
import conclave.model.ServerFrontpage;
import conclave.rmiPolicy.RMISecurityPolicyLoader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.ConnectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ServerController implements Remote{
    
    private String name;
    private String myUrl;
    private InetAddress ip;
    private int port;
    private boolean open;
    private HashMap<String, UserInterface> connections = new HashMap<>();
    private ArrayList<String> serverAdmins = new ArrayList();
    
    ServerFrontpage frontpage;
    ServerManager serverManager;
    AccountManager accountManager;
    RoomManager roomManager;
    
    private static ServerController instance;
    
    private ServerController()
    {
        accountManager = new AccountManager();
        roomManager = RoomManager.getInstance();
        frontpage = new ServerFrontpage();
        open = false;
        this.ip = null;
        this.name = "Conclave Testing Server";
        frontpage.addNewAnnouncment(name, "Welcome to Bradley's server.");
    }
    
    private void connectToDatabase()
    {
        try {
            Connection dbConnection = DriverManager.getConnection("jdbc:derby:Conclave;create=true");
            DatabaseMetaData dbmd = dbConnection.getMetaData();
            System.out.println(dbmd.getUserName());
        } catch (SQLException e)
        {
            
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
    
    public static synchronized ServerController getInstance() 
    {
        if (instance == null) {
                instance = new ServerController();}
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
    
    public void setIp(InetAddress ip)
    {
        this.ip = ip;
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
        for (UserInterface connection : connections.values())
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
        boolean isAdmin = false;
        if (serverAdmins.contains(username))
        {
            isAdmin = true;
        }
        return isAdmin;
    }
    
    public void updateFrontpage(String username, String msg) 
    {
        frontpage.addNewAnnouncment(username, msg);
        updateAllClientsFrontpage(username, msg);
    }
    
    public void updateAllClientsFrontpage(String username, String msg)
    {
        for (UserInterface ui : connections.values())
        {
            try {
                ui.updateFrontpage(username, msg);
            } catch (RemoteException ex) {
                Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public List<String> getFrontpage()
    {
        return new ArrayList(frontpage.getFrontpage());
    }
    
    public void startRooms()
    {
        try {
            roomManager.createRoom("TestRoom", "Testicles", 1);
            roomManager.mountOpenRoom("Open Room", 1);
            roomManager.mountOpenRoom("Conclave Room", 2);
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
            UserInterface newUI;
            if (isAAdmin(username))
            {
                newUI = new AdminInterfaceImpl(returnedAccount);
            } else {
                newUI = new UserInterfaceImpl(returnedAccount);
            }
            newUI.updateConnections(roomManager.returnRooms());
            newUI.setFrontpage(frontpage.getFrontpage());
            newUI.connect();
            Registry registry = LocateRegistry.getRegistry( 9807);
            registry.rebind(username, newUI);
            System.out.println("UserInterface for " + username + "is bound");
            connections.put(username, newUI);
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
    
    public List<String> getAllConnectedUsernames() {
        return new ArrayList(connections.keySet());
    }

    public void stopServer() 
    {
        serverManager.stopServer();
    }
    
    public void alertUser(Message msg, String username) throws RemoteException
    {
            UserInterface user = connections.get(username);
            user.recievePrivateMessage(msg);
    }
}
