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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author BradleyW
 */
@Entity
@Table(name = "USERKEYS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Userkeys.findAll", query = "SELECT u FROM Userkeys u"),
    @NamedQuery(name = "Userkeys.findByUserkeyid", query = "SELECT u FROM Userkeys u WHERE u.userkeyid = :userkeyid"),
    @NamedQuery(name = "Userkeys.findByUsername", query = "SELECT u FROM Userkeys u WHERE u.username = :username"),
    @NamedQuery(name = "Userkeys.findByKeyId", query = "SELECT u FROM Userkeys u WHERE u.keyid = :keyid")})
public class Userkeys implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "USERKEYID")
    private Integer userkeyid;
    @Basic(optional = false)
    @Column(name = "USERNAME")
    private String username;
    @JoinColumn(name = "KEYID", referencedColumnName = "KEYIDENTIFY")
    @ManyToOne(optional = false)
    private Sessionkeys keyid;

    public Userkeys() {
    }

    public Userkeys(Integer userkeyid) {
        this.userkeyid = userkeyid;
    }

    public Userkeys(Integer userkeyid, String username) {
        this.userkeyid = userkeyid;
        this.username = username;
    }

    public Integer getUserkeyid() {
        return userkeyid;
    }

    public void setUserkeyid(Integer userkeyid) {
        this.userkeyid = userkeyid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Sessionkeys getKeyid() {
        return keyid;
    }

    public void setKeyid(Sessionkeys keyid) {
        this.keyid = keyid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userkeyid != null ? userkeyid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Userkeys)) {
            return false;
        }
        Userkeys other = (Userkeys) object;
        if ((this.userkeyid == null && other.userkeyid != null) || (this.userkeyid != null && !this.userkeyid.equals(other.userkeyid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "conclave.db.Userkeys[ userkeyid=" + userkeyid + " ]";
    }
    
}
