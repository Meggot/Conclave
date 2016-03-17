/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author BradleyW
 */
public class Message implements Serializable {
    
    private Date timestamp;
    private String sender;
    private String recipientId;
    private String msg;
    
    private MessageType messageType;
    
    public Message(String isender, String irecipientId, String imsg, int messageTypei)
    {
        timestamp = new Date();
        sender = isender;
        recipientId = irecipientId;
        msg = imsg;
        messageType = new MessageType(messageTypei);
    }
    
    public void setMsgText(String imsg)
    {
        this.msg = imsg;
    }
    
    public String msgDisplay()
    {
        String returnString = "[" + timestamp.getHours() + ":" + timestamp.getMinutes() + "] " + getType() + " - " + sender + ": " + msg;
        return returnString;
    }
    
    public String getSenderName()
    {
        return sender;
    }
    
    public String getRecipientId()
    {
        return recipientId;
    }
    
    public String getType()
    {
        return messageType.getTypeText();
    }
}
