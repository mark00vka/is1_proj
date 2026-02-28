package podsistem1.service;

import entities.Grad;
import entities.Korisnik;
import entities.Uloga;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Podsistem1Service {

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/podsistem2?useSSL=false";
    private static final String DB1_URL = "jdbc:mysql://localhost:3306/podsistem1?useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "admin";

    private final EntityManagerFactory emf;

    public Podsistem1Service() {
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
    // Router
    // ----------------------------------------------------------------
    public String process(String action, String body) {
        switch (action) {
            case "PROVERI_KORISNIKA": return proveriKorisnika(body);
            case "KREIRAJ_GRAD":      return kreirajGrad(body);
            case "KREIRAJ_KORISNIKA": return kreirajKorisnika(body);
            case "DODAJ_NOVAC":       return dodajNovac(body);
            case "PROMENI_ADRESU":    return promeniAdresu(body);
            case "GET_ALL_GRADOVI":   return getAllGradovi();
            case "GET_ALL_KORISNICI": return getAllKorisnici();
            default: return error("Unknown action: " + action);
        }
    }

    // ----------------------------------------------------------------
    // 1. Check credentials (login)
    // ----------------------------------------------------------------
    private String proveriKorisnika(String body) {
        try {
            Document doc = parse(body);
            String korisnickoIme = getText(doc, "korisnicko_ime");
            String sifra         = getText(doc, "sifra");

            EntityManager em = emf.createEntityManager();
            TypedQuery<Korisnik> q = em.createQuery(
                "SELECT k FROM Korisnik k WHERE k.korisnickoIme = :ime " +
                "AND k.sifra = :sifra", Korisnik.class);
            q.setParameter("ime",   korisnickoIme);
            q.setParameter("sifra", sifra);
            List<Korisnik> results = q.getResultList();
            em.close();

            if (results.isEmpty())
                return error("Pogresno korisnicko ime ili sifra");

            Korisnik k = results.get(0);
            Document resp     = newDoc();
            Element  root     = resp.createElement("korisnik");
            resp.appendChild(root);

            addElement(resp, root, "id_korisnik",    String.valueOf(k.getIdKorisnik()));
            addElement(resp, root, "korisnicko_ime", k.getKorisnickoIme());
            addElement(resp, root, "ime",            k.getIme());
            addElement(resp, root, "prezime",        k.getPrezime());
            addElement(resp, root, "adresa",         k.getAdresa());
            addElement(resp, root, "stanje_novca",   String.valueOf(k.getStanjeNovca()));

            Element uloge = resp.createElement("uloge");
            root.appendChild(uloge);
            for (Uloga u : k.getUlogaList()) {
                addElement(resp, uloge, "uloga", u.getNaziv());
            }

            return toXml(resp);

        } catch (Exception e) {
            return error("Greska pri proveri korisnika: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // 2. Create city
    // ----------------------------------------------------------------
    private String kreirajGrad(String body) {
        EntityManager em = emf.createEntityManager();
        try {
            Document doc  = parse(body);
            String naziv  = getText(doc, "naziv");

            Grad grad = new Grad();
            grad.setNaziv(naziv);

            em.getTransaction().begin();
            em.persist(grad);
            em.getTransaction().commit();

            Document resp = newDoc();
            Element  root = resp.createElement("grad");
            resp.appendChild(root);
            addElement(resp, root, "id_grad", String.valueOf(grad.getIdGrad()));
            addElement(resp, root, "naziv",   grad.getNaziv());
            addElement(resp, root, "poruka",  "Grad uspesno kreiran");
            return toXml(resp);

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return error("Greska pri kreiranju grada: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // ----------------------------------------------------------------
    // 3. Create user
    // ----------------------------------------------------------------
    private String kreirajKorisnika(String body) {
        EntityManager em = emf.createEntityManager();
        try {
            Document doc = parse(body);

            Korisnik k = new Korisnik();
            k.setKorisnickoIme(getText(doc, "korisnicko_ime"));
            k.setSifra(getText(doc, "sifra"));
            k.setIme(getText(doc, "ime"));
            k.setPrezime(getText(doc, "prezime"));
            k.setAdresa(getText(doc, "adresa"));
            k.setStanjeNovca(0);

            String idGradStr = getText(doc, "id_grad");
            if (idGradStr != null && !idGradStr.isEmpty()) {
                Grad grad = em.find(Grad.class, Integer.parseInt(idGradStr));
                if (grad != null) k.setGrad(grad);
            }

            // Assign default role "kupac"
            TypedQuery<Uloga> q = em.createQuery(
                "SELECT u FROM Uloga u WHERE u.naziv = 'kupac'", Uloga.class);
            List<Uloga> uloge = q.getResultList();
            if (!uloge.isEmpty()) k.getUlogaList().add(uloge.get(0));

            em.getTransaction().begin();
            em.persist(k);
            em.getTransaction().commit();

            Document resp = newDoc();
            Element  root = resp.createElement("korisnik");
            resp.appendChild(root);
            addElement(resp, root, "id_korisnik",    String.valueOf(k.getIdKorisnik()));
            addElement(resp, root, "korisnicko_ime", k.getKorisnickoIme());
            addElement(resp, root, "poruka",         "Korisnik uspesno kreiran");
            return toXml(resp);

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return error("Greska pri kreiranju korisnika: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // ----------------------------------------------------------------
    // 4. Add money
    // ----------------------------------------------------------------
    private String dodajNovac(String body) {
        EntityManager em = emf.createEntityManager();
        try {
            Document doc = parse(body);
            int id       = Integer.parseInt(getText(doc, "id_korisnik"));
            int iznos    = Integer.parseInt(getText(doc, "iznos"));

            Korisnik k = em.find(Korisnik.class, id);
            if (k == null) return error("Korisnik nije pronadjen");

            em.getTransaction().begin();
            k.setStanjeNovca(k.getStanjeNovca() + iznos);
            em.getTransaction().commit();

            Document resp = newDoc();
            Element  root = resp.createElement("korisnik");
            resp.appendChild(root);
            addElement(resp, root, "id_korisnik",  String.valueOf(k.getIdKorisnik()));
            addElement(resp, root, "stanje_novca", String.valueOf(k.getStanjeNovca()));
            addElement(resp, root, "poruka",       "Novac uspesno dodat");
            return toXml(resp);

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return error("Greska pri dodavanju novca: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // ----------------------------------------------------------------
    // 5. Change address and city
    // ----------------------------------------------------------------
    private String promeniAdresu(String body) {
        EntityManager em = emf.createEntityManager();
        try {
            Document doc = parse(body);
            int id       = Integer.parseInt(getText(doc, "id_korisnik"));

            Korisnik k = em.find(Korisnik.class, id);
            if (k == null) return error("Korisnik nije pronadjen");

            em.getTransaction().begin();
            String adresa = getText(doc, "adresa");
            if (adresa != null && !adresa.isEmpty())
                k.setAdresa(adresa);

            String idGradStr = getText(doc, "id_grad");
            if (idGradStr != null && !idGradStr.isEmpty()) {
                Grad grad = em.find(Grad.class, Integer.parseInt(idGradStr));
                if (grad != null) k.setGrad(grad);
            }
            em.getTransaction().commit();

            Document resp = newDoc();
            Element  root = resp.createElement("korisnik");
            resp.appendChild(root);
            addElement(resp, root, "id_korisnik", String.valueOf(k.getIdKorisnik()));
            addElement(resp, root, "adresa",      k.getAdresa());
            addElement(resp, root, "poruka",      "Adresa uspesno promenjena");
            return toXml(resp);

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return error("Greska pri promeni adrese: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // ----------------------------------------------------------------
    // 6. Get all cities
    // ----------------------------------------------------------------
    private String getAllGradovi() {
        try {
            EntityManager em = emf.createEntityManager();
            List<Grad> gradovi = em.createQuery(
                "SELECT g FROM Grad g", Grad.class).getResultList();
            em.close();

            Document resp = newDoc();
            Element  root = resp.createElement("gradovi");
            resp.appendChild(root);
            for (Grad g : gradovi) {
                Element el = resp.createElement("grad");
                addElement(resp, el, "id_grad", String.valueOf(g.getIdGrad()));
                addElement(resp, el, "naziv",   g.getNaziv());
                root.appendChild(el);
            }
            return toXml(resp);

        } catch (Exception e) {
            return error("Greska pri dohvatanju gradova: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // 7. Get all users
    // ----------------------------------------------------------------
    private String getAllKorisnici() {
        try {
            EntityManager em = emf.createEntityManager();
            List<Korisnik> korisnici = em.createQuery(
                "SELECT k FROM Korisnik k", Korisnik.class).getResultList();
            em.close();

            Document resp = newDoc();
            Element  root = resp.createElement("korisnici");
            resp.appendChild(root);
            for (Korisnik k : korisnici) {
                Element el = resp.createElement("korisnik");
                addElement(resp, el, "id_korisnik",    String.valueOf(k.getIdKorisnik()));
                addElement(resp, el, "korisnicko_ime", k.getKorisnickoIme());
                addElement(resp, el, "ime",            k.getIme());
                addElement(resp, el, "prezime",        k.getPrezime());
                addElement(resp, el, "adresa",         k.getAdresa());
                addElement(resp, el, "stanje_novca",   String.valueOf(k.getStanjeNovca()));
                root.appendChild(el);
            }
            return toXml(resp);

        } catch (Exception e) {
            return error("Greska pri dohvatanju korisnika: " + e.getMessage());
        }
    }

    // ================================================================
    // XML Helper methods
    // ================================================================

    // Parse XML string into Document
    private Document parse(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xml)));
    }

    // Create a new empty Document
    private Document newDoc() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.newDocument();
    }

    // Get text content of first matching tag
    private String getText(Document doc, String tag) {
        NodeList list = doc.getElementsByTagName(tag);
        if (list.getLength() > 0) return list.item(0).getTextContent();
        return "";
    }

    // Add a child element with text content
    private void addElement(Document doc, Element parent,
                             String tag, String value) {
        Element el = doc.createElement(tag);
        el.setTextContent(value != null ? value : "");
        parent.appendChild(el);
    }

    // Serialize Document to XML string
    private String toXml(Document doc) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }

    // Build an XML error response
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