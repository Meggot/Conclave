/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.controllers;

import util.PerformanceLogger;

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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.ConnectionsLog;
import model.Message;
import model.ServerFrontpage;

/**
 * Servercontroller, responsible for hosting the server and maintaining
 * connections, responsbile for holding Admins, banned users and the server
 * frontpage. This class is responsible for creating UserInterfaces and
 * AdminInterfaces as well as adding to the registry
 *
 * @author BradleyW
 */
public class ServerController implements Remote {

    private String name;
    private InetAddress ip;
    private int port;
    private boolean open;
    private static int serverTicks;

    //For performance logging, we will be exporting the performance information into a csv file.
    PerformanceLogger performance;

    // TO avoid Concurrency exceptions like ConcurrentModificationException, we use
    //special java.util.concurrent library collections, like a conccurentHashMap, and a
    //"CopyOnWriteArraylist" which creates a copy of the collection when it edits.
    private final ConcurrentHashMap<String, IUserInterface> connections = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<String> serverAdmins = new CopyOnWriteArrayList();
    private final CopyOnWriteArrayList<String> bannedUsers = new CopyOnWriteArrayList();

    //sServer Frontpage
    ServerFrontpage frontpage;
    //ServerManager, handles new socket connections
    ServerManager serverManager;
    //AccountManager which manages persisted users.
    AccountManager accountManager;
    //RoomManage which handles the Room registry, and save/loads to the database
    RoomManager roomManager;

    private static ServerController instance;
    private static final Logger log = Logger.getLogger(ServerController.class.getName());
    //Socket requests per tick 
    private int requestsPerTicks;
    //Executor handlers limit.
    private static final int HANDLERS = 10;
    //ExecutorService pool.
    private ExecutorService pool = null;

    //As we only want one server active on a JVM, we use a singleton pattern.
    public static synchronized ServerController getInstance() {
        if (instance == null) {
            instance = new ServerController();
        }
        return instance;
    }

