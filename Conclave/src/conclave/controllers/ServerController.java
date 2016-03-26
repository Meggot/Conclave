/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.controllers;

import util.CSVWriter;
import conclave.ConclaveHandlers.AccountManager;
import conclave.ConclaveHandlers.ClientHandler;
import conclave.ConclaveHandlers.RoomManager;
import conclave.db.Account;
import conclave.rmiPolicy.RMISecurityPolicyLoader;
import conclave.rmiPolicy.RegistryLoader;
import conclaveinterfaces.IUserInterface;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.ConnectionsLog;
import model.Message;
import model.ServerFrontpage;

public class ServerController implements Remote {

    private String name;
    private InetAddress ip;
    private int port;
    private boolean open;

    private static int serverTicks;

    private int clientConnections;

    //For performance logging, we will be exporting the performance information into a csv file.
    //Using a fileWriter
    //Collections to host all the needed connection info.
    // TO avoid Concurrency exceptions like ConcurrentModificationException, we use
    //special java.util.concurrent library collections, like a conccurentHashMap, and a
    //"CopyOnWriteArraylist" which creates a copy of the collection when it edits.
    private ConcurrentHashMap<String, IUserInterface> connections = new ConcurrentHashMap<>();
    private CopyOnWriteArrayList<String> serverAdmins = new CopyOnWriteArrayList();
    private CopyOnWriteArrayList<String> bannedUsers = new CopyOnWriteArrayList();

    ServerFrontpage frontpage;
    ServerManager serverManager;
    AccountManager accountManager;
    RoomManager roomManager;
    CSVWriter performance;
    private static ServerController instance;
    private static final Logger log = Logger.getLogger(ServerController.class.getName());

    private static final int HANDLERS = 10;

    private ExecutorService pool = null;

    public static synchronized ServerController getInstance() {
        if (instance == null) {
            instance = new ServerController();
        }
        return instance;
    }

    private ServerController() {
        pool = Executors.newFixedThreadPool(HANDLERS);
        serverTicks = 0;
        name = "LAN Conclave Server";
        accountManager = new AccountManager();
        roomManager = RoomManager.getInstance();
        frontpage = new ServerFrontpage();
        instance = this;
        frontpage.addNewAnnouncment(name, "Welcome to " + name);
        open = false;
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Stopping Server");
                stopServer();
            }
        });
    }

    /**
     * 
     * @param name 
     */
    public void disconnectUser(String name) {
        if (connections.containsKey(name)) {
            connections.remove(name);
            try {
                roomManager.kickUser(name, false);
                Registry register = LocateRegistry.getRegistry();
                //register.unbind(name);
            } catch (RemoteException ex) {
                Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
                //} catch (NotBoundException ex)
                //{
                //   Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
            }
            log.log(Level.INFO, "User: {0} has terminated their connection.", name);
        }
    }

    public void loadRMI() {
        try {
            if (System.getSecurityManager() == null) {
                RMISecurityPolicyLoader.LoadPolicy("RMISecurity.policy");
            }
            RegistryLoader.Load();
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

    public boolean isBanned(String username) {
        boolean is = false;
        if  (bannedUsers.contains(username))
        {
            is = true;
        }
        return is;
    }

    public class ServerManager implements Runnable {
        private final InetAddress ip;
        private final int port;
        private boolean open;
        public ServerManager(InetAddress ip, int port) {
            this.ip = ip;
            this.port = port;
            open = false;
        }

        public boolean isOpen() {
            return open;
        }

        @Override
        public void run() {
            open = true;
            try {
                ServerSocket servSock = new ServerSocket(port, 50, ip);
                servSock.setReuseAddress(true);
                while (open) {
                    Socket clientSocket = servSock.accept();
                    clientConnections++;
                    ClientHandler handler = new ClientHandler(clientSocket);
                    System.out.println("CLIENT CONNECTED..");
                    pool.execute(handler);
                }
            } catch (BindException e) {
                System.out.println("A conclave server is already running on that IP and port.");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                stopServer();
                pool.shutdown();
            }
        }

        public void stopServer() {
            open = false;
            log.log(Level.INFO, "The server has been stopped");
        }

    }

    public void startServer() {
        loadRMI();
        startRooms();
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            log.log(Level.SEVERE, "Failed to startup the server on localhost", ex);
        }
        port = 20003;
        if (ip != null) {
            log.log(Level.INFO, "A LAN has been initilized.");
        }

        serverManager = new ServerManager(ip, port);
        pool.submit(serverManager);
        performance = new CSVWriter(name + "-" + System.currentTimeMillis());
        if (serverManager.isOpen()) {
            log.log(Level.INFO, "Server: {0} has been started, and is now open to new connections", name);
            open = true;
            Thread ConnectionManager = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (open) {
                        for (String name : connections.keySet()) {
                            try {
                                IUserInterface uiTemp = connections.get(name);
                                if (!uiTemp.isConnected()) {
                                    disconnectUser(name);
                                }
                            } catch (RemoteException e) {
                                disconnectUser(name);
                            }
                        }
                        try {
                            Thread.sleep(1000);
                            serverTicks++;
                            performance.systemLog(serverTicks, clientConnections, connections.size());
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

            });
            ConnectionManager.start();
        } else {
            log.log(Level.SEVERE, "Server failed to start");
            open = false;
        }
    }

    public String viewAllConnections() throws RemoteException {
        String returnString = "";
        int i = 0;
        for (IUserInterface connection : connections.values()) {
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
        for (IUserInterface ui : connections.values()) {
            try {
                ui.updateFrontpage(username, msg);
            } catch (RemoteException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
    }

    public void updateAllClientsConnections() {
        for (IUserInterface ui : connections.values()) {
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

    public void anonUserConnect() {
        clientConnections++;
        System.out.println("ANONUSER CONNECTED");
    }

    public void anonUserDisconnect() {
        clientConnections--;
        System.out.println("ANONUSER DISCONNECTED");
    }

    public void addNewInterface(Account returnedAccount) throws ConnectException {
        try {
            String username = returnedAccount.getUsername();
            if (isAUser(username)) {
                IUserInterface newUI;
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
        performance.close();
    }

    public void alertUser(Message msg, String username){
        IUserInterface user = connections.get(username);
        try {
            user.recievePrivateMessage(msg);
        } catch (RemoteException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            IUserInterface ui = connections.get(username);
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
