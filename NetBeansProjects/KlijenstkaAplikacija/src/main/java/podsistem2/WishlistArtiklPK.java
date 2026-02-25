/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package podsistem2;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author markovka
 */
@Embeddable
public class WishlistArtiklPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "id_wishlist")
    private int idWishlist;
    @Basic(optional = false)
    @Column(name = "id_artikl")
    private int idArtikl;

    public WishlistArtiklPK() {
    }

    public WishlistArtiklPK(int idWishlist, int idArtikl) {
        this.idWishlist = idWishlist;
        this.idArtikl = idArtikl;
    }

    public int getIdWishlist() {
        return idWishlist;
    }

    public void setIdWishlist(int idWishlist) {
        this.idWishlist = idWishlist;
    }

    public int getIdArtikl() {
        return idArtikl;
    }

    public void setIdArtikl(int idArtikl) {
        this.idArtikl = idArtikl;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idWishlist;
        hash += (int) idArtikl;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WishlistArtiklPK)) {
            return false;
        }
        WishlistArtiklPK other = (WishlistArtiklPK) object;
        if (this.idWishlist != other.idWishlist) {
            return false;
        }
        if (this.idArtikl != other.idArtikl) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entiteti.WishlistArtiklPK[ idWishlist=" + idWishlist + ", idArtikl=" + idArtikl + " ]";
    }
    
}
