/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.ConclaveHandlers;

import conclave.Conclave;
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
public class ConnectionHandler implements Runnable{
    
    private Socket sock;
    private Conclave server;
    private int connectionid;
    
    private InputStream ins;
    private OutputStream os;
    private final int timeOutPeriod = 3000;
    public ConnectionHandler(Socket isock, int id)
    {
        try {
        this.sock = isock;
        ins = sock.getInputStream();
        os = sock.getOutputStream();
        this.server = Conclave.getInstance();
        this.connectionid = id;
        } catch (IOException e)
        {
            
        }
    }
    
    @Override
    public void run()
    {
        try {
        while (sock.isConnected()) {
        String commandLine = recieveRequest();
        if (commandLine!=null)
        {
            String returnMsg = handleRequest(commandLine);
            sendMessage(returnMsg);
        }
        }
        } catch (IOException e)
        {
            System.out.println("Empty Request");
        }
    }
    
    public String handleRequest(String request)
    {
        int responseCode = 502;
        String returnMsg = "";
        String[] commandWords = request.split("\\s+");
        System.out.println(commandWords[0]);
        if (commandWords[0].contains("SETUP-CONNECTION"))
            { //BEGIN LOGIN
                        String username = "";
                        String password = "";
                        if(commandWords.length >=  2) 
                        {
                            username = commandWords[1];
                        if (commandWords.length ==  3 && server.isAUser(username))
                        {
                            password = commandWords[2];
                            boolean correctDetails = false;
                                try {
                                    correctDetails = server.login(username, password);
                                } catch (java.rmi.ConnectException ex) {
                                    Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            if (correctDetails) {
                                System.out.println("User logged in: " + username);
                                responseCode = 100;
                                returnMsg = "Member Login Successful";
                                if (server.isAAdmin(username))
                                {
                                    returnMsg = "Admin Login Successful";
                                }
                            } else
                            {
                                //PASSWORD INCORRECT
                                returnMsg = "Incorrect Username/Password details";
                                responseCode = 401;
                            }
                        } else if (commandWords.length == 2 && !server.isAUser(username)) {
                            System.out.println("Guest");
                            server.createGuestAccount(username);
                            boolean successful = false;
                                try {
                                    successful = server.login(username, "GUESTSESSION");
                                } catch (java.rmi.ConnectException ex) {
                                    Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                if (successful)
                                {
                                    System.out.println("Guest logged as: " + username);
                                    responseCode = 100;
                                    returnMsg = "Guest Login Successful";
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
                if (commandWords.length >=  2)
                {
                    String username = commandWords[1];
                    if (!server.isAUser(username))
                    {
                        if (commandWords.length >= 3)
                        {
                        String password = commandWords[2];
                        server.createAccount(username, password);
                        boolean creationSuccess = false;
                            try {
                                creationSuccess = server.login(username, password);
                            } catch (java.rmi.ConnectException ex) {
                                Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        responseCode = 400;
                        returnMsg = "Incorrect account creation details";
                        if (creationSuccess)
                        {
                            responseCode = 201;
                            returnMsg = "Account creation success";
                        }
                        } else {
                        server.createGuestAccount(username);
                        boolean guestCreationSuccess = false;
                            try {
                                guestCreationSuccess = server.login(username, "GUESTSESSION");
                            } catch (java.rmi.ConnectException ex) {
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
                }
            } else if (commandWords[0].contains("PING")){
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
            System.out.println(returnMsg);
            return returnMsg;
        }
    
    public String recieveRequest() throws IOException
    {
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
                        System.out.println("Completed in: " + timeOut + "ms");
                        System.out.println("Recieved: " + entireRequest);
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
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return entireRequest;
    }
    
    public String getUI(String userID)
    {
        //server.getNewInterface(userID);
        return null;
    }
    
    public int getConnectionId()
    {
        return connectionid;
    }
    
    public boolean sendMessage(String msg)
    {
        try {
        String writeMsg = msg + "\n";
        BufferedOutputStream bos = new BufferedOutputStream(os);
        OutputStreamWriter osw = new OutputStreamWriter(bos, "UTF-8");
        osw.write(writeMsg);
        osw.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    public String responseCode(int code)
    {
        String returnString;
        switch (code) {
            case 100:  returnString = "100 REQUEST VALID";
                     break;
            case 201:  returnString = "201 ACCOUNT CREATION SUCCESS";
                     break;
            case 202:   returnString = "202 ACCOUNT CREATE UNSUCCESSFUL";
                    break;
            case 400:  returnString = "400 REQUEST SYNTAX ERROR";
                     break;
            case 401:  returnString = "401 INCORRECT LOGIN DETAILS";
                     break;
            case 404:  returnString = "404 CANNOT LOCATE RESOURCE";
                     break;
            case 501:  returnString = "501 NOT YET IMPLEMENTED";
                     break;
            case 502:  returnString = "502 SERVER ERROR";
                     break;
            default: returnString = "502 SERVER ERROR";
                     break;
        }
        return returnString = returnString + " ";
    }
}
