/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author markovka
 */
@Entity
@Table(name = "stavka")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Stavka.findAll", query = "SELECT s FROM Stavka s"),
    @NamedQuery(name = "Stavka.findByIdStavka", query = "SELECT s FROM Stavka s WHERE s.idStavka = :idStavka"),
    @NamedQuery(name = "Stavka.findByKolicina", query = "SELECT s FROM Stavka s WHERE s.kolicina = :kolicina"),
    @NamedQuery(name = "Stavka.findByJedinicnaCena", query = "SELECT s FROM Stavka s WHERE s.jedinicnaCena = :jedinicnaCena"),
    @NamedQuery(name = "Stavka.findByIdArtikl", query = "SELECT s FROM Stavka s WHERE s.idArtikl = :idArtikl")})
public class Stavka implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_stavka")
    private Integer idStavka;
    @Basic(optional = false)
    @NotNull
    @Column(name = "kolicina")
    private int kolicina;
    @Basic(optional = false)
    @NotNull
    @Column(name = "jedinicna_cena")
    private double jedinicnaCena;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_artikl")
    private int idArtikl;
    @JoinColumn(name = "id_narudzbine", referencedColumnName = "id_narudzbine")
    @ManyToOne(optional = false)
    private Narudzbina idNarudzbine;

    public Stavka() {
    }

    public Stavka(Integer idStavka) {
        this.idStavka = idStavka;
    }

    public Stavka(Integer idStavka, int kolicina, double jedinicnaCena, int idArtikl) {
        this.idStavka = idStavka;
        this.kolicina = kolicina;
        this.jedinicnaCena = jedinicnaCena;
        this.idArtikl = idArtikl;
    }

    public Integer getIdStavka() {
        return idStavka;
    }

    public void setIdStavka(Integer idStavka) {
        this.idStavka = idStavka;
    }

    public int getKolicina() {
        return kolicina;
    }

    public void setKolicina(int kolicina) {
        this.kolicina = kolicina;
    }

    public double getJedinicnaCena() {
        return jedinicnaCena;
    }

    public void setJedinicnaCena(double jedinicnaCena) {
        this.jedinicnaCena = jedinicnaCena;
    }

    public int getIdArtikl() {
        return idArtikl;
    }

    public void setIdArtikl(int idArtikl) {
        this.idArtikl = idArtikl;
    }

    public Narudzbina getNarudzbina() {
        return idNarudzbine;
    }

    public void setNarudzbina(Narudzbina idNarudzbine) {
        this.idNarudzbine = idNarudzbine;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idStavka != null ? idStavka.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Stavka)) {
            return false;
        }
        Stavka other = (Stavka) object;
        if ((this.idStavka == null && other.idStavka != null) || (this.idStavka != null && !this.idStavka.equals(other.idStavka))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Stavka[ idStavka=" + idStavka + " ]";
    }
    
}
