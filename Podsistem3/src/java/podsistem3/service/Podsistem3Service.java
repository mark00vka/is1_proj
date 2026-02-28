package podsistem3.service;

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

public class Podsistem3Service {

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/podsistem3?useSSL=false";
    private static final String DB1_URL = "jdbc:mysql://localhost:3306/podsistem1?useSSL=false";
    private static final String DB2_URL = "jdbc:mysql://localhost:3306/podsistem2?useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "admin";

    private final EntityManagerFactory emf;

    public Podsistem3Service() {
        try { Class.forName("com.mysql.jdbc.Driver"); }
        catch (ClassNotFoundException e) { System.err.println(e.getMessage()); }

        Map<String, String> props = new HashMap<>();
        props.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
        props.put("javax.persistence.jdbc.url",    DB_URL);
        props.put("javax.persistence.jdbc.user",   DB_USER);
        props.put("javax.persistence.jdbc.password", DB_PASS);
        this.emf = Persistence.createEntityManagerFactory("Podsistem3PU", props);
    }

    // ----------------------------------------------------------------
    // JDBC helpers — fetch from podsistem1 and podsistem2
    // ----------------------------------------------------------------

    private boolean korisnikPostoji(int id) {
        return fetchInt(DB1_URL,
            "SELECT id_korisnik FROM korisnik WHERE id_korisnik = ?", id) >= 0;
    }

    private int getStanjeNovca(int id) {
        return fetchInt(DB1_URL,
            "SELECT stanje_novca FROM korisnik WHERE id_korisnik = ?", id);
    }

