/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclaveclient;

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
public class LoginControllerCreateAccountTest {

    private static LoginController controller;

    public LoginControllerCreateAccountTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        controller = new LoginController();
        controller.connect("192.168.0.15", "20003");
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCreateAccount1() {
        String username = "TestAccount";
        String password = "password1421";
        String expResult = "100 REQUEST VALID Account creation success";
        String result = controller.createAccount(username, password);
        assertEquals(expResult, result);
    }

    @Test
    public void testCreateAccount2() {
        String username = "923464252sf1f";
        String password = "3515sf1fe1f1";
        String expResult = "100 REQUEST VALID Account creation success";
        String result = controller.createAccount(username, password);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testCreateAccount3() {
        String username = "ADdsa&£$!&*$";
        String password = "afga$faf3a";
        String expResult = "100 REQUEST VALID Account creation success";
        String result = controller.createAccount(username, password);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testCreateAccount4() {
        String username = "UserAdam";
        String password = "password123";
        String expResult = "202 ACCOUNT CREATE UNSUCCESSFUL That user already exists";
        String result = controller.createAccount(username, password);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testCreateAccount5() {
        String username = "123456";
        String password = "123";
        String expResult = "400 REQUEST SYNTAX ERROR Username and Password must be atleast 5 characters long";
        String result = controller.createAccount(username, password);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testCreateAccount6() {
        String username = "123";
        String password = "123456";
        String expResult = "400 REQUEST SYNTAX ERROR Username and Password must be atleast 5 characters long";
        String result = controller.createAccount(username, password);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testCreateAccount7() {
        String username = "1234567890123456";
        String password = "123456";
        String expResult = "400 REQUEST SYNTAX ERROR Username and Password must be less than 15 characters long";
        String result = controller.createAccount(username, password);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testCreateAccount8() {
        String username = "thixists2";
        String password = "1234567890123456";
        String expResult = "400 REQUEST SYNTAX ERROR Username and Password must be less than 15 characters long";
        String result = controller.createAccount(username, password);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testCreateAccount9() {
        String username = "";
        String password = "123456789012345";
        String expResult = "400 REQUEST SYNTAX ERROR No username or password given.";
        String result = controller.createAccount(username, password);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testCreateAccount10() {
        String username = "thixists";
        String password = "";
        String expResult = "400 REQUEST SYNTAX ERROR No username or password given.";
        String result = controller.createAccount(username, password);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testCreateAccount11() {
        String username = "";
        String password = "";
        String expResult = "400 REQUEST SYNTAX ERROR No username or password given.";
        String result = controller.createAccount(username, password);
        assertEquals(expResult, result);
    }
}
