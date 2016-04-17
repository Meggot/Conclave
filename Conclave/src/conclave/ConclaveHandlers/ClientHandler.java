/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.ConclaveHandlers;

import conclave.controllers.ServerController;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This ClientHandler class will receive and send Socket messages, this will
 * handle request like: PING, LOGIN (username) (password), SETUP-ACCOUNT
 * (username) (password).
 *
 * The response will return a HTTP request to the socket, and using a Switch
 * method will also return a String response.
 *
 * @author BradleyW
 */
public class ClientHandler implements Runnable {

    private Socket socket;
    private ServerController server;

    public ClientHandler(Socket sock) {
        this.socket = sock;
        this.server = ServerController.getInstance();
    }

    /**
     * This run handles the request, returns a response and then closes the
     * socket.
     */
    @Override
    public void run() {
        try {
            String request = recieveRequest();
            String response = handleRequest(request);
            if (request == null) {
                System.out.println("NULLREQUEST");
            } else {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.INFO,
                        "ClientHandler: Thread '"
                        + Thread.currentThread().toString()
                        + "' received message: " + request
                        + "' response sent: " + response);
            }
            sendResponse(response);
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * sends a response to an already established Socket.
     *
     * @param msg
     * @return
     */
    public boolean sendResponse(String msg) {
        try {
            String writeMsg = msg + "\n";
            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
            OutputStreamWriter osw = new OutputStreamWriter(bos, "UTF-8");
            osw.write(writeMsg);
            osw.flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Recieves a request and returns the request as a string.
     *
     * @return
     * @throws IOException
     */
    public String recieveRequest() throws IOException {
        InputStreamReader insr = new InputStreamReader(socket.getInputStream());
        BufferedReader br = new BufferedReader(insr);
        String entireRequest = "";
        String nextLine = null;
        try {
            while (true) {
                Thread.sleep(10);
                if ((nextLine = br.readLine()) != null) {
                    entireRequest = entireRequest + nextLine;
                    if (!br.ready()) {
                        break;
                    } else {
                        entireRequest = entireRequest + "\n";
                    }
                }
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return entireRequest;
    }

    /**
     * deals with a string request and returns a valid response.
     *
     * @param request
     * @return
     */
    private String handleRequest(String request) {
        String returnMsg = "";
        int responseCode = 502;
        if (request != null) {
            String[] commandWords = request.split("\\s+");
            if (commandWords.length <= 0) {
                responseCode = 400;
                returnMsg = "Empty Request";
            } else if (commandWords[0].equals("PING")) {
                responseCode = 100;
                returnMsg = "Server is ready for connections";
            } else if (commandWords[0].equals("LOGIN")) {
                if (commandWords.length <= 2) {
                    returnMsg = "No Username/Password given";
                    responseCode = 400;
                } else if (commandWords.length == 3) {
                    try {
                        String username = commandWords[1];
                        String password = commandWords[2];
                        if (!server.isAUser(username)) {
                            responseCode = 404;
                            returnMsg = "A user by that username could not be found";
                        } else if (server.isUserLoggedIn(username)) {
                            responseCode = 423;
                            returnMsg = "That user is already logged in";
                        } else if (server.isBanned(username)) {
                            responseCode = 403;
                            returnMsg = "That user is banned from the server";
                        } else {
                            boolean loggedIn = server.login(username, password);
                            if (loggedIn) {
                                responseCode = 100;
                                if (server.isAAdmin(username)) {
                                    returnMsg = "You have logged in as an Admin: " + username;
                                } else {
                                    returnMsg = "You have logged in as a User: " + username;
                                }
                            } else {
                                responseCode = 401;
                                returnMsg = "Incorrect username/password";
                            }
                        }
                    } catch (ConnectException ex) {
                        Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else if (commandWords[0].equals("SETUP-ACCOUNT")) {
                System.out.println("setting up account");
                if (commandWords.length <= 2) {
                    returnMsg = "No username or password given";
                    responseCode = 400;
                } else if (commandWords.length == 3) {
                    String username = commandWords[1];
                    String password = commandWords[2];
                    if (username.length() > 5 && password.length() > 5) {
                        if (username.length() < 15 && password.length() < 15) {
                            try {
                                if (server.isAUser(username)) {
                                    returnMsg = "That user already exists";
                                    responseCode = 202;
                                } else {
                                    boolean creationSuccess = false;
                                    try {
                                        server.createAccount(username, password);
                                        creationSuccess = server.isAUser(username);
                                    } catch (ConnectException ex) {
                                        Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    responseCode = 400;
                                    returnMsg = "Incorrect account creation details";
                                    if (creationSuccess) {
                                        responseCode = 100;
                                        returnMsg = "Account creation success";
                                    }
                                }
                            } catch (ConnectException e) {

                            }
                        } else {
                            responseCode = 400;
                            returnMsg = "Username and Password must be less than 15 characters long";
                        }
                    } else {
                        responseCode = 400;
                        returnMsg = "Username and Password must be atleast 5 characters long";
                    }
                }
            } else {
                responseCode = 400;
                returnMsg = "That request is not recognized.";
            }
        }
        return responseCode(responseCode) + returnMsg;
    }

    /**
     * changes a response code into a String response.
     *
     * @param code
     * @return
     */
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
        returnString = returnString + " ";
        return returnString;
    }
}
