package conclaveloadtester;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author BradleyW
 */
public class ResultEntry {
    
    private long responseTime;
    private String request;
    private int resultEntry;
    
    public ResultEntry(String request, int entry, long responseTime)
    {
        this.responseTime = responseTime;
        this.request = request;
        this.resultEntry = entry;
    }
    
    @Override
    public String toString()
    {
        return resultEntry + ": " + request + " @ " + responseTime;
    }
    
    public long getResponseTime()
    {
        return responseTime;
    }
    
    public int getRequestNumber()
    {
        return resultEntry;
    }
}
