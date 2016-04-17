package conclaveclient;

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
public class UserInteractionTests {

    private static IUserInterface ui;
    private static LoginController controller;

    public UserInteractionTests() {
    }

    @BeforeClass
    public static void setUpClass() {
        controller = new LoginController();
        controller.connect("192.168.0.15", "20003");
        controller.createAccount("UserAdam", "password123");
        try {
            controller.login("UserAdam", "password123");
            Registry registry = LocateRegistry.getRegistry("192.168.0.15", 9807);
            ui = (IUserInterface) registry.lookup("UserAdam");
        } catch (RemoteException ex) {
            Logger.getLogger(UserInteractionTests.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotBoundException ex) {
            Logger.getLogger(UserInteractionTests.class.getName()).log(Level.SEVERE, null, ex);
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
        if (ui.inRoom())
        {
            ui.leaveRoom();
        }
    }

    @Test
    public void joinRoomTest1() {
        try {
            ui.joinRoom("Atrium");
            boolean successfull = ui.inRoom();
            assertEquals(successfull, true);
        } catch (RemoteException ex) {
            Logger.getLogger(UserInteractionTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void joinRoomTest2() {
        try {
            ui.joinRoom("Atrium");
            ui.leaveRoom();
            boolean successfull = ui.inRoom();
            assertEquals(successfull, false);
        } catch (RemoteException ex) {
            Logger.getLogger(UserInteractionTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void joinRoomTest3() {
        try {
            System.out.println(ui.joinRoom("TestingRoom", "test123"));
            boolean successfull = ui.inRoom();
            assertEquals(successfull, true);
        } catch (RemoteException ex) {
            System.out.println("error");
            Logger.getLogger(UserInteractionTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void joinRoomTest4() {
        try {
            ui.joinRoom("TestingRoom", "wrongpass");
            boolean successful = ui.inRoom();
            assertEquals(successful, false);
        } catch (RemoteException ex) {
            Logger.getLogger(UserInteractionTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void joinRoomTest5() {
        try {
            ui.joinRoom("Locked Conference", "test123");
            ui.leaveRoom();
            boolean successfull = ui.inRoom();
            assertEquals(successfull, false);
        } catch (RemoteException ex) {
            Logger.getLogger(UserInteractionTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void joinRoomTest6() {
        try {
            ui.joinRoom("Closed Room");
            boolean successfull = ui.inRoom();
            assertEquals(successfull, false);
        } catch (RemoteException ex) {
            Logger.getLogger(UserInteractionTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void joinRoomTest7() {
        try {
            boolean successful = true;
            ui.postMessage("Testing Message");
            List<Message> chatUpdate = ui.getChatlogUpdates(0);
            for (Message msg : chatUpdate)
            {
                if (msg.msgDisplay().contains("Testing Message"))
                {
                    successful = false;
                }
            }
            assertEquals(successful, true);
        } catch (RemoteException ex) {
            Logger.getLogger(UserInteractionTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void joinRoomTest8() {
        try {
            boolean successful = false;
            ui.joinRoom("Atrium");
            ui.postMessage("Testing Message");
            List<Message> chatUpdate = ui.getChatlogUpdates(0);
            for (Message msg : chatUpdate)
            {
                if (msg.msgDisplay().contains("Testing Message"))
                {
                    successful = true;
                }
            }
            assertEquals(successful, true);
        } catch (RemoteException ex) {
            Logger.getLogger(UserInteractionTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
