
import conclaveclient.LoginController;
import conclaveclient.LoginGUI;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author BradleyW
 */
public class main {
    
    public static void main(String[] args) throws IOException {
        try {
            File secretKeyLocation = new File("secretkey.txt");
            if (secretKeyLocation==null)
            {
                System.out.println("Cannot load a 'secretkey.txt' file in Client directory");
                KeyInput ki = new KeyInput();
                ki.setVisible(true);
            } else {
                BufferedReader br = new BufferedReader(new FileReader(secretKeyLocation));
                String line;
                while ((line = br.readLine()) != null)
                {
                    String secretKey = line.substring(0, 32);
                    String keyUser = line.substring(32, 37);
                    System.out.println("Skey Found: " + secretKey);
                    System.out.println("Skey User: " + keyUser);
                    LoginController controller = new LoginController(secretKey, keyUser);
                    LoginGUI gui = new LoginGUI(controller);
                    gui.setVisible(true);
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Cannot load a 'secretkey.txt' file in Client directory");
            KeyInput ki = new KeyInput();
            ki.setVisible(true);
        }
    }
}
