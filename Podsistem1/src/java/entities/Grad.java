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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author markovka
 */
@Entity
@Table(name = "grad")
@NamedQueries({
    @NamedQuery(name = "Grad.findAll", query = "SELECT g FROM Grad g")})
public class Grad implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_grad")
    private Integer idGrad;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "naziv")
    private String naziv;
    @OneToMany(mappedBy = "idGrad")
    private Collection<Korisnik> korisnikCollection;

    public Grad() {
    }

    public Grad(Integer idGrad) {
        this.idGrad = idGrad;
    }

    public Grad(Integer idGrad, String naziv) {
        this.idGrad = idGrad;
        this.naziv = naziv;
    }

    public Integer getIdGrad() {
        return idGrad;
    }

    public void setIdGrad(Integer idGrad) {
        this.idGrad = idGrad;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
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
        hash += (idGrad != null ? idGrad.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Grad)) {
            return false;
        }
        Grad other = (Grad) object;
        if ((this.idGrad == null && other.idGrad != null) || (this.idGrad != null && !this.idGrad.equals(other.idGrad))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Grad[ idGrad=" + idGrad + " ]";
    }
    
}
