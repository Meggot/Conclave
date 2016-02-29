/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.model;

import java.io.Serializable;

/**
 *
 * @author BradleyW
 */
public class MessageType implements Serializable{
    
    private int type; 
    
    public MessageType(int itype)
    {
        this.type = itype;
    }
    
    public String getTypeText()
    {
        String returnString = "";
        switch(type){
            case 1:  returnString = "ROOM";
                     break;
            case 2:  returnString = "SYSTEM";
                     break;
            case 3:  returnString = "ADMIN";
                     break;
            case 4:  returnString = "PRIVATE";
                     break;
        }
        return returnString;
    }
            
            
}
