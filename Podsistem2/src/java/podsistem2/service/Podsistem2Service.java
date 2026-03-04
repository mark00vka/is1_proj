package podsistem2.service;

import entities.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

public class Podsistem2Service {

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/podsistem2?useSSL=false";
    private static final String DB1_URL = "jdbc:mysql://localhost:3306/podsistem1?useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "admin";

    private final EntityManagerFactory emf;

    public Podsistem2Service() {
        try { Class.forName("com.mysql.jdbc.Driver"); }
        catch (ClassNotFoundException e) { System.err.println(e.getMessage()); }

        Map<String, String> props = new HashMap<>();
        props.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
        props.put("javax.persistence.jdbc.url",    DB_URL);
        props.put("javax.persistence.jdbc.user",   DB_USER);
        props.put("javax.persistence.jdbc.password", DB_PASS);
        this.emf = Persistence.createEntityManagerFactory("Podsistem2PU", props);
    }

    // ----------------------------------------------------------------
    // JDBC helpers to fetch data from podsistem1
    // ----------------------------------------------------------------

    // Returns true if korisnik exists in podsistem1
    private boolean korisnikPostoji(int idKorisnik) {
        try (Connection conn = DriverManager.getConnection(DB1_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT id_korisnik FROM korisnik WHERE id_korisnik = ?")) {
            ps.setInt(1, idKorisnik);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.err.println("Error checking korisnik: " + e.getMessage());
            return false;
        }
    }

    // Returns korisnik stanje_novca from podsistem1
    private int getStanjeNovca(int idKorisnik) {
        try (Connection conn = DriverManager.getConnection(DB1_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT stanje_novca FROM korisnik WHERE id_korisnik = ?")) {
            ps.setInt(1, idKorisnik);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("stanje_novca");
        } catch (Exception e) {
            System.err.println("Error getting stanje_novca: " + e.getMessage());
        }
        return -1;
    }

    // ----------------------------------------------------------------
    // Router
    // ----------------------------------------------------------------
    public String process(String action, String body) {
        switch (action) {
            case "KREIRAJ_KATEGORIJU":    return kreirajKategoriju(body);
            case "KREIRAJ_ARTIKL":        return kreirajArtikl(body);
            case "PROMENI_CENU":          return promeniCenu(body);
            case "POSTAVI_POPUST":        return postaviPopust(body);
            case "DODAJ_U_KORPU":         return dodajUKorpu(body);
            case "OBRISI_IZ_KORPE":       return obrisiIzKorpe(body);
            case "DODAJ_U_WISHLIST":      return dodajUWishlist(body);
            case "OBRISI_IZ_WISHLIST":    return obrisiIzWishlist(body);
            case "GET_ALL_KATEGORIJE":    return getAllKategorije();
            case "GET_ARTIKLI_KORISNIKA": return getArtikliKorisnika(body);
            case "GET_KORPA":             return getKorpa(body);
            case "GET_WISHLIST":          return getWishlist(body);
            default: return error("Unknown action: " + action);
        }
    }

    // 1. Create category
    private String kreirajKategoriju(String body) {
        EntityManager em = emf.createEntityManager();
        try {
            Document doc     = parse(body);
            String naziv     = getText(doc, "naziv");
            String nadkatStr = getText(doc, "id_nadkategorija");

            Kategorija k = new Kategorija();
            k.setNaziv(naziv);
            if (nadkatStr != null && !nadkatStr.isEmpty()) {
                Kategorija nadkat = em.find(Kategorija.class,
                                           Integer.parseInt(nadkatStr));
                if (nadkat != null) k.setNadkategorija(nadkat);
            }

            em.getTransaction().begin();
            em.persist(k);
            em.getTransaction().commit();

            Document resp = newDoc();
            Element root  = resp.createElement("kategorija");
            resp.appendChild(root);
            addEl(resp, root, "id_kategorija",
                  String.valueOf(k.getIdKategorija()));
            addEl(resp, root, "naziv",  k.getNaziv());
            addEl(resp, root, "poruka", "Kategorija uspesno kreirana");
            return toXml(resp);

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return error("Greska pri kreiranju kategorije: " + e.getMessage());
        } finally { em.close(); }
    }

    // 2. Create article — verify kreator exists in podsistem1
    private String kreirajArtikl(String body) {
        EntityManager em = emf.createEntityManager();
        try {
            Document doc   = parse(body);
            int idKreator  = Integer.parseInt(getText(doc, "id_kreator"));

            // Verify korisnik exists in podsistem1
            if (!korisnikPostoji(idKreator))
                return error("Korisnik nije pronadjen u sistemu");

            Artikl a = new Artikl();
            a.setNaziv(getText(doc, "naziv"));
            a.setOpis(getText(doc, "opis"));
            a.setCena(Double.parseDouble(getText(doc, "cena")));
            a.setPopust(Double.parseDouble(getText(doc, "popust")));
            a.setIdKreator(idKreator);

            Kategorija kat = em.find(Kategorija.class,
                Integer.parseInt(getText(doc, "id_kategorija")));
            if (kat == null) return error("Kategorija nije nadjena");
            a.setKategorija(kat);

            em.getTransaction().begin();
            em.persist(a);
            em.getTransaction().commit();

            Document resp = newDoc();
            Element root  = resp.createElement("artikl");
            resp.appendChild(root);
            addEl(resp, root, "id_artikl", String.valueOf(a.getIdArtikl()));
            addEl(resp, root, "naziv",     a.getNaziv());
            addEl(resp, root, "poruka",    "Artikl uspesno kreiran");
            return toXml(resp);

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return error("Greska pri kreiranju artikla: " + e.getMessage());
        } finally { em.close(); }
    }

    // 3. Change price — only creator can do this
    private String promeniCenu(String body) {
        EntityManager em = emf.createEntityManager();
        try {
            Document doc    = parse(body);
            int idArtikl    = Integer.parseInt(getText(doc, "id_artikl"));
            int idKorisnik  = Integer.parseInt(getText(doc, "id_korisnik"));
            double novaCena = Double.parseDouble(getText(doc, "cena"));

            Artikl a = em.find(Artikl.class, idArtikl);
            if (a == null) return error("Artikl nije pronadjen");
            if (a.getIdKreator() != idKorisnik)
                return error("Nemate pravo da menjate cenu ovog artikla");

            em.getTransaction().begin();
            a.setCena(novaCena);
            em.getTransaction().commit();

            Document resp = newDoc();
            Element root  = resp.createElement("artikl");
            resp.appendChild(root);
            addEl(resp, root, "id_artikl", String.valueOf(a.getIdArtikl()));
            addEl(resp, root, "cena",      String.valueOf(a.getCena()));
            addEl(resp, root, "poruka",    "Cena uspesno promenjena");
            return toXml(resp);

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return error("Greska pri promeni cene: " + e.getMessage());
        } finally { em.close(); }
    }

    // 4. Set discount — only creator can do this
    private String postaviPopust(String body) {
        EntityManager em = emf.createEntityManager();
        try {
            Document doc   = parse(body);
            int idArtikl   = Integer.parseInt(getText(doc, "id_artikl"));
            int idKorisnik = Integer.parseInt(getText(doc, "id_korisnik"));
            double popust  = Double.parseDouble(getText(doc, "popust"));

            Artikl a = em.find(Artikl.class, idArtikl);
            if (a == null) return error("Artikl nije pronadjen");
            if (a.getIdKreator() != idKorisnik)
                return error("Nemate pravo da postavljate popust");

            em.getTransaction().begin();
            a.setPopust(popust);
            em.getTransaction().commit();

            Document resp = newDoc();
            Element root  = resp.createElement("artikl");
            resp.appendChild(root);
            addEl(resp, root, "id_artikl", String.valueOf(a.getIdArtikl()));
            addEl(resp, root, "popust",    String.valueOf(a.getPopust()));
            addEl(resp, root, "poruka",    "Popust uspesno postavljen");
            return toXml(resp);

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return error("Greska pri postavljanju popusta: " + e.getMessage());
        } finally { em.close(); }
    }

    // 5. Add to cart — verify korisnik in podsistem1
    private String dodajUKorpu(String body) {
        EntityManager em = emf.createEntityManager();
        try {
            Document doc   = parse(body);
            int idKorisnik = Integer.parseInt(getText(doc, "id_korisnik"));
            int idArtikl   = Integer.parseInt(getText(doc, "id_artikl"));
            int kolicina   = Integer.parseInt(getText(doc, "kolicina"));

            if (!korisnikPostoji(idKorisnik))
                return error("Korisnik nije pronadjen");

            Artikl a = em.find(Artikl.class, idArtikl);
            if (a == null) return error("Artikl nije pronadjen");

            // Find or create cart
            TypedQuery<Korpa> q = em.createQuery(
                "SELECT k FROM Korpa k WHERE k.idKorisnik = :id", Korpa.class);
            q.setParameter("id", idKorisnik);
            List<Korpa> korpe = q.getResultList();

            em.getTransaction().begin();
            Korpa korpa;
            if (korpe.isEmpty()) {
                korpa = new Korpa();
                korpa.setIdKorisnik(idKorisnik);
                korpa.setUkupnaCena(0);
                em.persist(korpa);
                em.flush();
            } else {
                korpa = korpe.get(0);
            }

            // Check if already in cart
            TypedQuery<KorpaArtikl> qa = em.createQuery(
                "SELECT ka FROM KorpaArtikl ka WHERE " +
                "ka.korpaArtiklPK.idKorpa = :idK AND ka.korpaArtiklPK.idArtikl = :idA",
                KorpaArtikl.class);
            qa.setParameter("idK", korpa.getIdKorpa());
            qa.setParameter("idA", idArtikl);
            List<KorpaArtikl> existing = qa.getResultList();

            if (!existing.isEmpty()) {
                existing.get(0).setKolicina(
                    existing.get(0).getKolicina() + kolicina);
            } else {
                KorpaArtiklPK pk = new KorpaArtiklPK(korpa.getIdKorpa(), idArtikl);
                KorpaArtikl ka = new KorpaArtikl(pk, kolicina);
                em.persist(ka);
            }

            double total = korpa.getUkupnaCena() +
                (a.getCena() * (1 - a.getPopust() / 100) * kolicina);
            korpa.setUkupnaCena(total);
            em.getTransaction().commit();

            Document resp = newDoc();
            Element root  = resp.createElement("korpa");
            resp.appendChild(root);
            addEl(resp, root, "id_korpa",    String.valueOf(korpa.getIdKorpa()));
            addEl(resp, root, "ukupna_cena", String.valueOf(korpa.getUkupnaCena()));
            addEl(resp, root, "poruka",      "Artikl dodat u korpu");
            return toXml(resp);

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return error("Greska pri dodavanju u korpu: " + e.getMessage());
        } finally { em.close(); }
    }

    // 6. Remove from cart
    private String obrisiIzKorpe(String body) {
        EntityManager em = emf.createEntityManager();
        try {
            Document doc   = parse(body);
            int idKorisnik = Integer.parseInt(getText(doc, "id_korisnik"));
            int idArtikl   = Integer.parseInt(getText(doc, "id_artikl"));
            int kolicina   = Integer.parseInt(getText(doc, "kolicina"));

            TypedQuery<KorpaArtikl> q = em.createQuery(
                "SELECT ka FROM KorpaArtikl ka WHERE " +
                "ka.korpa.idKorisnik = :idK AND " +
                "ka.artikl.idArtikl = :idA", KorpaArtikl.class);
            q.setParameter("idK", idKorisnik);
            q.setParameter("idA", idArtikl);
            List<KorpaArtikl> results = q.getResultList();

            if (results.isEmpty()) return error("Artikl nije u korpi");

            KorpaArtikl ka = results.get(0);
            Korpa korpa    = ka.getKorpa();
            Artikl a       = ka.getArtikl();
            int stvarnaKolicina = Math.min(kolicina, ka.getKolicina());

            em.getTransaction().begin();
            if (ka.getKolicina() <= kolicina) {
                korpa.getKorpaArtiklList().remove(ka);
                em.remove(ka);
            } else {
                ka.setKolicina(ka.getKolicina() - kolicina);
            }
            double reduction = a.getCena() * (1 - a.getPopust() / 100)
                               * stvarnaKolicina;
            korpa.setUkupnaCena(Math.max(0, korpa.getUkupnaCena() - reduction));
            em.getTransaction().commit();

            Document resp = newDoc();
            Element root  = resp.createElement("korpa");
            resp.appendChild(root);
            addEl(resp, root, "poruka", "Artikl obrisan iz korpe");
            return toXml(resp);

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return error("Greska pri brisanju iz korpe: " + e.getMessage());
        } finally { em.close(); }
    }

    // 7. Add to wishlist
    private String dodajUWishlist(String body) {
        EntityManager em = emf.createEntityManager();
        try {
            Document doc   = parse(body);
            int idKorisnik = Integer.parseInt(getText(doc, "id_korisnik"));
            int idArtikl   = Integer.parseInt(getText(doc, "id_artikl"));

            if (!korisnikPostoji(idKorisnik))
                return error("Korisnik nije pronadjen");

            Artikl a = em.find(Artikl.class, idArtikl);
            if (a == null) return error("Artikl nije pronadjen");

            TypedQuery<Wishlist> q = em.createQuery(
                "SELECT w FROM Wishlist w WHERE w.idKorisnik = :id",
                Wishlist.class);
            q.setParameter("id", idKorisnik);
            List<Wishlist> wishlists = q.getResultList();

            em.getTransaction().begin();
            Wishlist wishlist;
            if (wishlists.isEmpty()) {
                wishlist = new Wishlist();
                wishlist.setIdKorisnik(idKorisnik);
                wishlist.setDatumKreiranja(new Date());
                em.persist(wishlist);
                em.flush();
            } else {
                wishlist = wishlists.get(0);
            }

            TypedQuery<WishlistArtikl> qwa = em.createQuery(
                "SELECT wa FROM WishlistArtikl wa WHERE " +
                "wa.wishlistArtiklPK.idWishlist = :idW AND wa.wishlistArtiklPK.idArtikl = :idA",
                WishlistArtikl.class);
            qwa.setParameter("idW", wishlist.getIdWishlist());
            qwa.setParameter("idA", idArtikl);
            List<WishlistArtikl> existingWa = qwa.getResultList();

            if (existingWa.isEmpty()) {
                WishlistArtiklPK waPK = new WishlistArtiklPK(wishlist.getIdWishlist(), idArtikl);
                WishlistArtikl wa = new WishlistArtikl(waPK, new Date());
                em.persist(wa);
            }
            em.getTransaction().commit();

            Document resp = newDoc();
            Element root  = resp.createElement("wishlist");
            resp.appendChild(root);
            addEl(resp, root, "poruka", "Artikl dodat u wishlist");
            return toXml(resp);

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return error("Greska pri dodavanju u wishlist: " + e.getMessage());
        } finally { em.close(); }
    }

    // 8. Remove from wishlist
    private String obrisiIzWishlist(String body) {
        EntityManager em = emf.createEntityManager();
        try {
            Document doc   = parse(body);
            int idKorisnik = Integer.parseInt(getText(doc, "id_korisnik"));
            int idArtikl   = Integer.parseInt(getText(doc, "id_artikl"));

            TypedQuery<WishlistArtikl> q = em.createQuery(
                "SELECT wa FROM WishlistArtikl wa WHERE " +
                "wa.wishlist.idKorisnik = :idK AND " +
                "wa.artikl.idArtikl = :idA", WishlistArtikl.class);
            q.setParameter("idK", idKorisnik);
            q.setParameter("idA", idArtikl);
            List<WishlistArtikl> results = q.getResultList();

            if (results.isEmpty()) return error("Artikl nije u wishlist-i");

            em.getTransaction().begin();
            em.remove(results.get(0));
            em.getTransaction().commit();

            Document resp = newDoc();
            Element root  = resp.createElement("wishlist");
            resp.appendChild(root);
            addEl(resp, root, "poruka", "Artikl obrisan iz wishlist-e");
            return toXml(resp);

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return error("Greska: " + e.getMessage());
        } finally { em.close(); }
    }

    // 9. Get all categories
    private String getAllKategorije() {
        try {
            EntityManager em = emf.createEntityManager();
            List<Kategorija> kategorije = em.createQuery(
                "SELECT k FROM Kategorija k", Kategorija.class).getResultList();
            em.close();

            Document resp = newDoc();
            Element root  = resp.createElement("kategorije");
            resp.appendChild(root);
            for (Kategorija k : kategorije) {
                Element el = resp.createElement("kategorija");
                addEl(resp, el, "id_kategorija",
                      String.valueOf(k.getIdKategorija()));
                addEl(resp, el, "naziv", k.getNaziv());
                if (k.getNadkategorija() != null)
                    addEl(resp, el, "id_nadkategorija",
                          String.valueOf(k.getNadkategorija().getIdKategorija()));
                root.appendChild(el);
            }
            return toXml(resp);
        } catch (Exception e) {
            return error("Greska: " + e.getMessage());
        }
    }

    // 10. Get articles by creator
    private String getArtikliKorisnika(String body) {
        try {
            Document doc   = parse(body);
            int idKorisnik = Integer.parseInt(getText(doc, "id_korisnik"));

            EntityManager em = emf.createEntityManager();
            TypedQuery<Artikl> q = em.createQuery(
                "SELECT a FROM Artikl a WHERE a.idKreator = :id", Artikl.class);
            q.setParameter("id", idKorisnik);
            List<Artikl> artikli = q.getResultList();
            em.close();

            Document resp = newDoc();
            Element root  = resp.createElement("artikli");
            resp.appendChild(root);
            for (Artikl a : artikli) {
                Element el = resp.createElement("artikl");
                addEl(resp, el, "id_artikl", String.valueOf(a.getIdArtikl()));
                addEl(resp, el, "naziv",     a.getNaziv());
                addEl(resp, el, "opis",      a.getOpis());
                addEl(resp, el, "cena",      String.valueOf(a.getCena()));
                addEl(resp, el, "popust",    String.valueOf(a.getPopust()));
                root.appendChild(el);
            }
            return toXml(resp);
        } catch (Exception e) {
            return error("Greska: " + e.getMessage());
        }
    }

    // 11. Get cart
    private String getKorpa(String body) {
        try {
            Document doc   = parse(body);
            int idKorisnik = Integer.parseInt(getText(doc, "id_korisnik"));

            EntityManager em = emf.createEntityManager();
            TypedQuery<Korpa> q = em.createQuery(
                "SELECT k FROM Korpa k LEFT JOIN FETCH k.korpaArtiklList ka LEFT JOIN FETCH ka.artikl WHERE k.idKorisnik = :id", Korpa.class);
            q.setParameter("id", idKorisnik);
            List<Korpa> korpe = q.getResultList();

            Document resp = newDoc();
            Element root  = resp.createElement("korpa");
            resp.appendChild(root);

            if (korpe.isEmpty()) {
                em.close();
                addEl(resp, root, "poruka", "Korpa je prazna");
                return toXml(resp);
            }

            Korpa korpa = korpe.get(0);
            addEl(resp, root, "id_korpa",    String.valueOf(korpa.getIdKorpa()));
            addEl(resp, root, "ukupna_cena", String.valueOf(korpa.getUkupnaCena()));

            Element artikli = resp.createElement("artikli");
            root.appendChild(artikli);
            for (KorpaArtikl ka : korpa.getKorpaArtiklList()) {
                Element el = resp.createElement("stavka");
                addEl(resp, el, "id_artikl",
                      String.valueOf(ka.getArtikl().getIdArtikl()));
                addEl(resp, el, "naziv",    ka.getArtikl().getNaziv());
                addEl(resp, el, "cena",     String.valueOf(ka.getArtikl().getCena()));
                addEl(resp, el, "popust",   String.valueOf(ka.getArtikl().getPopust()));
                addEl(resp, el, "kolicina", String.valueOf(ka.getKolicina()));
                artikli.appendChild(el);
            }
            em.close();
            return toXml(resp);
        } catch (Exception e) {
            return error("Greska: " + e.getMessage());
        }
    }

    // 12. Get wishlist
    private String getWishlist(String body) {
        try {
            Document doc   = parse(body);
            int idKorisnik = Integer.parseInt(getText(doc, "id_korisnik"));

            EntityManager em = emf.createEntityManager();
            TypedQuery<Wishlist> q = em.createQuery(
                "SELECT w FROM Wishlist w WHERE w.idKorisnik = :id",
                Wishlist.class);
            q.setParameter("id", idKorisnik);
            List<Wishlist> wishlists = q.getResultList();

            Document resp = newDoc();
            Element root  = resp.createElement("wishlist");
            resp.appendChild(root);

            if (wishlists.isEmpty()) {
                em.close();
                addEl(resp, root, "poruka", "Wishlist je prazan");
                return toXml(resp);
            }

            Wishlist wl = wishlists.get(0);
            addEl(resp, root, "id_wishlist", String.valueOf(wl.getIdWishlist()));

            Element artikli = resp.createElement("artikli");
            root.appendChild(artikli);
            for (WishlistArtikl wa : wl.getWishlistArtiklList()) {
                Element el = resp.createElement("artikl");
                addEl(resp, el, "id_artikl",
                      String.valueOf(wa.getArtikl().getIdArtikl()));
                addEl(resp, el, "naziv", wa.getArtikl().getNaziv());
                addEl(resp, el, "cena",  String.valueOf(wa.getArtikl().getCena()));
                addEl(resp, el, "datum_dodavanja",
                      wa.getDatumDodavanja().toString());
                artikli.appendChild(el);
            }
            em.close();
            return toXml(resp);
        } catch (Exception e) {
            return error("Greska: " + e.getMessage());
        }
    }

    // ================================================================
    // XML helpers
    // ================================================================
    private Document parse(String xml) throws Exception {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        DocumentBuilder b = f.newDocumentBuilder();
        return b.parse(new InputSource(new StringReader(xml)));
    }

    private Document newDoc() throws Exception {
        return DocumentBuilderFactory.newInstance()
                                     .newDocumentBuilder().newDocument();
    }

    private String getText(Document doc, String tag) {
        NodeList list = doc.getElementsByTagName(tag);
        if (list.getLength() > 0) return list.item(0).getTextContent();
        return "";
    }

    private void addEl(Document doc, Element parent, String tag, String val) {
        Element el = doc.createElement(tag);
        el.setTextContent(val != null ? val : "");
        parent.appendChild(el);
    }

    private String toXml(Document doc) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter sw = new StringWriter();
        t.transform(new DOMSource(doc), new StreamResult(sw));
        return sw.toString();
    }

    private String error(String msg) {
        try {
            Document doc  = newDoc();
            Element  root = doc.createElement("greska");
            doc.appendChild(root);
            root.setTextContent(msg);
            return toXml(doc);
        } catch (Exception e) {
            return "<greska>" + msg + "</greska>";
        }
    }
}