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

/**This class is responsible for writing performance information into a .csv file,
 * for use in load testing and performance analysis.
 *
 * @author BradleyW
 */
public class CSVWriter {

    PrintWriter writer; //Printwriter used to write to a txt file.
    private Socket sock; //Socket used to find response times.

    private InetAddress ip; //Server IP.
    private int port; //Server port.
    
    /**
     * Initiate a CSV writer with a ip, port and a logname which serves as the filename
     * @param logName
     * @param ip
     * @param port 
     */
    public CSVWriter(String logName, InetAddress ip, int port) {
        String fileName = "PerformanceLog_" + logName + ".csv";
        sock = null;
        this.ip = ip;
        this.port = port;
        loadWriter(fileName);
    }

    /**
     * Loads a writer, handles if the file doesn't exist or does and overwrites.
     * @param filename 
     */
    public void loadWriter(String filename) {
        try {
            writer = new PrintWriter(filename, "UTF-8");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSVWriter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CSVWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        writeToFile("Tick Count, Memory Used %, Response Time, Requests, Logged Users, Thread Count");
    }

    /**
     * Write a line to the CSV, this is usually done as: entry, entry, entry, entry
     * The line carrage is done automatically
     * @param dataSet 
     */
    public void writeToFile(String dataSet) {
        writer.println(dataSet);
    }

    /**
     * Write a system log to the specified writer file.
     * 
     * @param tick
     * @param requestsInTick
     * @param loggedUsers 
     */
    public void systemLog(int tick, int requestsInTick, int loggedUsers) {
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

    /**
     * Finds out the response time in ms.
     * @return 
     */
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

    /**
     * Reads an entry from the socket, this is used to find out if there is a response,
     * the String it reads is irrelevant, but should get a response.
     * @param is
     * @return 
     */
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

    /**
     * Closes the writer and saves the file, this must be called at the end of the logging
     *time else the file will be unreadable.
     */
    public void close() {
        writer.flush();
        writer.close();
    }
    /**
     * Starts up the socket, this is done every response time to find out if the server goes
     * down or not.
     * @param ip
     * @param port 
     */
    private void initateResponseSocket(InetAddress ip, int port) {
        try {
            sock = new Socket(ip, port);
        } catch (IOException ex) {
            Logger.getLogger(CSVWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
