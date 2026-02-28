/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author markovka
 */
@Entity
@Table(name = "narudzbina")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Narudzbina.findAll", query = "SELECT n FROM Narudzbina n"),
    @NamedQuery(name = "Narudzbina.findByIdNarudzbine", query = "SELECT n FROM Narudzbina n WHERE n.idNarudzbine = :idNarudzbine"),
    @NamedQuery(name = "Narudzbina.findByUkupnaCena", query = "SELECT n FROM Narudzbina n WHERE n.ukupnaCena = :ukupnaCena"),
    @NamedQuery(name = "Narudzbina.findByVremeKreiranja", query = "SELECT n FROM Narudzbina n WHERE n.vremeKreiranja = :vremeKreiranja"),
    @NamedQuery(name = "Narudzbina.findByAdresa", query = "SELECT n FROM Narudzbina n WHERE n.adresa = :adresa"),
    @NamedQuery(name = "Narudzbina.findByIdGradDostava", query = "SELECT n FROM Narudzbina n WHERE n.idGradDostava = :idGradDostava"),
    @NamedQuery(name = "Narudzbina.findByIdKupac", query = "SELECT n FROM Narudzbina n WHERE n.idKupac = :idKupac")})
public class Narudzbina implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_narudzbine")
    private Integer idNarudzbine;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ukupna_cena")
    private double ukupnaCena;
    @Basic(optional = false)
    @NotNull
    @Column(name = "vreme_kreiranja")
    @Temporal(TemporalType.TIMESTAMP)
    private Date vremeKreiranja;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "adresa")
    private String adresa;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_grad_dostava")
    private int idGradDostava;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_kupac")
    private int idKupac;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idNarudzbine")
    private List<Stavka> stavkaList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idNarudzbine")
    private List<Transakcija> transakcijaList;

    public Narudzbina() {
    }

    public Narudzbina(Integer idNarudzbine) {
        this.idNarudzbine = idNarudzbine;
    }

    public Narudzbina(Integer idNarudzbine, double ukupnaCena, Date vremeKreiranja, String adresa, int idGradDostava, int idKupac) {
        this.idNarudzbine = idNarudzbine;
        this.ukupnaCena = ukupnaCena;
        this.vremeKreiranja = vremeKreiranja;
        this.adresa = adresa;
        this.idGradDostava = idGradDostava;
        this.idKupac = idKupac;
    }

    public Integer getIdNarudzbine() {
        return idNarudzbine;
    }

    public void setIdNarudzbine(Integer idNarudzbine) {
        this.idNarudzbine = idNarudzbine;
    }

    public double getUkupnaCena() {
        return ukupnaCena;
    }

    public void setUkupnaCena(double ukupnaCena) {
        this.ukupnaCena = ukupnaCena;
    }

    public Date getVremeKreiranja() {
        return vremeKreiranja;
    }

    public void setVremeKreiranja(Date vremeKreiranja) {
        this.vremeKreiranja = vremeKreiranja;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public int getIdGradDostava() {
        return idGradDostava;
    }

    public void setIdGradDostava(int idGradDostava) {
        this.idGradDostava = idGradDostava;
    }

    public int getIdKupac() {
        return idKupac;
    }

    public void setIdKupac(int idKupac) {
        this.idKupac = idKupac;
    }

    @XmlTransient
    public List<Stavka> getStavkaList() {
        return stavkaList;
    }

    public void setStavkaList(List<Stavka> stavkaList) {
        this.stavkaList = stavkaList;
    }

    @XmlTransient
    public List<Transakcija> getTransakcijaList() {
        return transakcijaList;
    }

    public void setTransakcijaList(List<Transakcija> transakcijaList) {
        this.transakcijaList = transakcijaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idNarudzbine != null ? idNarudzbine.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Narudzbina)) {
            return false;
        }
        Narudzbina other = (Narudzbina) object;
        if ((this.idNarudzbine == null && other.idNarudzbine != null) || (this.idNarudzbine != null && !this.idNarudzbine.equals(other.idNarudzbine))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Narudzbina[ idNarudzbine=" + idNarudzbine + " ]";
    }
    
}
