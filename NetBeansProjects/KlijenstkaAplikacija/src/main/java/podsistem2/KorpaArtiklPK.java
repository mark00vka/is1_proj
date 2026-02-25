/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package podsistem2;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author markovka
 */
@Embeddable
public class KorpaArtiklPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "id_korpa")
    private int idKorpa;
    @Basic(optional = false)
    @Column(name = "id_artikl")
    private int idArtikl;

    public KorpaArtiklPK() {
    }

    public KorpaArtiklPK(int idKorpa, int idArtikl) {
        this.idKorpa = idKorpa;
        this.idArtikl = idArtikl;
    }

    public int getIdKorpa() {
        return idKorpa;
    }

    public void setIdKorpa(int idKorpa) {
        this.idKorpa = idKorpa;
    }

    public int getIdArtikl() {
        return idArtikl;
    }

    public void setIdArtikl(int idArtikl) {
        this.idArtikl = idArtikl;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idKorpa;
        hash += (int) idArtikl;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof KorpaArtiklPK)) {
            return false;
        }
        KorpaArtiklPK other = (KorpaArtiklPK) object;
        if (this.idKorpa != other.idKorpa) {
            return false;
        }
        if (this.idArtikl != other.idArtikl) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entiteti.KorpaArtiklPK[ idKorpa=" + idKorpa + ", idArtikl=" + idArtikl + " ]";
    }
    
}
