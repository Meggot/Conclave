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
public class LoginControllerKeyTests {
    
    //Static Secret Key Variables for AES encryption setup.
    private static final String iip = "192.168.0.15";
    private static final String iport = "20003";
    public LoginControllerKeyTests() {
    }
    
    @Before
    public void setUp() {
        
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of connect method, of class LoginController.
     */
    @Test
    public void testKey1() {
        String secKey = "B55E4C33045B62AC907529233ADAAD6C";
        String secKeyUsers = "00001";
        LoginController instance = new LoginController(secKey, secKeyUsers);
        instance.connect(iip, iport);
        String expResult = "403 NOT PERMITTED Invalid key given";
        String result = instance.connect(iip, iport);
        assertEquals(expResult, result);
    }  
    
    @Test
    public void testKey2() {
        String secKey = "B55E4C33045B62AC907529233ADAAD6C";
        String secKeyUsers = "00036";
        LoginController instance = new LoginController(secKey, secKeyUsers);
        instance.connect(iip, iport);
        String expResult = "Connected to server at: /" + iip +":"+ iport;
        String result = instance.connect(iip, iport);
        assertEquals(expResult, result);
    } 
    
}