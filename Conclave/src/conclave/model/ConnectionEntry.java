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
public class ConnectionEntry implements Serializable{
    
    private String name;
    private String desc;
    
    public ConnectionEntry(String conName, String conDesc){
        this.name = conName;
        this.desc = conDesc;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getDesc()
    {
        return desc;
    }
}
