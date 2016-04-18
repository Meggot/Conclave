/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.db;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author BradleyW
 */
@Entity
@Table(name = "SESSIONKEYS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Sessionkeys.findAll", query = "SELECT s FROM Sessionkeys s"),
    @NamedQuery(name = "Sessionkeys.findByKeyidentify", query = "SELECT s FROM Sessionkeys s WHERE s.keyidentify = :keyidentify"),
    @NamedQuery(name = "Sessionkeys.findByKeystring", query = "SELECT s FROM Sessionkeys s WHERE s.keystring = :keystring")})
public class Sessionkeys implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "KEYIDENTIFY")
    private Integer keyidentify;
    @Basic(optional = false)
    @Column(name = "KEYSTRING")
    private String keystring;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "keyid")
    private Collection<Userkeys> userkeysCollection;

    public Sessionkeys() {
    }

    public Sessionkeys(Integer keyidentify) {
        this.keyidentify = keyidentify;
    }

    public Sessionkeys(Integer keyidentify, String keystring) {
        this.keyidentify = keyidentify;
        this.keystring = keystring;
    }

    public Integer getKeyidentify() {
        return keyidentify;
    }

    public void setKeyidentify(Integer keyidentify) {
        this.keyidentify = keyidentify;
    }

    public String getKeystring() {
        return keystring;
    }

    public void setKeystring(String keystring) {
        this.keystring = keystring;
    }

    @XmlTransient
    public Collection<Userkeys> getUserkeysCollection() {
        return userkeysCollection;
    }

    public void setUserkeysCollection(Collection<Userkeys> userkeysCollection) {
        this.userkeysCollection = userkeysCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (keyidentify != null ? keyidentify.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Sessionkeys)) {
            return false;
        }
        Sessionkeys other = (Sessionkeys) object;
        if ((this.keyidentify == null && other.keyidentify != null) || (this.keyidentify != null && !this.keyidentify.equals(other.keyidentify))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "conclave.db.Sessionkeys[ keyidentify=" + keyidentify + " ]";
    }
    
}
