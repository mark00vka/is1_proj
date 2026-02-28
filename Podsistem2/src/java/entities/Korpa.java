/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import java.util.List;
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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author markovka
 */
@Entity
@Table(name = "korpa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Korpa.findAll", query = "SELECT k FROM Korpa k"),
    @NamedQuery(name = "Korpa.findByIdKorpa", query = "SELECT k FROM Korpa k WHERE k.idKorpa = :idKorpa"),
    @NamedQuery(name = "Korpa.findByUkupnaCena", query = "SELECT k FROM Korpa k WHERE k.ukupnaCena = :ukupnaCena"),
    @NamedQuery(name = "Korpa.findByIdKorisnik", query = "SELECT k FROM Korpa k WHERE k.idKorisnik = :idKorisnik")})
public class Korpa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_korpa")
    private Integer idKorpa;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ukupna_cena")
    private double ukupnaCena;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_korisnik")
    private int idKorisnik;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "korpa")
    private List<KorpaArtikl> korpaArtiklList;

    public Korpa() {
    }

    public Korpa(Integer idKorpa) {
        this.idKorpa = idKorpa;
    }

    public Korpa(Integer idKorpa, double ukupnaCena, int idKorisnik) {
        this.idKorpa = idKorpa;
        this.ukupnaCena = ukupnaCena;
        this.idKorisnik = idKorisnik;
    }

    public Integer getIdKorpa() {
        return idKorpa;
    }

    public void setIdKorpa(Integer idKorpa) {
        this.idKorpa = idKorpa;
    }

    public double getUkupnaCena() {
        return ukupnaCena;
    }

    public void setUkupnaCena(double ukupnaCena) {
        this.ukupnaCena = ukupnaCena;
    }

    public int getIdKorisnik() {
        return idKorisnik;
    }

    public void setIdKorisnik(int idKorisnik) {
        this.idKorisnik = idKorisnik;
    }

    @XmlTransient
    public List<KorpaArtikl> getKorpaArtiklList() {
        return korpaArtiklList;
    }

    public void setKorpaArtiklList(List<KorpaArtikl> korpaArtiklList) {
        this.korpaArtiklList = korpaArtiklList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idKorpa != null ? idKorpa.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Korpa)) {
            return false;
        }
        Korpa other = (Korpa) object;
        if ((this.idKorpa == null && other.idKorpa != null) || (this.idKorpa != null && !this.idKorpa.equals(other.idKorpa))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Korpa[ idKorpa=" + idKorpa + " ]";
    }
    
}
