/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author markovka
 */
@Entity
@Table(name = "transakcija")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Transakcija.findAll", query = "SELECT t FROM Transakcija t"),
    @NamedQuery(name = "Transakcija.findByIdTransakcija", query = "SELECT t FROM Transakcija t WHERE t.idTransakcija = :idTransakcija"),
    @NamedQuery(name = "Transakcija.findByPlacenaSuma", query = "SELECT t FROM Transakcija t WHERE t.placenaSuma = :placenaSuma"),
    @NamedQuery(name = "Transakcija.findByVremePlacanja", query = "SELECT t FROM Transakcija t WHERE t.vremePlacanja = :vremePlacanja"),
    @NamedQuery(name = "Transakcija.findByIdKupac", query = "SELECT t FROM Transakcija t WHERE t.idKupac = :idKupac")})
public class Transakcija implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_transakcija")
    private Integer idTransakcija;
    @Basic(optional = false)
    @NotNull
    @Column(name = "placena_suma")
    private double placenaSuma;
    @Basic(optional = false)
    @NotNull
    @Column(name = "vreme_placanja")
    @Temporal(TemporalType.TIMESTAMP)
    private Date vremePlacanja;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_kupac")
    private int idKupac;
    @JoinColumn(name = "id_narudzbine", referencedColumnName = "id_narudzbine")
    @ManyToOne(optional = false)
    private Narudzbina idNarudzbine;

    public Transakcija() {
    }

    public Transakcija(Integer idTransakcija) {
        this.idTransakcija = idTransakcija;
    }

    public Transakcija(Integer idTransakcija, double placenaSuma, Date vremePlacanja, int idKupac) {
        this.idTransakcija = idTransakcija;
        this.placenaSuma = placenaSuma;
        this.vremePlacanja = vremePlacanja;
        this.idKupac = idKupac;
    }

    public Integer getIdTransakcija() {
        return idTransakcija;
    }

    public void setIdTransakcija(Integer idTransakcija) {
        this.idTransakcija = idTransakcija;
    }

    public double getPlacenaSuma() {
        return placenaSuma;
    }

    public void setPlacenaSuma(double placenaSuma) {
        this.placenaSuma = placenaSuma;
    }

    public Date getVremePlacanja() {
        return vremePlacanja;
    }

    public void setVremePlacanja(Date vremePlacanja) {
        this.vremePlacanja = vremePlacanja;
    }

    public int getIdKupac() {
        return idKupac;
    }

    public void setIdKupac(int idKupac) {
        this.idKupac = idKupac;
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
        hash += (idTransakcija != null ? idTransakcija.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Transakcija)) {
            return false;
        }
        Transakcija other = (Transakcija) object;
        if ((this.idTransakcija == null && other.idTransakcija != null) || (this.idTransakcija != null && !this.idTransakcija.equals(other.idTransakcija))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Transakcija[ idTransakcija=" + idTransakcija + " ]";
    }
    
}
