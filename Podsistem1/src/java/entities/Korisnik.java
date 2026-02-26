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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
@Table(name = "korisnik")
@NamedQueries({
    @NamedQuery(name = "Korisnik.findAll", query = "SELECT k FROM Korisnik k")})
public class Korisnik implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_korisnik")
    private Integer idKorisnik;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "korisnicko_ime")
    private String korisnickoIme;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "sifra")
    private String sifra;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "ime")
    private String ime;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "prezime")
    private String prezime;
    @Size(max = 255)
    @Column(name = "adresa")
    private String adresa;
    @Basic(optional = false)
    @NotNull
    @Column(name = "stanje_novca")
    private double stanjeNovca;
    @JoinTable(name = "korisnik_uloga", joinColumns = {
        @JoinColumn(name = "id_korisnik", referencedColumnName = "id_korisnik")}, inverseJoinColumns = {
        @JoinColumn(name = "id_uloga", referencedColumnName = "id_uloga")})
    @ManyToMany
    private Collection<Uloga> ulogaCollection;
    @JoinColumn(name = "id_grad", referencedColumnName = "id_grad")
    @ManyToOne
    private Grad idGrad;

    public Korisnik() {
    }

    public Korisnik(Integer idKorisnik) {
        this.idKorisnik = idKorisnik;
    }

    public Korisnik(Integer idKorisnik, String korisnickoIme, String sifra, String ime, String prezime, double stanjeNovca) {
        this.idKorisnik = idKorisnik;
        this.korisnickoIme = korisnickoIme;
        this.sifra = sifra;
        this.ime = ime;
        this.prezime = prezime;
        this.stanjeNovca = stanjeNovca;
    }

    public Integer getIdKorisnik() {
        return idKorisnik;
    }

    public void setIdKorisnik(Integer idKorisnik) {
        this.idKorisnik = idKorisnik;
    }

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    public String getSifra() {
        return sifra;
    }

    public void setSifra(String sifra) {
        this.sifra = sifra;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public double getStanjeNovca() {
        return stanjeNovca;
    }

    public void setStanjeNovca(double stanjeNovca) {
        this.stanjeNovca = stanjeNovca;
    }

    public Collection<Uloga> getUlogaCollection() {
        return ulogaCollection;
    }

    public void setUlogaCollection(Collection<Uloga> ulogaCollection) {
        this.ulogaCollection = ulogaCollection;
    }

    public Grad getIdGrad() {
        return idGrad;
    }

    public void setIdGrad(Grad idGrad) {
        this.idGrad = idGrad;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idKorisnik != null ? idKorisnik.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Korisnik)) {
            return false;
        }
        Korisnik other = (Korisnik) object;
        if ((this.idKorisnik == null && other.idKorisnik != null) || (this.idKorisnik != null && !this.idKorisnik.equals(other.idKorisnik))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Korisnik[ idKorisnik=" + idKorisnik + " ]";
    }
    
}
