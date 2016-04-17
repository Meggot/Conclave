/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclaveclient;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author BradleyW
 */
public class LoginControllerLoginTests {
    
    private static LoginController controller;
    public LoginControllerLoginTests() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        controller = new LoginController();
        controller.connect("192.168.0.15","20003");
        controller.createAccount("UserAdam", "password123");
        controller.createAccount("AdminsAdam", "adminpassw");
        controller.createAccount("KandyKane", "password124");
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        controller.connect("192.168.0.15","20003");
        if (!controller.isConnected())
        {
            System.out.println("Cannot connect to the conclave server");
        }
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testLogin1()
    {
        try {
            String username = "UserAdam";
            String password = "password123";
            String expResult = "100 REQUEST VALID You have logged in as a User: " + username;
            String result = controller.login(username, password);
            assertEquals(expResult, result);
        } catch (RemoteException ex) {
            Logger.getLogger(LoginControllerLoginTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testLogin2()
    {
        try {
            String username = "AdminsAdam";
            String password = "adminpassw";
            String expResult = "100 REQUEST VALID You have logged in as an Admin: " + username;
            String result = controller.login(username, password);
            assertEquals(expResult, result);
        } catch (RemoteException ex) {
            Logger.getLogger(LoginControllerLoginTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testLogin3()
    {
        try {
            String username = "UserAdam";
            String password = "password123";
            String expResult = "423 USER ALREADY LOGGED IN That user is already logged in";
            String result = controller.login(username, password);
            assertEquals(expResult, result);
        } catch (RemoteException ex) {
            Logger.getLogger(LoginControllerLoginTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testLogin4()
    {
        try {
            String username = "KandyKane";
            String password = "wrongpassword";
            String expResult = "401 INCORRECT LOGIN DETAILS Incorrect username/password";
            String result = controller.login(username, password);
            assertEquals(expResult, result);
        } catch (RemoteException ex) {
            Logger.getLogger(LoginControllerLoginTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testLogin5()
    {
        try {
            String username = "-------";
            String password = "Periapsis";
            String expResult = "404 CANNOT LOCATE RESOURCE A user by that username could not be found";
            String result = controller.login(username, password);
            assertEquals(expResult, result);
        } catch (RemoteException ex) {
            Logger.getLogger(LoginControllerLoginTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testLogin6()
    {
        try {
            String username = "-------";
            String password = "Periapsis";
            String expResult = "404 CANNOT LOCATE RESOURCE A user by that username could not be found";
            String result = controller.login(username, password);
            assertEquals(expResult, result);
        } catch (RemoteException ex) {
            Logger.getLogger(LoginControllerLoginTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testLogin7()
    {
        try {
            String username = "";
            String password = "";
            String expResult = "400 REQUEST SYNTAX ERROR No Username/Password given";
            String result = controller.login(username, password);
            assertEquals(expResult, result);
        } catch (RemoteException ex) {
            Logger.getLogger(LoginControllerLoginTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testLogin8()
    {
        try {
            String username = "LOALOSLO";
            String password = "";
            String expResult = "400 REQUEST SYNTAX ERROR No Username/Password given";
            String result = controller.login(username, password);
            assertEquals(expResult, result);
        } catch (RemoteException ex) {
            Logger.getLogger(LoginControllerLoginTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testLogin9()
    {
        try {
            String username = "";
            String password = "LOALOSLO";
            String expResult = "400 REQUEST SYNTAX ERROR No Username/Password given";
            String result = controller.login(username, password);
            assertEquals(expResult, result);
        } catch (RemoteException ex) {
            Logger.getLogger(LoginControllerLoginTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testLogin10()
    {
        try {
            String username = "";
            String password = "LOALOSLO";
            String expResult = "400 REQUEST SYNTAX ERROR No Username/Password given";
            String result = controller.login(username, password);
            assertEquals(expResult, result);
        } catch (RemoteException ex) {
            Logger.getLogger(LoginControllerLoginTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
