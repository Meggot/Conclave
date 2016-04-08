/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclaveloadtester;

import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BradleyW
 */
public class main {
    public static void main(String[] args) {
        LoadTesterGui gui = new LoadTesterGui();
        gui.setVisible(true);
//        String uniqueName = "";
//        String request = "";
//        String response = "";
//        for (int i = 0; i < 500; i++)
//        {
//            uniqueName = "TESTaccount" + i;
//            try {
//                request = "LOGIN " + uniqueName + " password";
//                PacketUtil packet = new PacketUtil(InetAddress.getByName("192.168.0.24"), 20003);
//                packet.sendPacketRequest(request);
//                response = packet.readStream();
//                System.out.println("---------- REQ: " + i + "---------");
//                System.out.println("REQUEST: " + request);
//                System.out.println("RESPONSE: " + response);
//            } catch (IOException ex) {
//                Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
    }
}