    private void updateStanjeNovca(int idKorisnik, int novoStanje) {
        try (Connection conn = DriverManager.getConnection(DB1_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(
                 "UPDATE korisnik SET stanje_novca = ? WHERE id_korisnik = ?")) {
            ps.setInt(1, novoStanje);
            ps.setInt(2, idKorisnik);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error updating stanje_novca: " + e.getMessage());
        }
    }

    private boolean gradPostoji(int id) {
        return fetchInt(DB1_URL,
            "SELECT id_grad FROM grad WHERE id_grad = ?", id) >= 0;
    }

    private String getGradNaziv(int id) {
        try (Connection conn = DriverManager.getConnection(DB1_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT naziv FROM grad WHERE id_grad = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("naziv");
        } catch (Exception e) {
            System.err.println("Error getting grad: " + e.getMessage());
        }
        return "";
    }

    // Fetch artikl info from podsistem2
    private double[] getArtiklInfo(int idArtikl) {
        // Returns [cena, popust] or null if not found
        try (Connection conn = DriverManager.getConnection(DB2_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT cena, popust FROM artikl WHERE id_artikl = ?")) {
            ps.setInt(1, idArtikl);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new double[]{rs.getDouble("cena"), rs.getDouble("popust")};
            }
        } catch (Exception e) {
            System.err.println("Error getting artikl: " + e.getMessage());
        }
        return null;
    }

    private String getArtiklNaziv(int idArtikl) {
        try (Connection conn = DriverManager.getConnection(DB2_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT naziv FROM artikl WHERE id_artikl = ?")) {
            ps.setInt(1, idArtikl);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("naziv");
        } catch (Exception e) {
            System.err.println("Error getting artikl naziv: " + e.getMessage());
        }
        return "";
    }

    // Clear cart in podsistem2 after payment
    private void ocistiKorpu(int idKorisnik) {
        try (Connection conn = DriverManager.getConnection(DB2_URL, DB_USER, DB_PASS);
             PreparedStatement ps1 = conn.prepareStatement(
                 "DELETE ka FROM korpa_artikl ka " +
                 "JOIN korpa k ON ka.id_korpa = k.id_korpa " +
                 "WHERE k.id_korisnik = ?");
             PreparedStatement ps2 = conn.prepareStatement(
                 "UPDATE korpa SET ukupna_cena = 0 WHERE id_korisnik = ?")) {
            ps1.setInt(1, idKorisnik);
            ps1.executeUpdate();
            ps2.setInt(1, idKorisnik);
            ps2.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error clearing korpa: " + e.getMessage());
        }
    }

    private int fetchInt(String url, String sql, int param) {
        try (Connection conn = DriverManager.getConnection(url, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, param);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println("fetchInt error: " + e.getMessage());
        }
        return -1;
    }

    // ----------------------------------------------------------------
    // Router
    // ----------------------------------------------------------------
    public String process(String action, String body) {
        switch (action) {
            case "PLATI":               return plati(body);
            case "GET_NARUDZBINE":      return getNarudzbine(body);
            case "GET_ALL_NARUDZBINE":  return getAllNarudzbine();
            case "GET_ALL_TRANSAKCIJE": return getAllTransakcije();
            default: return error("Unknown action: " + action);
        }
    }

    // ----------------------------------------------------------------
    // PLATI
    // ----------------------------------------------------------------
    private String plati(String body) {
        EntityManager em = emf.createEntityManager();
        try {
            Document doc   = parse(body);
            int idKorisnik = Integer.parseInt(getText(doc, "id_korisnik"));
            String adresa  = getText(doc, "adresa");
            int idGrad     = Integer.parseInt(getText(doc, "id_grad"));

            // Validate from podsistem1
            if (!korisnikPostoji(idKorisnik))
                return error("Korisnik nije pronadjen");
            if (!gradPostoji(idGrad))
                return error("Grad nije pronadjen");

            // Parse stavke
            NodeList stavkeNodes = doc.getElementsByTagName("stavka");
            if (stavkeNodes.getLength() == 0)
                return error("Korpa je prazna");

            // Calculate total using prices from podsistem2
            double ukupno = 0;
            for (int i = 0; i < stavkeNodes.getLength(); i++) {
                Element stavkaEl = (Element) stavkeNodes.item(i);
                int idArtikl = Integer.parseInt(
                    stavkaEl.getElementsByTagName("id_artikl")
                            .item(0).getTextContent());
                int kolicina = Integer.parseInt(
                    stavkaEl.getElementsByTagName("kolicina")
                            .item(0).getTextContent());

                double[] info = getArtiklInfo(idArtikl);
                if (info == null)
                    return error("Artikl " + idArtikl + " nije pronadjen");

                double jedinicnaCena = info[0] * (1 - info[1] / 100);
                ukupno += jedinicnaCena * kolicina;
            }

            // Check funds
            int stanjeNovca = getStanjeNovca(idKorisnik);
            if (stanjeNovca < ukupno)
                return error("Nedovoljno sredstava. Stanje: " + stanjeNovca
                             + ", potrebno: " + ukupno);

            em.getTransaction().begin();

            // Create order — store only ids, no FK to other DBs
            Narudzbina narudzbina = new Narudzbina();
            narudzbina.setIdKupac(idKorisnik);
            narudzbina.setAdresa(adresa);
            narudzbina.setIdGradDostava(idGrad);
            narudzbina.setVremeKreiranja(new Date());
            narudzbina.setUkupnaCena(ukupno);
            em.persist(narudzbina);
            em.flush();

            // Create stavke
            for (int i = 0; i < stavkeNodes.getLength(); i++) {
                Element stavkaEl = (Element) stavkeNodes.item(i);
                int idArtikl = Integer.parseInt(
                    stavkaEl.getElementsByTagName("id_artikl")
                            .item(0).getTextContent());
                int kolicina = Integer.parseInt(
                    stavkaEl.getElementsByTagName("kolicina")
                            .item(0).getTextContent());

                double[] info      = getArtiklInfo(idArtikl);
                double jedinicnaCena = info[0] * (1 - info[1] / 100);

                Stavka stavka = new Stavka();
                stavka.setNarudzbina(narudzbina);
                stavka.setIdArtikl(idArtikl);
                stavka.setKolicina(kolicina);
                stavka.setJedinicnaCena(jedinicnaCena);
                em.persist(stavka);
            }

            // Create transaction
            Transakcija transakcija = new Transakcija();
            transakcija.setIdKupac(idKorisnik);
            transakcija.setNarudzbina(narudzbina);
            transakcija.setPlacenaSuma(ukupno);
            transakcija.setVremePlacanja(new Date());
            em.persist(transakcija);

            em.getTransaction().commit();

            // Deduct money in podsistem1
            updateStanjeNovca(idKorisnik, (int)(stanjeNovca - ukupno));

            // Clear cart in podsistem2
            ocistiKorpu(idKorisnik);

            Document resp = newDoc();
            Element root  = resp.createElement("placanje");
            resp.appendChild(root);
            addEl(resp, root, "id_narudzbine",
                  String.valueOf(narudzbina.getIdNarudzbine()));
            addEl(resp, root, "id_transakcija",
                  String.valueOf(transakcija.getIdTransakcija()));
            addEl(resp, root, "placena_suma",   String.valueOf(ukupno));
            addEl(resp, root, "stanje_novca",
                  String.valueOf((int)(stanjeNovca - ukupno)));
            addEl(resp, root, "poruka", "Placanje uspesno izvrseno");
            return toXml(resp);

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return error("Greska pri placanju: " + e.getMessage());
        } finally { em.close(); }
    }

    // ----------------------------------------------------------------
    // Get orders for user
    // ----------------------------------------------------------------
    private String getNarudzbine(String body) {
        try {
            Document doc   = parse(body);
            int idKorisnik = Integer.parseInt(getText(doc, "id_korisnik"));

            EntityManager em = emf.createEntityManager();
            TypedQuery<Narudzbina> q = em.createQuery(
                "SELECT n FROM Narudzbina n WHERE n.idKupac = :id",
                Narudzbina.class);
            q.setParameter("id", idKorisnik);
            List<Narudzbina> narudzbine = q.getResultList();
            em.close();
            return buildNarudzbineXml(narudzbine);
        } catch (Exception e) {
            return error("Greska: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // Get all orders
    // ----------------------------------------------------------------
    private String getAllNarudzbine() {
        try {
            EntityManager em = emf.createEntityManager();
            List<Narudzbina> narudzbine = em.createQuery(
                "SELECT n FROM Narudzbina n", Narudzbina.class).getResultList();
            em.close();
            return buildNarudzbineXml(narudzbine);
        } catch (Exception e) {
            return error("Greska: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // Get all transactions
    // ----------------------------------------------------------------
    private String getAllTransakcije() {
        try {
            EntityManager em = emf.createEntityManager();
            List<Transakcija> transakcije = em.createQuery(
                "SELECT t FROM Transakcija t", Transakcija.class).getResultList();
            em.close();

            Document resp = newDoc();
            Element root  = resp.createElement("transakcije");
            resp.appendChild(root);
            for (Transakcija t : transakcije) {
                Element el = resp.createElement("transakcija");
                addEl(resp, el, "id_transakcija",
                      String.valueOf(t.getIdTransakcija()));
                addEl(resp, el, "placena_suma",
                      String.valueOf(t.getPlacenaSuma()));
                addEl(resp, el, "vreme_placanja",
                      t.getVremePlacanja().toString());
                addEl(resp, el, "id_narudzbine",
                      String.valueOf(t.getNarudzbina().getIdNarudzbine()));
                addEl(resp, el, "id_kupac",
                      String.valueOf(t.getIdKupac()));
                root.appendChild(el);
            }
            return toXml(resp);
        } catch (Exception e) {
            return error("Greska: " + e.getMessage());
        }
    }

    private String buildNarudzbineXml(List<Narudzbina> narudzbine)
            throws Exception {
        Document resp = newDoc();
        Element root  = resp.createElement("narudzbine");
        resp.appendChild(root);
        for (Narudzbina n : narudzbine) {
            Element el = resp.createElement("narudzbina");
            addEl(resp, el, "id_narudzbine",
                  String.valueOf(n.getIdNarudzbine()));
            addEl(resp, el, "ukupna_cena",
                  String.valueOf(n.getUkupnaCena()));
            addEl(resp, el, "vreme_kreiranja",
                  n.getVremeKreiranja().toString());
            addEl(resp, el, "adresa",  n.getAdresa());
            addEl(resp, el, "grad",    getGradNaziv(n.getIdGradDostava()));

            Element stavkeEl = resp.createElement("stavke");
            for (Stavka s : n.getStavkaList()) {
                Element stavkaEl = resp.createElement("stavka");
                addEl(resp, stavkaEl, "naziv",
                      getArtiklNaziv(s.getIdArtikl()));
                addEl(resp, stavkaEl, "kolicina",
                      String.valueOf(s.getKolicina()));
                addEl(resp, stavkaEl, "jedinicna_cena",
                      String.valueOf(s.getJedinicnaCena()));
                stavkeEl.appendChild(stavkaEl);
            }
            el.appendChild(stavkeEl);
            root.appendChild(el);
        }
        return toXml(resp);
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