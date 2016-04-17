package conclaveclient;

import conclaveinterfaces.IAdminInterface;
import conclaveinterfaces.IUserInterface;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Message;
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
public class AdminInteractionTests {

    private static IAdminInterface ui;
    private static LoginController controller;

    public AdminInteractionTests() {
    }

    @BeforeClass
    public static void setUpClass() {
        controller = new LoginController();
        controller.connect("192.168.0.15", "20003");
        controller.createAccount("AdminAdam", "password123");
        try {
            controller.login("AdminAdam", "password123");
            Registry registry = LocateRegistry.getRegistry("192.168.0.15", 9807);
            ui = (IAdminInterface) registry.lookup("AdminAdam");
        } catch (RemoteException ex) {
            Logger.getLogger(AdminInteractionTests.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotBoundException ex) {
            Logger.getLogger(AdminInteractionTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() throws RemoteException {
        if (ui.inRoom()) {
            ui.leaveRoom();
        }
    }

    @Test
    public void adminInteractionTest1() {
        try {
            ui.addRoom("newRoom", 1);
            boolean successfull = ui.joinRoom("newRoom");
            assertEquals(successfull, true);
        } catch (RemoteException ex) {
            Logger.getLogger(AdminInteractionTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void adminInteractionTest2() {
        try {
            ui.addRoom("newRoom", 1);
            List<String> names = ui.getRoomNames();
            for (String name : names) {
                System.out.println(name);
            }
            boolean successfull = false;
            assertEquals(successfull, true);
        } catch (RemoteException ex) {
            Logger.getLogger(AdminInteractionTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
