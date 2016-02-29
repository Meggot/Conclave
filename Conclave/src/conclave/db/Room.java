/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author BradleyW
 */
@Entity
@Table(name = "ROOM")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Room.findAll", query = "SELECT r FROM Room r"),
    @NamedQuery(name = "Room.findByRoomid", query = "SELECT r FROM Room r WHERE r.roomid = :roomid"),
    @NamedQuery(name = "Room.findByRoomname", query = "SELECT r FROM Room r WHERE r.roomname = :roomname"),
    @NamedQuery(name = "Room.findByHashedpassword", query = "SELECT r FROM Room r WHERE r.hashedpassword = :hashedpassword"),
    @NamedQuery(name = "Room.findByRoomtype", query = "SELECT r FROM Room r WHERE r.roomtype = :roomtype")})
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ROOMID")
    private Integer roomid;
    @Basic(optional = false)
    @Column(name = "ROOMNAME")
    private String roomname;
    @Basic(optional = false)
    @Column(name = "HASHEDPASSWORD")
    private String hashedpassword;
    @Basic(optional = false)
    @Lob
    @Column(name = "SALT")
    private Serializable salt;
    @Basic(optional = false)
    @Column(name = "ROOMTYPE")
    private String roomtype;

    public Room() {
    }

    public Room(Integer roomid) {
        this.roomid = roomid;
    }

    public Room(Integer roomid, String roomname, String hashedpassword, Serializable salt, String roomtype) {
        this.roomid = roomid;
        this.roomname = roomname;
        this.hashedpassword = hashedpassword;
        this.salt = salt;
        this.roomtype = roomtype;
    }

    public Integer getRoomid() {
        return roomid;
    }

    public void setRoomid(Integer roomid) {
        this.roomid = roomid;
    }

    public String getRoomname() {
        return roomname;
    }

    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }

    public String getHashedpassword() {
        return hashedpassword;
    }

    public void setHashedpassword(String hashedpassword) {
        this.hashedpassword = hashedpassword;
    }

    public byte[] getSalt() 
    {
        String s = (String) salt;
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                             + Character.digit(s.charAt(i+1), 16));
    }
        return data;
    }

    public void setSalt(byte[] saltBytes) 
    {
    char[] hexChars = new char[saltBytes.length * 2];
    char[] hexArray = "0123456789ABCDEF".toCharArray();
    for ( int j = 0; j < saltBytes.length; j++ ) {
        int v = saltBytes[j] & 0xFF;
        hexChars[j * 2] = hexArray[v >>> 4];
        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
     }
    this.salt = new String(hexChars);
    }

    public String getRoomtype() {
        return roomtype;
    }

    public void setRoomtype(String roomtype) {
        this.roomtype = roomtype;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomid != null ? roomid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Room)) {
            return false;
        }
        Room other = (Room) object;
        if ((this.roomid == null && other.roomid != null) || (this.roomid != null && !this.roomid.equals(other.roomid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "conclave.db.Room[ roomid=" + roomid + " ]";
    }
    
}
