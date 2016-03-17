/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.ConclaveHandlers;

import conclave.ServerController;
import conclave.db.Account;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BradleyW
 */
public class ConnectionHandler implements Runnable {

    private Socket sock;
    private ServerController server;
    private int connectionid;

    private InputStream ins;
    private OutputStream os;
    private final int timeOutPeriod = 3000;
    private static final Logger log = Logger.getLogger(ConnectionHandler.class.getName());
    private boolean loggedIn;

    public ConnectionHandler(Socket isock, int id) {
        try {
            this.sock = isock;
            ins = sock.getInputStream();
            os = sock.getOutputStream();
            this.server = ServerController.getInstance();
            this.connectionid = id;
            log.log(Level.INFO, "A user has connected from: {0}", sock.getInetAddress().toString());
        } catch (IOException e) {

        }
    }

    @Override
    public void run() {
        try {
            while (!sock.isClosed()) {
                String commandLine = recieveRequest();
                if (commandLine != null) {
                    String returnMsg = handleRequest(commandLine);
                    sendMessage(returnMsg);
                    log.log(Level.INFO, "Recieved Request: {0}, sent back response: {1}.", new Object[]{commandLine, returnMsg});
                }
                if (loggedIn) {
                   sock.close();
                }
            }
        } catch (IOException e) {
        } finally {
            try {
                sock.close();
            } catch (IOException ex) {
                Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String getIpString() {
        return sock.getInetAddress().toString();
    }

    public String handleRequest(String request) {
        int responseCode = 502;
        String returnMsg = "";
        String[] commandWords = request.split("\\s+");
        System.out.println(commandWords[0]);
        if (commandWords[0].contains("SETUP-CONNECTION")) { //BEGIN LOGIN
            String username = "";
            String password = "";
            if (commandWords.length >= 2) {
                username = commandWords[1];
                boolean isAUser = false;
                try {
                    isAUser = server.isAUser(username);
                } catch (ConnectException e) {
                }
                if (commandWords.length == 3 && isAUser) {
                    if (!server.isUserLoggedIn(username)) {
                        password = commandWords[2];
                        boolean correctDetails = false;
                        try {
                            correctDetails = server.login(username, password);
                        } catch (ConnectException ex) {
                            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (correctDetails) {
                            if (server.connect(username)) {
                                responseCode = 100;
                                returnMsg = "Member Login Successful";
                                if (server.isAAdmin(username)) {
                                    returnMsg = "Admin Login Successful";
                                }
                                loggedIn = true;
                            } else {
                                responseCode = 403;
                                returnMsg = "That user is not permitted to connect to this server.";
                            }
                        } else {
                            //PASSWORD INCORRECT
                            returnMsg = "Incorrect Username/Password details";
                            responseCode = 401;
                        }
                    } else {
                        responseCode = 423;
                        returnMsg = "That user is already logged in";
                    }
                } else if (commandWords.length == 2 && isAUser) {
                    boolean successful = false;
                    try {
                        server.createGuestAccount(username);
                        successful = server.login(username, "GUESTSESSION");
                    } catch (ConnectException ex) {
                        Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (successful) {
                        System.out.println("Guest logged as: " + username);
                        responseCode = 100;
                        returnMsg = "Guest Login Successful";
                        loggedIn = true;
                    }
                } else {
                    //USERNAME INCORRECT
                    responseCode = 401;
                    returnMsg = "Incorrect Username/Password details";
                }
            } else {
                responseCode = 400;
                returnMsg = "You must provide a username/password";
                //try (ObjectOutputStream objs = new ObjectOutputStream(os)) {
                //    objs.writeObject(account);
                //}
            }
        } else if (commandWords[0].contains("SETUP-ACCOUNT")) {
            if (commandWords.length >= 2) {
                String username = commandWords[1];
                username = commandWords[1];
                boolean isAUser = false;
                try {
                    isAUser = server.isAUser(username);
                } catch (ConnectException e) {
                }
                if (!isAUser) {
                    if (commandWords.length >= 3) {
                        String password = commandWords[2];
                        boolean creationSuccess = false;
                        try {
                            server.createAccount(username, password);
                            creationSuccess = server.isAUser(username);
                        } catch (ConnectException ex) {
                            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        responseCode = 400;
                        returnMsg = "Incorrect account creation details";
                        if (creationSuccess) {
                            responseCode = 201;
                            returnMsg = "Account creation success";
                        }
                    } else {
                        boolean guestCreationSuccess = false;
                        try {
                            server.createGuestAccount(username);
                            guestCreationSuccess = server.login(username, "GUESTSESSION");
                        } catch (ConnectException ex) {
                            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (guestCreationSuccess) {
                            responseCode = 201;
                            returnMsg = "Guest account creation success";
                        }
                    }

                } else {
                    responseCode = 202;
                    returnMsg = "That username is already in usage";
                }
            } else {
                returnMsg = "You must provide a username/password";
            }
        } else if (commandWords[0].contains("PING")) {
            responseCode = 100;
            returnMsg = "Server is open";
        } else if (commandWords[0].contains("ROOM-LISTINGS")) {
            responseCode = 501;
            returnMsg = "This feature has been discontinued";
        } else {
            responseCode = 501;
            returnMsg = "Feature is not yet implemented";
        }

        //"SETUP-CONNECTION {username] <password>}
        //#ACCEPTED/DENIED {ACCEPTED: [RoomHandler] <Account>}
        returnMsg = responseCode(responseCode) + returnMsg;
        return returnMsg;
    }

    public String recieveRequest() throws IOException {
        InputStreamReader insr = new InputStreamReader(ins);
        BufferedReader br = new BufferedReader(insr);
        String entireRequest = null;
        String nextLine = "";
        int timeOut = 0;
        try {
            if (br.ready()) {
                entireRequest = "";
                while (timeOut < timeOutPeriod) {
                    if ((nextLine = br.readLine()) != null) {
                        entireRequest = entireRequest + nextLine;
                        if (!br.ready()) {
                            break;
                        } else {
                            entireRequest = entireRequest + "\n";
                        }
                    } else {
                        timeOut++;
                        Thread.sleep(1);
                    }
                }
            }
        } catch (InterruptedException e) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, e.toString(), e);
        } catch (IOException e) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, e.toString(), e);
        }
        return entireRequest;
    }

    public int getConnectionId() {
        return connectionid;
    }

    public boolean sendMessage(String msg) {
        try {
            String writeMsg = msg + "\n";
            BufferedOutputStream bos = new BufferedOutputStream(os);
            OutputStreamWriter osw = new OutputStreamWriter(bos, "UTF-8");
            osw.write(writeMsg);
            osw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String responseCode(int code) {
        String returnString;
        switch (code) {
            case 100:
                returnString = "100 REQUEST VALID";
                break;
            case 201:
                returnString = "201 ACCOUNT CREATION SUCCESS";
                break;
            case 202:
                returnString = "202 ACCOUNT CREATE UNSUCCESSFUL";
                break;
            case 400:
                returnString = "400 REQUEST SYNTAX ERROR";
                break;
            case 401:
                returnString = "401 INCORRECT LOGIN DETAILS";
                break;
            case 404:
                returnString = "404 CANNOT LOCATE RESOURCE";
                break;
            case 501:
                returnString = "501 NOT YET IMPLEMENTED";
                break;
            case 502:
                returnString = "502 SERVER ERROR";
                break;
            case 403:
                returnString = "403 NOT PERMITTED";
                break;
            case 423:
                returnString = "423 USER ALREADY LOGGED IN";
                break;
            default:
                returnString = "502 SERVER ERROR";
                break;
        }
        return returnString = returnString + " ";
    }
}
