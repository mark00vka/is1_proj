/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
@Table(name = "wishlist_artikl")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "WishlistArtikl.findAll", query = "SELECT w FROM WishlistArtikl w"),
    @NamedQuery(name = "WishlistArtikl.findByIdWishlist", query = "SELECT w FROM WishlistArtikl w WHERE w.wishlistArtiklPK.idWishlist = :idWishlist"),
    @NamedQuery(name = "WishlistArtikl.findByIdArtikl", query = "SELECT w FROM WishlistArtikl w WHERE w.wishlistArtiklPK.idArtikl = :idArtikl"),
    @NamedQuery(name = "WishlistArtikl.findByDatumDodavanja", query = "SELECT w FROM WishlistArtikl w WHERE w.datumDodavanja = :datumDodavanja")})
public class WishlistArtikl implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected WishlistArtiklPK wishlistArtiklPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "datum_dodavanja")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datumDodavanja;
    @JoinColumn(name = "id_artikl", referencedColumnName = "id_artikl", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Artikl artikl;
    @JoinColumn(name = "id_wishlist", referencedColumnName = "id_wishlist", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Wishlist wishlist;

    public WishlistArtikl() {
    }

    public WishlistArtikl(WishlistArtiklPK wishlistArtiklPK) {
        this.wishlistArtiklPK = wishlistArtiklPK;
    }

    public WishlistArtikl(WishlistArtiklPK wishlistArtiklPK, Date datumDodavanja) {
        this.wishlistArtiklPK = wishlistArtiklPK;
        this.datumDodavanja = datumDodavanja;
    }

    public WishlistArtikl(int idWishlist, int idArtikl) {
        this.wishlistArtiklPK = new WishlistArtiklPK(idWishlist, idArtikl);
    }

    public WishlistArtiklPK getWishlistArtiklPK() {
        return wishlistArtiklPK;
    }

    public void setWishlistArtiklPK(WishlistArtiklPK wishlistArtiklPK) {
        this.wishlistArtiklPK = wishlistArtiklPK;
    }

    public Date getDatumDodavanja() {
        return datumDodavanja;
    }

    public void setDatumDodavanja(Date datumDodavanja) {
        this.datumDodavanja = datumDodavanja;
    }

    public Artikl getArtikl() {
        return artikl;
    }

    public void setArtikl(Artikl artikl) {
        this.artikl = artikl;
    }

    public Wishlist getWishlist() {
        return wishlist;
    }

    public void setWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (wishlistArtiklPK != null ? wishlistArtiklPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WishlistArtikl)) {
            return false;
        }
        WishlistArtikl other = (WishlistArtikl) object;
        if ((this.wishlistArtiklPK == null && other.wishlistArtiklPK != null) || (this.wishlistArtiklPK != null && !this.wishlistArtiklPK.equals(other.wishlistArtiklPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.WishlistArtikl[ wishlistArtiklPK=" + wishlistArtiklPK + " ]";
    }
    
}
