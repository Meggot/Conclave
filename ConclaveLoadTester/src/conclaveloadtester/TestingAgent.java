package conclaveloadtester;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
public class TestingAgent {

    private static ArrayList<PacketUtil> loadAgents;
    private static ArrayList<String> requests;
    private static ArrayList<ResultEntry> results;
    CSVWriter writer;

    public TestingAgent() 
    {
        loadAgents = new ArrayList();
        results = new ArrayList();
        requests = new ArrayList();
    }
    
    public boolean createNewTest(String testname)
    {
        writer = new CSVWriter(testname);
        return true;
    }
    
    public void addRequest(String request)
    {
        requests.add(request);
    }
    
    public void removeRequest(String request)
    {
        requests.remove(request);
    }
    
    public String allRequests() {
        String returnString = "";
        for (String req : requests)
        {
            returnString = returnString + req + "\n";
        }
        return returnString;
    }

    public boolean beginTest(int iterations){
        int testNumber = 0;
        long timeDiff = 0;
        for (String req : requests) {
            results.clear();
            testNumber++;
            for (int i = 0; i < iterations; i++) {
                PacketUtil newAgent;
                ResultEntry newEntry;
                try {
                    newAgent = new PacketUtil(InetAddress.getByName("192.168.0.24"), 20003);
                    //loadAgents.add(newAgent);
                    long startTime = System.currentTimeMillis();
                    newAgent.sendPacketRequest(req);
                    String response = newAgent.readStream();
                    timeDiff = System.currentTimeMillis() - startTime;
                    /**
                    System.out.println("--LoadAgent: " + i + "--"
                            + "\n Request: " + req
                            + "\n Response: " + response
                            + "\n ResponseTime: " + timeDiff + "ms");
                            * **/
                    newAgent.close();
                    newEntry = new ResultEntry(req, i, timeDiff);
                    results.add(newEntry);
                } catch (IOException ex) {
                    Logger.getLogger(TestingAgent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            long sum = 0;
            long max = 0;
            long min = timeDiff; //Instead of initilizing this at 0, which would always make in the miniumum, we'll set it to the last entry.
            long entry = 0;
            for (ResultEntry res : results) {
                entry = res.getResponseTime();
                if (entry > max)
                {
                    max = entry;
                } else if (entry < min)
                {
                    min = entry;
                } 
                sum = sum + entry;
            }
            long avg = (sum / results.size());
            writer.systemLog(testNumber, req, avg, min, max);
        }
        writer.close();
        return true;
    }
}
