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
public class LoginControllerConnectTests {
    
    //Static Secret Key Variables for AES encryption setup.
    private static final String secKey = "B55E4C33045B62AC907529233ADAAD6C";
    private static final String secKeyUser = "00036";
    
    public LoginControllerConnectTests() {
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
    public void testConnect1() {
        System.out.println("connect");
        String iip = "192.168.0.15";
        String iport = "20003";
        LoginController instance = new LoginController(secKey, secKeyUser);
        String expResult = "Connected to server at: /" + iip +":"+ iport;
        String result = instance.connect(iip, iport);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testConnect2() {
        System.out.println("connect");
        String iip = "192.168.0.8";
        String iport = "20003";
        LoginController instance = new LoginController(secKey, secKeyUser);
        String expResult = "Cannot find a conclave server on that IP or Port";
        String result = instance.connect(iip, iport);
        assertEquals(expResult, result);
    }
    @Test
    public void testConnect3() {
        System.out.println("connect");
        String iip = "192.168.0.3";
        String iport = "20004";
        LoginController instance = new LoginController(secKey, secKeyUser);
        String expResult = "Cannot find a conclave server on that IP or Port";
        String result = instance.connect(iip, iport);
        assertEquals(expResult, result);
    }
    @Test
    public void testConnect4() {
        System.out.println("connect");
        String iip = "192.168.0.8";
        String iport = "ACDE";
        LoginController instance = new LoginController(secKey, secKeyUser);
        String expResult = "Bad format of IP/Port";
        String result = instance.connect(iip, iport);
        assertEquals(expResult, result);
    }
    @Test
    public void testConnect5() {
        System.out.println("connect");
        String iip = "ACDE";
        String iport = "20003";
        LoginController instance = new LoginController(secKey, secKeyUser);
        String expResult = "Bad format of IP/Port";
        String result = instance.connect(iip, iport);
        assertEquals(expResult, result);
    }
    @Test
    public void testConnect6() {
        System.out.println("connect");
        String iip = "ACDE";
        String iport = "ACDE";
        LoginController instance = new LoginController(secKey, secKeyUser);
        String expResult = "Bad format of IP/Port";
        String result = instance.connect(iip, iport);
        assertEquals(expResult, result);
    }
    @Test
    public void testConnect7() {
        System.out.println("connect");
        String iip = "";
        String iport = "20003";
        LoginController instance = new LoginController(secKey, secKeyUser);
        String expResult = "Bad format of IP/Port";
        String result = instance.connect(iip, iport);
        assertEquals(expResult, result);
    }@Test
    public void testConnect8() {
        System.out.println("connect");
        String iip = "192.168.0.3";
        String iport = "";
        LoginController instance = new LoginController(secKey, secKeyUser);
        String expResult = "Bad format of IP/Port";
        String result = instance.connect(iip, iport);
        assertEquals(expResult, result);
    }
    @Test
    public void testConnect9() {
        System.out.println("connect");
        String iip = "";
        String iport = "";
        LoginController instance = new LoginController(secKey, secKeyUser);
        String expResult = "Bad format of IP/Port";
        String result = instance.connect(iip, iport);
        assertEquals(expResult, result);
    }
    
}