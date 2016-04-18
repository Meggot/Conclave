/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclaveclient.Handlers.utlity;

import conclaveclient.security.Encryptor;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BradleyW
 */
public class PacketUtil {

    private Socket outSocket;
    private InetAddress ip;
    private int port;
    private final int timeOutPeriod = 2000;
    private String secKey;
    private String skeyUser;

    public PacketUtil(InetAddress ip, int port, String skey, String skeyUser) throws IOException {
        this.ip = ip;
        this.port = port;
        outSocket = new Socket(ip, port);
        outSocket.setSoTimeout(timeOutPeriod);
        this.secKey = skey;
        this.skeyUser = skeyUser;
    }

    public void refreshSocket() {
        try {
            outSocket = new Socket(ip, port);
        } catch (IOException e) {
        }
    }

    public void setConnectionDetails(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
        refreshSocket();
    }

    public void sendPacketRequest(String request) throws UnknownHostException {
        try {
            String encMessage = Encryptor.encrypt(request, secKey);
            String writeMsg = skeyUser + encMessage
                    + "\n";
            BufferedOutputStream bos = new BufferedOutputStream(outSocket.getOutputStream());
            OutputStreamWriter osw = new OutputStreamWriter(bos, "UTF-8");
            System.out.println("Sending: " + writeMsg);
            osw.write(writeMsg);
            osw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readStream() {
        String entireRequest = "";
        try {
            InputStreamReader insr = new InputStreamReader(outSocket.getInputStream());
            BufferedReader br = new BufferedReader(insr);
            String nextLine = "";
            int timeOut = 0;
            while (timeOut < timeOutPeriod) {
                if ((nextLine = br.readLine()) != null) {
                    entireRequest = entireRequest + nextLine;
                    if (!br.ready()) {
                        break;
                    } else {
                        entireRequest = entireRequest + "\n";
                    }
                } else {
                    timeOut++;
                    Thread.sleep(1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException ex) {
            Logger.getLogger(PacketUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        String res;
        if (!entireRequest.contains("403"))
        {
            res = Encryptor.decrypt(entireRequest, secKey);
            System.out.println("Recieving: " + res);
        } else {
            res = entireRequest;
        }
        return res;
    }

    public void close() {
        try {
            outSocket.close();
        } catch (IOException e) {

        }
    }
}
