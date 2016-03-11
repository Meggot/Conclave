/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclaveclient.Handlers;

import conclave.db.Account;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author BradleyW
 */
public class PacketUtil {
    
    private Socket outSocket;
    private OutputStream os;
    private InputStream is;
    
    private InetAddress ip;
    private int port;
    private final int timeOutPeriod = 3000;
    
    public PacketUtil(InetAddress ip, int port) throws IOException
    {
        this.ip = ip;
        this.port = port;
        outSocket = new Socket(ip, port); 
        os = outSocket.getOutputStream();
        is  = outSocket.getInputStream();
    }
    
    public void refreshSocket()
    {
        try {
        outSocket = new Socket(ip, port); 
        os = outSocket.getOutputStream();
        is  = outSocket.getInputStream();
        }catch (IOException e)
        {
        }
    }
    
    public void setConnectionDetails(InetAddress ip, int port)
    {
        this.ip = ip;
        this.port = port;
        //refreshSocket();
    }
    
    public void sendPacketRequest(String request) throws UnknownHostException
    {
        //refreshSocket();
        try {
            BufferedOutputStream bos = new BufferedOutputStream(os);
            OutputStreamWriter osw = new OutputStreamWriter(bos);
            String msg = request + "\n";
            System.out.println("Request: " + msg);
            osw.write(msg);
            osw.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        } 
    }
    
    public Object getObj()
    {
        Object acct = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(is);
            int timeOut = 0;
            while (outSocket.isConnected())
            {
                acct = (Account) ois.readObject();
                if (acct!=null) {
                    return acct;
                } else if (timeOut > 6000)
                {
                    break;
                }
                Thread.sleep(1);
                timeOut++;
            }
        } catch (IOException e) {
            
        } catch (InterruptedException e)
        {
        } catch (ClassNotFoundException ce)
        {
            
        }
        return acct;
    }
    
    public String readStream()
    {
        InputStreamReader insr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(insr);
        String entireRequest = "";
        String nextLine = "";
        int timeOut = 0;
        try {
            while (timeOut < timeOutPeriod) {
                if ((nextLine = br.readLine()) != null) {
                    entireRequest = entireRequest + nextLine;
                    if (!br.ready()) {
                        System.out.println("Completed in: " + timeOut + "ms");
                        break;
                    } else {
                        entireRequest = entireRequest + "\n";
                    }
                } else {
                timeOut++;
                Thread.sleep(1);
                }
            }
        System.out.println("Recieved: " + entireRequest);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return entireRequest;
    }
    
    public void close()
    {
        try {
            outSocket.close();
        } catch (IOException e)
        {
            
        }
    }
}
