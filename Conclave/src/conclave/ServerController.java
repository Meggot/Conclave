/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave;

import conclave.ConclaveHandlers.ServerManager;
import conclave.ConclaveHandlers.AccountManager;
import conclave.ConclaveHandlers.ConnectionHandler;
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
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerController implements Remote {

    private String name;
    private InetAddress ip;
    private int port;
    private boolean open;

    //Collections to host all the needed connection info.
    // TO avoid Concurrency exceptions like ConcurrentModificationException, we use
    //special java.util.concurrent library collections, like a conccurentHashMap, and a
    //"CopyOnWriteArraylist" which creates a copy of the collection when it edits.
    
    private ConcurrentHashMap<String, UserInterface> connections = new ConcurrentHashMap<>();
    private CopyOnWriteArrayList<String> serverAdmins = new CopyOnWriteArrayList();
    private CopyOnWriteArrayList<String> bannedUsers = new CopyOnWriteArrayList();

    ServerFrontpage frontpage;
    ServerManager serverManager;
    AccountManager accountManager;
    RoomManager roomManager;

    private static ServerController instance;
    private static final Logger log = Logger.getLogger(ServerController.class.getName());

    public static synchronized ServerController getInstance() {
        if (instance == null) {
            instance = new ServerController();
        }
        return instance;
    }

    private ServerController() {
        accountManager = new AccountManager();
        roomManager = RoomManager.getInstance();
        frontpage = new ServerFrontpage();
        instance = this;
        name = "LAN Conclave Server";
        frontpage.addNewAnnouncment(name, "Welcome to " + name);
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            log.log(Level.SEVERE, "Failed to startup the server on localhost", ex);
        }
        port = 20003;
        if (ip != null) {
            log.log(Level.INFO, "A LAN has been initilized.");
        }
        open = true;
        Thread ConnectionManager = new Thread(new Runnable() {
            @Override
            public void run() {
                while (open) {
                    for (String name : connections.keySet()) {
                        try {
                            UserInterface uiTemp = connections.get(name);
                            if (!uiTemp.isConnected()) {
                                disconnectUser(name);
                            }
                        } catch (RemoteException e) {
                            disconnectUser(name);
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        });
        ConnectionManager.start();
    }

    public void disconnectUser(String name) {
        if (connections.containsKey(name)) {
            connections.remove(name);
            try {
                roomManager.kickUser(name, false);
            } catch (RemoteException ex) {
                Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
            }
            log.log(Level.INFO, "User: {0} has terminated their connection.", name);
        }
    }

    public void loadRMI() {
        try {
            if (System.getSecurityManager() == null) {
                RMISecurityPolicyLoader.LoadPolicy("RMISecurity.policy");
            }
            LocateRegistry.createRegistry(9807);
        } catch (RemoteException e) {
            log.log(Level.SEVERE, "Failed to load the RMI registry", e);
            stopServer();
        }
    }

    public void setName(String iname) {
        this.name = iname;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public void startServer() {
        loadRMI();
        startRooms();
        open = true;
        serverManager = new ServerManager(ip, port);
        serverManager.start();
        if (serverManager.isOpen()) {
            log.log(Level.INFO, "Server: {0} has been started, and is now open to new connections", name);
        }
    }

    public String viewAllConnections() throws RemoteException {
        String returnString = "";
        int i = 0;
        for (UserInterface connection : connections.values()) {
            returnString = returnString + " User " + i + ":" + connection.getUsername();
            i++;
        }
        return returnString;
    }

    public boolean connect(String username) {
        boolean accepted = false;
        if (open && !bannedUsers.contains(username)) {
            accepted = true;
        }
        return accepted;
    }

    public boolean isAUser(String name) throws java.net.ConnectException {
        return accountManager.isAUser(name);
    }

    public boolean isAAdmin(String username) {
        boolean isAdmin = false;
        if (serverAdmins.contains(username)) {
            isAdmin = true;
        }
        return isAdmin;
    }

    public void updateFrontpage(String username, String msg) {
        frontpage.addNewAnnouncment(username, msg);
        updateAllClientsFrontpage(username, msg);
        log.log(Level.INFO, "Admin: {0} has posted a new announcement: {1}", new Object[]{username, msg});
    }

    public void updateAllClientsFrontpage(String username, String msg) {
        for (UserInterface ui : connections.values()) {
            try {
                ui.updateFrontpage(username, msg);
            } catch (RemoteException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void updateAllClientsConnections() {
        for (UserInterface ui : connections.values()) {
            try {
                ui.updateConnections(roomManager.returnRooms());
            } catch (RemoteException ex) {
                Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public List<String> getFrontpage() {
        return new ArrayList(frontpage.getFrontpage());
    }

    public void startRooms() {
        try {
            roomManager.createRoom("TestRoom", "Testicles", 1);
            roomManager.mountOpenRoom("Open Room", 1);
            roomManager.mountOpenRoom("Conclave Room", 2);
            roomManager.loadRoom("TestRoom");
        } catch (RemoteException e) {
            log.log(Level.SEVERE, e.toString(), e);
        }
    }

    public ConnectionsLog viewAllRooms() {
        try {
            return roomManager.returnRooms();
        } catch (RemoteException e) {
            log.log(Level.SEVERE, e.toString(), e);
        }
        return null;
    }

    public boolean login(String username, String password) throws ConnectException {
        Account returnedAccount = null;
        if (accountManager.verifyUser(username, password) && !bannedUsers.contains(username)) {
            returnedAccount = accountManager.getUserByName(username);
            if (returnedAccount != null) {
                addNewInterface(returnedAccount);
                log.log(Level.INFO, "User: {0} has logged in", username);
                return true;
            }
        }
        return false;
    }

    public void addNewInterface(Account returnedAccount) throws ConnectException {
        try {
            String username = returnedAccount.getUsername();
            if (isAUser(username)) {
                UserInterface newUI;
                if (isAAdmin(username)) {
                    newUI = new AdminInterfaceImpl(returnedAccount, this);
                } else {
                    newUI = new UserInterfaceImpl(returnedAccount);
                }
                newUI.updateConnections(roomManager.returnRooms());
                newUI.setFrontpage(frontpage.getFrontpage());
                newUI.connect();
                Registry registry = LocateRegistry.getRegistry(9807);
                registry.rebind(username, newUI);
                connections.put(username, newUI);
            } else {
            }
        } catch (RemoteException e) {
            log.log(Level.SEVERE, e.toString(), e);
        }
    }

    public void addAdmin(String username) {
        serverAdmins.add(username);
        System.out.println("Admin Added: " + username);
        log.log(Level.INFO, "A new Admin has been added: {0}", username);
    }

    public void createGuestAccount(String username) throws ConnectException {
        if (username.length() >= 5 && !accountManager.isAUser(username)) {
            String guestPassword = "GUESTSESSION";
            accountManager.addAccount(username, guestPassword);
            log.log(Level.INFO, "A new guest account has been created with the username: {0}", username);
        }
    }

    public void createAccount(String username, String password) throws ConnectException {
        if (username.length() >= 5 && password.length() >= 5) {
            accountManager.addAccount(username, password);
            log.log(Level.INFO, "A new account has been created with the username: {0}", username);
        }
    }

    public List<String> getAllConnectedUsernames() {
        return new ArrayList(connections.keySet());
    }

    public void stopServer() {
        serverManager.stopServer();
        for (String name : connections.keySet()) {
            disconnectUser(name);
        }
        open = false;
    }

    public void alertUser(Message msg, String username) throws RemoteException {
        UserInterface user = connections.get(username);
        user.recievePrivateMessage(msg);
    }

    public boolean isUserLoggedIn(String username) {
        boolean loggedIn = false;
        if (connections.containsKey(username)) {
            loggedIn = true;
        }
        return loggedIn;
    }

    public void banUser(String username) {
        if (connections.containsKey(username)) {
            UserInterface ui = connections.get(username);
            try {
                ui.recievePrivateMessage(new Message("System", username, "You have been banned from the server", 3));
                roomManager.kickUser(username, true);
                ui.disconnect();
            } catch (RemoteException ex) {
                connections.remove(username);
            }
            bannedUsers.add(username);
        }
    }

}
