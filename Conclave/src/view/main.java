/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import conclave.ConclaveHandlers.SecurityHandler;
import conclave.controllers.ServerController;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Encryptor;

/**
 *
 * @author BradleyW
 */
public class main {

    public static void main(String[] args) {
        ServerTerminal st = new ServerTerminal();
        st.setVisible(true);
    }
}