    private ServerController() {
        pool = Executors.newFixedThreadPool(HANDLERS);
        serverTicks = 0; //1 tick is 1 second of runtime.
        name = "LAN Conclave Server"; //hardcoded name

        //Initiate singleton patterns for the managers
        accountManager = AccountManager.getInstance();
        roomManager = RoomManager.getInstance();
        frontpage = new ServerFrontpage();
        instance = this;
        frontpage.addNewAnnouncment(name, "Welcome to " + name); //hardcoded server welcome announcement
        open = false;

        // By adding this shutdown hook we can ensure that the server exits gracefully.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                stopServer();
            }
        });
    }

    /**
     * Disconnects a user from the server, used when banning a user.
     *
     * @param name
     */
    public void disconnectUser(String name) {
        if (connections.containsKey(name)) {
            connections.remove(name);
            try {
                roomManager.kickUser(name, false);
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

    /**
     * Loads the RMI policy and security policy.
     */
    public void loadRMI() {
        try {
            if (System.getSecurityManager() == null) {
                RMISecurityPolicyLoader.LoadPolicy("RMISecurity.policy");
            }
            RegistryLoader.Load();
        } catch (RemoteException e) {
            log.log(Level.SEVERE, "Failed to load the RMI registry", e);
            stopServer();
        } catch (UnknownHostException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Sets a server name.
     *
     * @param iname
     */
    public void setName(String iname) {
        this.name = iname;
    }

    /**
     * Sets a server port which Conclave runs off.
     *
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Sets the IP, this is sort of redundant, as the server always runs of its
     * own localhost ip.
     *
     * @param ip
     */
    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    /**
     * Checks to see if the user is banned, use
     *
     * @param username
     * @return
     */
    public boolean isBanned(String username) {
        boolean is = false;
        if (bannedUsers.contains(username)) {
            is = true;
        }
        return is;
    }

    /**
     * This server manager class is responsible for accepting socket connections
     * and assigning a new handler to the connection, it is started by the
     * startServer() method and closed by closeServer();
     */
    public class ServerManager implements Runnable {

        private final InetAddress ip;
        private final int port;
        private boolean open;

        public ServerManager(InetAddress ip, int port) {
            this.ip = ip;
            this.port = port;
            open = true;
        }

        public boolean isOpen() {
            return open;
        }

        @Override
        public void run() {
            try {
                ServerSocket servSock = new ServerSocket(port, 50, ip); //conventional serversocket
                servSock.setReuseAddress(true);
                open = true;
                while (open) {
                    Socket clientSocket = servSock.accept();
                    requestsPerTicks++;//increase the requestsPerTick, for the performance analysis.
                    ClientHandler handler = new ClientHandler(clientSocket);
                    pool.execute(handler);//execute the new handlers runnable in the pool.
                }
            } catch (BindException e) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.INFO, "A server has failed to bind to the IP");
            } catch (IOException e) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.INFO, "A server has failed to read from a socket connection.");
            } finally {
                if (!pool.isTerminated()) {
                    System.err.println("cancel non-finished tasks");
                }
                pool.shutdownNow();
            }
        }

        /**
         * Stops the server by breaking the ServerSocket accept() loop, allowing
         * the finally clause to execute.
         */
        public void stopServer() {
            open = false;
            log.log(Level.INFO, "The server has been stopped");
            pool.shutdown();
        }

    } //End of ServerManager class.

//Server Controller
    /**
     * This method will start the Server, load the RMI registry and also load
     * any rooms found in the Persistence database. It will also run a
     * ConnectionManager thread which will monitor connection states and remove
     * disconnected users.
     */
    public void startServer() {
        loadRMI();
        startRooms();
        try {
            ip = InetAddress.getLocalHost(); //gets the localhost ip.
        } catch (UnknownHostException ex) {
            log.log(Level.SEVERE, "Failed to startup the server on localhost", ex);
        }
        port = 20003; //default Conclave port is 20003
        if (ip != null) {
            log.log(Level.INFO, "A LAN has been initilized.");
        }

        performance = new PerformanceLogger(name + "-" + System.currentTimeMillis(), ip, port); // new performance writer, saved to a unique filename
        /**
         * This is responsible for logging the performance details to the csv,
         * this allows each row in the csv file to be exactly 1 second apart.
         */
        class PerformanceLogging extends TimerTask {

            public void run() {
                serverTicks++;
                performance.systemLog(serverTicks, requestsPerTicks, connections.size(), roomManager.roomsAmount());
                requestsPerTicks = 0;
            }
        }
        Timer performanceAnalysis = new Timer();
        serverManager = new ServerManager(ip, port);
        open = true; //sets the open flag to true, allowing the newly created ServerManager to run, when it is submitted. 
        //This code will take the server out of the pool, allowing all pool threads to only be used by 
        //Thread newThread = new Thread(new Runnable() {
        //    public void run()
        //    {
        //        serverManager.run();
        //    }
        //});
        //newThread.start();
        pool.submit(serverManager); //submits the ServerManager into the ExecutrPool
        performanceAnalysis.scheduleAtFixedRate(new PerformanceLogging(), 0, 1000);//schedule for once every second, starting immediantly.
        if (serverManager.isOpen()) { //if the server managed to startup.
            log.log(Level.INFO, "Server: {0} has been started at: {1} on port {2}, and is now open to new connections", new Object[]{name, ip, port});
            Thread ConnectionManager = new Thread(new Runnable() { //this thread will cycle through connections and remove ones with the Disconnected flag.
                @Override
                public void run() {
                    while (open) { //like the ServerManager, runs while server is open.
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
                            Thread.sleep(1000); //Server tick, in the future this should be more natural and consistent.

                        } catch (InterruptedException ex) {
                            Logger.getLogger(ServerController.class
                                    .getName()).log(Level.SEVERE, null, ex);
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

    /**
     * Returns a String of all connections, used as a testing method
     *
     * @return
     * @throws RemoteException
     */
    public String viewAllConnections() throws RemoteException {
        String returnString = "";
        int i = 0;
        for (IUserInterface connection : connections.values()) {
            returnString = returnString + " User " + i + ":" + connection.getUsername();
            i++;
        }
        return returnString;
    }

    /**
     * Connects the user to a server, if the server is open and the username is
     * not banned.
     *
     * @param username
     * @return
     */
    public boolean connect(String username) {
        boolean accepted = false;
        if (open && !bannedUsers.contains(username)) {
            accepted = true;
        }
        return accepted;
    }

    /**
     * Finds out if the username exists.
     *
     * @param name
     * @return
     * @throws java.net.ConnectException
     */
    public boolean isAUser(String name) throws java.net.ConnectException {
        return accountManager.isAUser(name);
    }

    /**
     * If a username is an Admin.
     *
     * @param username
     * @return
     */
    public boolean isAAdmin(String username) {
        boolean isAdmin = false;
        if (serverAdmins.contains(username)) {
            isAdmin = true;
        }
        return isAdmin;
    }

    /**
     * Updates the frontpage with a msg, the username is used for clarity.
     *
     * @param username
     * @param msg
     */
    public void updateFrontpage(String username, String msg) {
        frontpage.addNewAnnouncment(username, msg);
        updateAllClientsFrontpage(username, msg);
        log.log(Level.INFO, "Admin: {0} has posted a new announcement: {1}", new Object[]{username, msg});
    }

    /**
     * Updates all the users frontpage, this enables controlles to push
     * frontpage announcements in realtime.
     *
     * @param username
     * @param msg
     */
    public void updateAllClientsFrontpage(String username, String msg) {
        for (IUserInterface ui : connections.values()) {
            try {
                ui.updateFrontpage(username, msg);
            } catch (RemoteException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * When aaconnection changes, all clients connections log is updated from
     * this method.
     */
    public void updateAllClientsConnections() {
        for (IUserInterface ui : connections.values()) {
            try {
                ui.updateConnections(roomManager.returnRooms());

            } catch (RemoteException ex) {
                Logger.getLogger(ServerController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Returns the current frontpage
     *
     * @return
     */
    public List<String> getFrontpage() {
        return new ArrayList(frontpage.getFrontpage());
    }

    /**
     * Starts all rooms found in the persistence DB.
     */
    public void startRooms() {
        try {
            roomManager.mountOpenRoom("Atrium", 1);
            List<String> names = roomManager.getAllRoomNames();
            for (String nme : names) {
                roomManager.loadRoom(nme);
            }
        } catch (RemoteException e) {
            log.log(Level.SEVERE, e.toString(), e);
        }
    }

    /**
     * View all rooms connectionslog.
     *
     * @return
     */
    public ConnectionsLog viewAllRooms() {
        try {
            return roomManager.returnRooms();
        } catch (RemoteException e) {
            log.log(Level.SEVERE, e.toString(), e);
        }
        return null;
    }

    /**
     * Verifies a username + password and under a certain number of
     * restrictions. If the user is verified then a new interface is created and
     * added to the registry.
     *
     * @param username
     * @param password
     * @return
     * @throws ConnectException
     */
    public boolean login(String username, String password) throws ConnectException {
        Account returnedAccount = null;
        if (accountManager.verifyUser(username, password) && !bannedUsers.contains(username) && !connections.contains(username)) {
            returnedAccount = accountManager.getUserByName(username);
            if (returnedAccount != null) {
                addNewInterface(returnedAccount);
                log.log(Level.INFO, "User: {0} has logged in", username);
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a new interface for the account specified, if the account given is
     * an admin, then it is assigned an AdminInterface object instead of a
     * classic UserInterface. It will then add this UI onto the registry.
     *
     * @param returnedAccount
     * @throws ConnectException
     */
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
                newUI.connect();
                newUI.updateConnections(roomManager.returnRooms());
                newUI.setFrontpage(frontpage.getFrontpage());
                Registry registry = LocateRegistry.getRegistry(9807);
                registry.rebind(username, newUI);
                connections.put(username, newUI);
            } else {
            }
        } catch (RemoteException e) {
            log.log(Level.SEVERE, e.toString(), e);
        }
    }

    /**
     * Adds a username to be given an admin interface to the ArrayList.
     *
     * @param username
     */
    public void addAdmin(String username) {
        serverAdmins.add(username);
        log.log(Level.INFO, "A new Admin has been added: {0}", username);
    }

    /**
     * Creates a new account, using the accountmanager to persist.
     *
     * @param username
     * @param password
     * @throws ConnectException
     */
    public void createAccount(String username, String password) throws ConnectException {
        if (username.length() >= 5 && password.length() >= 5) {
            accountManager.addAccount(username, password);
            log.log(Level.INFO, "A new account has been created with the username: {0}", username);
        }
    }

    /**
     * Returns a List collection of all connected usernames, for more high level
     * interactions with the connected userbank. Used for Admin control.
     *
     * @return
     */
    public List<String> getAllConnectedUsernames() {
        return new ArrayList(connections.keySet());
    }

    /**
     * Stops the server, and disconnects all connected users.
     */
    public void stopServer() {
        serverManager.stopServer();
        for (String name : connections.keySet()) {
            disconnectUser(name);
        }
        open = false;
        performance.close();
        pool.shutdown();
    }

    /**
     * Alert a user, this is used during Admin messages, as well as room private
     * whispers.
     *
     * @param msg
     * @param username
     */
    public void alertUser(Message msg, String username) {
        IUserInterface user = connections.get(username);
        try {
            user.recievePrivateMessage(msg);

        } catch (RemoteException ex) {
            Logger.getLogger(ServerController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Checks to see if the user is logged in, used during the Login process.
     *
     * @param username
     * @return
     */
    public boolean isUserLoggedIn(String username) {
        boolean loggedIn = false;
        if (connections.containsKey(username)) {
            loggedIn = true;
        }
        return loggedIn;
    }

    /**
     * Bans a user from the server, by a username. Will send them a private
     * message too.
     *
     * @param username
     */
    public void banUser(String username) {
        if (connections.containsKey(username)) {
            IUserInterface ui = connections.get(username);
            try {
                ui.recievePrivateMessage(new Message("System", username, "You have been banned from the server", 3));
                ui.disconnect();
            } catch (RemoteException ex) {
            }
            bannedUsers.add(username);
        }
    }

}
