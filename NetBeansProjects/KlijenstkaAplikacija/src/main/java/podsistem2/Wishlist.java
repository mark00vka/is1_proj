/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package podsistem2;

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

/**
 *
 * @author markovka
 */
@Entity
@Table(name = "wishlist")
@NamedQueries({
    @NamedQuery(name = "Wishlist.findAll", query = "SELECT w FROM Wishlist w")})
public class Wishlist implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_wishlist")
    private Integer idWishlist;
    @Basic(optional = false)
    @Column(name = "datum_kreiranja")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datumKreiranja;
    @Basic(optional = false)
    @Column(name = "id_korisnik")
    private int idKorisnik;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "wishlist")
    private List<WishlistArtikl> wishlistArtiklList;

    public Wishlist() {
    }

    public Wishlist(Integer idWishlist) {
        this.idWishlist = idWishlist;
    }

    public Wishlist(Integer idWishlist, Date datumKreiranja, int idKorisnik) {
        this.idWishlist = idWishlist;
        this.datumKreiranja = datumKreiranja;
        this.idKorisnik = idKorisnik;
    }

    public Integer getIdWishlist() {
        return idWishlist;
    }

    public void setIdWishlist(Integer idWishlist) {
        this.idWishlist = idWishlist;
    }

    public Date getDatumKreiranja() {
        return datumKreiranja;
    }

    public void setDatumKreiranja(Date datumKreiranja) {
        this.datumKreiranja = datumKreiranja;
    }

    public int getIdKorisnik() {
        return idKorisnik;
    }

    public void setIdKorisnik(int idKorisnik) {
        this.idKorisnik = idKorisnik;
    }

    public List<WishlistArtikl> getWishlistArtiklList() {
        return wishlistArtiklList;
    }

    public void setWishlistArtiklList(List<WishlistArtikl> wishlistArtiklList) {
        this.wishlistArtiklList = wishlistArtiklList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idWishlist != null ? idWishlist.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Wishlist)) {
            return false;
        }
        Wishlist other = (Wishlist) object;
        if ((this.idWishlist == null && other.idWishlist != null) || (this.idWishlist != null && !this.idWishlist.equals(other.idWishlist))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entiteti.Wishlist[ idWishlist=" + idWishlist + " ]";
    }
    
}
