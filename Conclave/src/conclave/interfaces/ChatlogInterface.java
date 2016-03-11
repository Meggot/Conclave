/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.interfaces;

import conclave.model.Message;
import java.util.LinkedList;

/**
 *
 * @author BradleyW
 */
public interface ChatlogInterface {
    
    public void addMessage(Message msg);
    public String viewEntries();
    public LinkedList<Message> getAllEntriesAfter(int lstMsgRecievedLine);
}
