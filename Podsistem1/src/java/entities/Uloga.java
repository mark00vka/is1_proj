/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author markovka
 */
@Entity
@Table(name = "uloga")
@NamedQueries({
    @NamedQuery(name = "Uloga.findAll", query = "SELECT u FROM Uloga u")})
public class Uloga implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_uloga")
    private Integer idUloga;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "naziv")
    private String naziv;
    @Lob
    @Size(max = 65535)
    @Column(name = "opis")
    private String opis;
    @ManyToMany(mappedBy = "ulogaCollection")
    private Collection<Korisnik> korisnikCollection;

    public Uloga() {
    }

    public Uloga(Integer idUloga) {
        this.idUloga = idUloga;
    }

    public Uloga(Integer idUloga, String naziv) {
        this.idUloga = idUloga;
        this.naziv = naziv;
    }

    public Integer getIdUloga() {
        return idUloga;
    }

    public void setIdUloga(Integer idUloga) {
        this.idUloga = idUloga;
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

    public Collection<Korisnik> getKorisnikCollection() {
        return korisnikCollection;
    }

    public void setKorisnikCollection(Collection<Korisnik> korisnikCollection) {
        this.korisnikCollection = korisnikCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idUloga != null ? idUloga.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Uloga)) {
            return false;
        }
        Uloga other = (Uloga) object;
        if ((this.idUloga == null && other.idUloga != null) || (this.idUloga != null && !this.idUloga.equals(other.idUloga))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Uloga[ idUloga=" + idUloga + " ]";
    }
    
}
