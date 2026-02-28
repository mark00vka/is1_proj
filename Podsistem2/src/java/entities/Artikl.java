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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author markovka
 */
@Entity
@Table(name = "artikl")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Artikl.findAll", query = "SELECT a FROM Artikl a"),
    @NamedQuery(name = "Artikl.findByIdArtikl", query = "SELECT a FROM Artikl a WHERE a.idArtikl = :idArtikl"),
    @NamedQuery(name = "Artikl.findByNaziv", query = "SELECT a FROM Artikl a WHERE a.naziv = :naziv"),
    @NamedQuery(name = "Artikl.findByCena", query = "SELECT a FROM Artikl a WHERE a.cena = :cena"),
    @NamedQuery(name = "Artikl.findByPopust", query = "SELECT a FROM Artikl a WHERE a.popust = :popust"),
    @NamedQuery(name = "Artikl.findByIdKreator", query = "SELECT a FROM Artikl a WHERE a.idKreator = :idKreator")})
public class Artikl implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_artikl")
    private Integer idArtikl;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "naziv")
    private String naziv;
    @Lob
    @Size(max = 65535)
    @Column(name = "opis")
    private String opis;
    @Basic(optional = false)
    @NotNull
    @Column(name = "cena")
    private double cena;
    @Basic(optional = false)
    @NotNull
    @Column(name = "popust")
    private double popust;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_kreator")
    private int idKreator;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "artikl")
    private List<WishlistArtikl> wishlistArtiklList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "artikl")
    private List<KorpaArtikl> korpaArtiklList;
    @JoinColumn(name = "id_kategorija", referencedColumnName = "id_kategorija")
    @ManyToOne(optional = false)
    private Kategorija idKategorija;

    public Artikl() {
    }

    public Artikl(Integer idArtikl) {
        this.idArtikl = idArtikl;
    }

    public Artikl(Integer idArtikl, String naziv, double cena, double popust, int idKreator) {
        this.idArtikl = idArtikl;
        this.naziv = naziv;
        this.cena = cena;
        this.popust = popust;
        this.idKreator = idKreator;
    }

    public Integer getIdArtikl() {
        return idArtikl;
    }

    public void setIdArtikl(Integer idArtikl) {
        this.idArtikl = idArtikl;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public double getCena() {
        return cena;
    }

    public void setCena(double cena) {
        this.cena = cena;
    }

    public double getPopust() {
        return popust;
    }

    public void setPopust(double popust) {
        this.popust = popust;
    }

    public int getIdKreator() {
        return idKreator;
    }

    public void setIdKreator(int idKreator) {
        this.idKreator = idKreator;
    }

    @XmlTransient
    public List<WishlistArtikl> getWishlistArtiklList() {
        return wishlistArtiklList;
    }

    public void setWishlistArtiklList(List<WishlistArtikl> wishlistArtiklList) {
        this.wishlistArtiklList = wishlistArtiklList;
    }

    @XmlTransient
    public List<KorpaArtikl> getKorpaArtiklList() {
        return korpaArtiklList;
    }

    public void setKorpaArtiklList(List<KorpaArtikl> korpaArtiklList) {
        this.korpaArtiklList = korpaArtiklList;
    }

    public Kategorija getKategorija() {
        return idKategorija;
    }

    public void setKategorija(Kategorija idKategorija) {
        this.idKategorija = idKategorija;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idArtikl != null ? idArtikl.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Artikl)) {
            return false;
        }
        Artikl other = (Artikl) object;
        if ((this.idArtikl == null && other.idArtikl != null) || (this.idArtikl != null && !this.idArtikl.equals(other.idArtikl))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Artikl[ idArtikl=" + idArtikl + " ]";
    }
    
}
