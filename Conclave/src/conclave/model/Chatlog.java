/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.model;

import conclave.interfaces.ChatlogInterface;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author BradleyW
 */
public class Chatlog implements ChatlogInterface, Serializable {
    
    public CopyOnWriteArrayList<Message> textLog;
    
    public Chatlog()
    {
        textLog = new CopyOnWriteArrayList<>();
    }
    
    /**
     * 
     * @param Message 
     */
    @Override
    public void addMessage(Message Message)
    {
        textLog.add(Message);
    }
    
    @Override
    public String viewEntries() 
    {
        String allEntries = "";
        int i = 0;
        for (Message msg : textLog)
        {
            allEntries = allEntries + i + ": " + msg.msgDisplay() + "\n";
            i++;
        }
        return allEntries;
    }
    
    
    /**
     * 
     * @param lstMsgRecievedLine
     * @return 
     */
    @Override
    public LinkedList<Message> getAllEntriesAfter(int lstMsgRecievedLine)
    {
        return new LinkedList<Message>(textLog.subList(lstMsgRecievedLine, textLog.size()));
    }

}
