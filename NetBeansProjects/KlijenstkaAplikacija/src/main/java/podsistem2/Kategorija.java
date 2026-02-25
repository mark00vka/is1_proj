/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package podsistem2;

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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author markovka
 */
@Entity
@Table(name = "kategorija")
@NamedQueries({
    @NamedQuery(name = "Kategorija.findAll", query = "SELECT k FROM Kategorija k")})
public class Kategorija implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_kategorija")
    private Integer idKategorija;
    @Basic(optional = false)
    @Column(name = "naziv")
    private String naziv;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idKategorija")
    private List<Artikl> artiklList;
    @OneToMany(mappedBy = "idNadkategorija")
    private List<Kategorija> kategorijaList;
    @JoinColumn(name = "id_nadkategorija", referencedColumnName = "id_kategorija")
    @ManyToOne
    private Kategorija idNadkategorija;

    public Kategorija() {
    }

    public Kategorija(Integer idKategorija) {
        this.idKategorija = idKategorija;
    }

    public Kategorija(Integer idKategorija, String naziv) {
        this.idKategorija = idKategorija;
        this.naziv = naziv;
    }

    public Integer getIdKategorija() {
        return idKategorija;
    }

    public void setIdKategorija(Integer idKategorija) {
        this.idKategorija = idKategorija;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public List<Artikl> getArtiklList() {
        return artiklList;
    }

    public void setArtiklList(List<Artikl> artiklList) {
        this.artiklList = artiklList;
    }

    public List<Kategorija> getKategorijaList() {
        return kategorijaList;
    }

    public void setKategorijaList(List<Kategorija> kategorijaList) {
        this.kategorijaList = kategorijaList;
    }

    public Kategorija getIdNadkategorija() {
        return idNadkategorija;
    }

    public void setIdNadkategorija(Kategorija idNadkategorija) {
        this.idNadkategorija = idNadkategorija;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idKategorija != null ? idKategorija.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Kategorija)) {
            return false;
        }
        Kategorija other = (Kategorija) object;
        if ((this.idKategorija == null && other.idKategorija != null) || (this.idKategorija != null && !this.idKategorija.equals(other.idKategorija))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entiteti.Kategorija[ idKategorija=" + idKategorija + " ]";
    }
    
}
