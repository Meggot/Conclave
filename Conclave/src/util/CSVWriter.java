/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BradleyW
 */
public class CSVWriter {

    PrintWriter writer;
    private Socket sock;

    private InetAddress ip;
    private int port;
    public CSVWriter(String logName, InetAddress ip, int port) {
        String fileName = "PerformanceLog_" + logName + ".csv";
        sock = null;
        this.ip = ip;
        this.port = port;
        loadWriter(fileName);
    }

    public void loadWriter(String filename) {
        try {
            writer = new PrintWriter(filename, "UTF-8");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSVWriter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CSVWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        writeToFile("Tick Count, Memory Used %, Response Time, Client Handlers, Logged Users, Thread Count");
    }

    public void writeToFile(String dataSet) {
        writer.println(dataSet);
    }

    public void systemLog(int tick, int anonUsers, int loggedUsers) {
        long responseTime = responseTime();
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long memoryHeld = runtime.totalMemory();
        float percentage = (float) ((memoryHeld * 100) / maxMemory);
        String csvString = tick + ","
                + percentage + ","
                + responseTime + ","
                + anonUsers + ","
                + loggedUsers + ","
                + java.lang.Thread.activeCount();
        writeToFile(csvString);
    }

    public long responseTime() {
        initateResponseSocket(ip, port);
        long returnLong = 0;
        try {
            OutputStream os = sock.getOutputStream();
            InputStream is = sock.getInputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os);
            OutputStreamWriter osw = new OutputStreamWriter(bos);
            String msg = "PING" + "\n";
            long startTime = System.currentTimeMillis();
            osw.write(msg);
            osw.flush();
            String responseString = readStream(is);
            if (responseString != null) {
                returnLong = System.currentTimeMillis() - startTime;
            } else {
                close();
            }
        } catch (IOException ex) {
            Logger.getLogger(CSVWriter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return returnLong;
    }

    public String readStream(InputStream is) {
        InputStreamReader insr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(insr);
        String entireRequest = null;
        String nextLine = "";
        int timeOut = 0;
        try {
            while (timeOut < 1000) {
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entireRequest;
    }

    public void close() {
        writer.flush();
        writer.close();
    }

    private void initateResponseSocket(InetAddress ip, int port) {
        try {
            sock = new Socket(ip, port);
        } catch (IOException ex) {
            Logger.getLogger(CSVWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
