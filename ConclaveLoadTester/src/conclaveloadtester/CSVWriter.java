/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclaveloadtester;

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
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BradleyW
 */
public class CSVWriter {

    PrintWriter writer;

    public CSVWriter(String logName) {
        String fileName = "TestName_" + logName + ".csv";
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
        writeToFile("Test Number, Request, Mean Response Time, Min, Max");
    }

    public void writeToFile(String dataSet) {
        writer.println(dataSet);
    }

    public void systemLog(int testNumber, String request, long responseTime, long min, long max) {
        String csvString = testNumber + ","
                + request + ","
                + responseTime + ","
                + min + "," +
                + max;
        writeToFile(csvString);
    }
    
    public void close() {
        writer.flush();
        writer.close();
    }

}
