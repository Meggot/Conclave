/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.ConclaveHandlers;

import conclave.db.Account;
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
public class AccountManagerTest {
    
    public AccountManagerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
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

    /**
     * Test of getAccount method, of class AccountManager.
     */
    @Test
    public void testGetAccount() {
        System.out.println("getAccount");
        int userid = 0;
        AccountManager instance = new AccountManager();
        Account expResult = null;
        Account result = instance.getAccount(userid);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addAccount method, of class AccountManager.
     */
    @Test
    public void testAddAccount() {
        System.out.println("addAccount");
        String username = "";
        String PTpassword = "";
        AccountManager instance = new AccountManager();
        instance.addAccount(username, PTpassword);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUserByName method, of class AccountManager.
     */
    @Test
    public void testGetUserByName() {
        System.out.println("getUserByName");
        String username = "";
        AccountManager instance = new AccountManager();
        Account expResult = null;
        Account result = instance.getUserByName(username);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of verifyUser method, of class AccountManager.
     */
    @Test
    public void testVerifyUser() {
        System.out.println("verifyUser");
        String iusername = "";
        String ptPassword = "";
        AccountManager instance = new AccountManager();
        boolean expResult = false;
        boolean result = instance.verifyUser(iusername, ptPassword);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isAUser method, of class AccountManager.
     */
    @Test
    public void testIsAUser() {
        System.out.println("isAUser");
        String username = "";
        AccountManager instance = new AccountManager();
        boolean expResult = false;
        boolean result = instance.isAUser(username);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listAllUsers method, of class AccountManager.
     */
    @Test
    public void testListAllUsers() {
        System.out.println("listAllUsers");
        AccountManager instance = new AccountManager();
        String expResult = "";
        String result = instance.listAllUsers();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
