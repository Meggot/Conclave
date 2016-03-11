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
public class Announcement implements Serializable{

    private final String announcment;
    private final String announcerName;
    
    public Announcement(String username, String msg)
    {
        this.announcerName = username;
        this.announcment = msg;
    }
    
    public String getText(){
        return announcment;
    }
    
    public String getName() {
        return announcerName;
    }
}
