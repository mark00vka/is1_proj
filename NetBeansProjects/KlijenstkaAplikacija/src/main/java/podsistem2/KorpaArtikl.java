/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package podsistem2;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author markovka
 */
@Entity
@Table(name = "korpa_artikl")
@NamedQueries({
    @NamedQuery(name = "KorpaArtikl.findAll", query = "SELECT k FROM KorpaArtikl k")})
public class KorpaArtikl implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected KorpaArtiklPK korpaArtiklPK;
    @Basic(optional = false)
    @Column(name = "kolicina")
    private int kolicina;
    @JoinColumn(name = "id_artikl", referencedColumnName = "id_artikl", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Artikl artikl;
    @JoinColumn(name = "id_korpa", referencedColumnName = "id_korpa", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Korpa korpa;

    public KorpaArtikl() {
    }

    public KorpaArtikl(KorpaArtiklPK korpaArtiklPK) {
        this.korpaArtiklPK = korpaArtiklPK;
    }

    public KorpaArtikl(KorpaArtiklPK korpaArtiklPK, int kolicina) {
        this.korpaArtiklPK = korpaArtiklPK;
        this.kolicina = kolicina;
    }

    public KorpaArtikl(int idKorpa, int idArtikl) {
        this.korpaArtiklPK = new KorpaArtiklPK(idKorpa, idArtikl);
    }

    public KorpaArtiklPK getKorpaArtiklPK() {
        return korpaArtiklPK;
    }

    public void setKorpaArtiklPK(KorpaArtiklPK korpaArtiklPK) {
        this.korpaArtiklPK = korpaArtiklPK;
    }

    public int getKolicina() {
        return kolicina;
    }

    public void setKolicina(int kolicina) {
        this.kolicina = kolicina;
    }

    public Artikl getArtikl() {
        return artikl;
    }

    public void setArtikl(Artikl artikl) {
        this.artikl = artikl;
    }

    public Korpa getKorpa() {
        return korpa;
    }

    public void setKorpa(Korpa korpa) {
        this.korpa = korpa;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (korpaArtiklPK != null ? korpaArtiklPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof KorpaArtikl)) {
            return false;
        }
        KorpaArtikl other = (KorpaArtikl) object;
        if ((this.korpaArtiklPK == null && other.korpaArtiklPK != null) || (this.korpaArtiklPK != null && !this.korpaArtiklPK.equals(other.korpaArtiklPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entiteti.KorpaArtikl[ korpaArtiklPK=" + korpaArtiklPK + " ]";
    }
    
}
