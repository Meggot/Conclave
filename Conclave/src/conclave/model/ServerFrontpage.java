/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author BradleyW
 */
public class ServerFrontpage implements Serializable{
    
    private ArrayList<Announcement> announcements;
    
    public ServerFrontpage() {
        this.announcements = new ArrayList();
    }
    
    public void addNewAnnouncment(String username, String msg) {
        Announcement sd = new Announcement(username, msg);
        announcements.add(sd);
    }
    
    public void setFrontpage(List<Announcement> iannouncements) {
        this.announcements = new ArrayList(iannouncements);
    }
    
    public List<Announcement> getFrontpage() {
        return announcements;
    }
}
