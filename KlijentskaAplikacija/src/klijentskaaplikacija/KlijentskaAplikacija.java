package klijentskaaplikacija;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import static klijentskaaplikacija.XMLTabela.extractTag;

public class KlijentskaAplikacija {

    private static final String BASE_URL = "http://localhost:8080/CentralniServer/api";
    private static String token = null;
    private static String uloga = null;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Dobrodosli u Online Prodavnicu ===");

        while (true) {
            if (token == null) {
                prikaziMeniGost();
            } else {
                prikaziMeniKorisnik();
            }
        }
    }
    
    private static void prikaziMeniKorisnik() {
        boolean isAdmin = "administrator".equals(uloga);

        System.out.println("\n--- Meni" + (isAdmin ? " (Administrator)" : " (Kupac)") + " ---");
        System.out.println("--- Artikli ---");
        System.out.println("1.  Moji artikli");
        System.out.println("2.  Kreiraj artikl");
        System.out.println("3.  Promeni cenu artikla");
        System.out.println("4.  Postavi popust");
        System.out.println("--- Korpa & Wishlist ---");
        System.out.println("5.  Pregled korpe");
        System.out.println("6.  Dodaj u korpu");
        System.out.println("7.  Obrisi iz korpe");
        System.out.println("8.  Pregled wishlist-e");
        System.out.println("9.  Dodaj u wishlist");
        System.out.println("10. Obrisi iz wishlist-e");
        System.out.println("--- Narudzbine ---");
        System.out.println("11. Moje narudzbine");
        System.out.println("12. Plati (naruci)");
        System.out.println("--- Nalog ---");
        System.out.println("13. Pregled gradova");
        System.out.println("14. Pregled kategorija");
        System.out.println("15. Dodaj novac na racun");
        System.out.println("16. Promeni adresu");

        if (isAdmin) {
            System.out.println("--- Admin ---");
            System.out.println("17. Svi korisnici");
            System.out.println("18. Kreiraj korisnika");
            System.out.println("19. Dodaj novac korisniku");
            System.out.println("20. Promeni adresu korisnika");
            System.out.println("21. Kreiraj grad");
            System.out.println("22. Kreiraj kategoriju");
            System.out.println("23. Sve narudzbine");
            System.out.println("24. Sve transakcije");
        }

        System.out.println("0.  Odjava");
        System.out.print("Izbor: ");

        String izbor = scanner.nextLine().trim();
        switch (izbor) {
            case "1":  mojiArtikli();       break;
            case "2":  kreirajArtikl();     break;
            case "3":  promeniCenu();       break;
            case "4":  postaviPopust();     break;
            case "5":  pregledKorpe();      break;
            case "6":  dodajUKorpu();       break;
            case "7":  obrisiIzKorpe();     break;
            case "8":  pregledWishlist();   break;
            case "9":  dodajUWishlist();    break;
            case "10": obrisiIzWishlist();  break;
            case "11": mojeNarudzbine();    break;
            case "12": plati();             break;
            case "13": pregledGradova();    break;
            case "14": pregledKategorija(); break;
            case "15": dodajNovac();        break;
            case "16": promeniAdresu();     break;
            case "17": if (isAdmin) sviKorisnici();     break;
            case "18": if (isAdmin) kreirajKorisnika(); break;
            case "19": if (isAdmin) dodajNovac();       break;
            case "20": if (isAdmin) promeniAdresu();    break;
            case "21": if (isAdmin) kreirajGrad();      break;
            case "22": if (isAdmin) kreirajKategoriju();break;
            case "23": if (isAdmin) sveNarudzbine();    break;
            case "24": if (isAdmin) sveTransakcije();   break;
            case "0":  odjava(); break;
            default:   System.out.println("Nepoznata opcija.");
    }
}
    
    // ================================================================
    // Menus
    // ================================================================

    private static void prikaziMeniGost() {
        System.out.println("\n--- Meni ---");
        System.out.println("1. Prijava");
        System.out.println("2. Registracija");
        System.out.println("0. Izlaz");
        System.out.print("Izbor: ");

        String izbor = scanner.nextLine().trim();
        switch (izbor) {
            case "1": prijava();        break;
            case "2": registracija();   break;
            case "0":
                System.out.println("Dovidjenja!");
                System.exit(0);
            default:
                System.out.println("Nepoznat izbor.");
        }
    }

    
    
    // ================================================================
    // Auth
    // ================================================================

    private static void prijava() {
        System.out.print("Korisnicko ime: ");
        String ime = scanner.nextLine().trim();
        System.out.print("Sifra: ");
        String sifra = scanner.nextLine().trim();

        String xml = "<request><korisnicko_ime>" + ime +
                     "</korisnicko_ime><sifra>" + sifra + "</sifra></request>";

        String response = sendRequest("POST", "/korisnici/login", xml, null);
        XMLTabela.ispisi(response);

        if (response.contains("<id_korisnik>")) {
            token = extractTag(response, "id_korisnik");
            uloga = extractTag(response, "uloga");
            System.out.println("Prijavljeni ste kao: " + ime +
                               " (uloga: " + uloga + ")");
        } else {
            System.out.println("Prijava neuspesna.");
        }
    }

    private static void registracija() {
        System.out.print("Korisnicko ime: ");
        String korime = scanner.nextLine().trim();
        System.out.print("Sifra: ");
        String sifra = scanner.nextLine().trim();
        System.out.print("Ime: ");
        String ime = scanner.nextLine().trim();
        System.out.print("Prezime: ");
        String prezime = scanner.nextLine().trim();
        System.out.print("Adresa: ");
        String adresa = scanner.nextLine().trim();

        // Show cities first
        pregledGradova();
        System.out.print("ID grada: ");
        String idGrad = scanner.nextLine().trim();

        String xml = "<request>" +
            "<korisnicko_ime>" + korime    + "</korisnicko_ime>" +
            "<sifra>"          + sifra     + "</sifra>" +
            "<ime>"            + ime       + "</ime>" +
            "<prezime>"        + prezime   + "</prezime>" +
            "<adresa>"         + adresa    + "</adresa>" +
            "<id_grad>"        + idGrad    + "</id_grad>" +
            "</request>";

        String response = sendRequest("POST", "/korisnici", xml, null);
        System.out.println(response.contains("<id_korisnik>")
            ? "Registracija uspesna! Mozete se prijaviti."
            : response);
    }

    private static void odjava() {
        token = null;
        uloga = null;
        System.out.println("Odjavili ste se.");
    }

    // ================================================================
    // Korisnici (admin)
    // ================================================================

    private static void sviKorisnici() {
        XMLTabela.ispisi(sendRequest("GET", "/korisnici", null, token));
    }

    private static void kreirajKorisnika() {
        System.out.print("Korisnicko ime: ");
        String korime = scanner.nextLine().trim();
        System.out.print("Sifra: ");
        String sifra = scanner.nextLine().trim();
        System.out.print("Ime: ");
        String ime = scanner.nextLine().trim();
        System.out.print("Prezime: ");
        String prezime = scanner.nextLine().trim();
        System.out.print("Adresa: ");
        String adresa = scanner.nextLine().trim();
        pregledGradova();
        System.out.print("ID grada: ");
        String idGrad = scanner.nextLine().trim();

        String xml = "<request>" +
            "<korisnicko_ime>" + korime  + "</korisnicko_ime>" +
            "<sifra>"          + sifra   + "</sifra>" +
            "<ime>"            + ime     + "</ime>" +
            "<prezime>"        + prezime + "</prezime>" +
            "<adresa>"         + adresa  + "</adresa>" +
            "<id_grad>"        + idGrad  + "</id_grad>" +
            "</request>";

        XMLTabela.ispisi(sendRequest("POST", "/korisnici", xml, token));
    }

    private static void dodajNovac() {
        System.out.print("ID korisnika: ");
        String id = scanner.nextLine().trim();
        System.out.print("Iznos: ");
        String iznos = scanner.nextLine().trim();

        String xml = "<request><iznos>" + iznos + "</iznos></request>";
        XMLTabela.ispisi(
            sendRequest("PUT", "/korisnici/" + id + "/novac", xml, token));
    }

    private static void promeniAdresu() {
        System.out.print("ID korisnika: ");
        String id = scanner.nextLine().trim();
        System.out.print("Nova adresa: ");
        String adresa = scanner.nextLine().trim();
        pregledGradova();
        System.out.print("ID novog grada: ");
        String idGrad = scanner.nextLine().trim();

        String xml = "<request>" +
            "<adresa>"  + adresa + "</adresa>" +
            "<id_grad>" + idGrad + "</id_grad>" +
            "</request>";
        XMLTabela.ispisi(
            sendRequest("PUT", "/korisnici/" + id + "/adresa", xml, token));
    }

    // ================================================================
    // Gradovi
    // ================================================================

    private static void pregledGradova() {
        String response = sendRequest("GET", "/gradovi", null, token);
        XMLTabela.ispisi(response);
    }

    private static void kreirajGrad() {
        System.out.print("Naziv grada: ");
        String naziv = scanner.nextLine().trim();
        String xml   = "<request><naziv>" + naziv + "</naziv></request>";
        XMLTabela.ispisi(sendRequest("POST", "/gradovi", xml, token));
    }

    // ================================================================
    // Kategorije
    // ================================================================

    private static void pregledKategorija() {
        String response = sendRequest("GET", "/kategorije", null, token);
        XMLTabela.ispisi(response);
    }

    private static void kreirajKategoriju() {
        System.out.print("Naziv kategorije: ");
        String naziv = scanner.nextLine().trim();
        System.out.print("ID nadkategorije (Enter za prazno): ");
        String nadkat = scanner.nextLine().trim();

        String xml = "<request><naziv>" + naziv + "</naziv>" +
            (nadkat.isEmpty() ? "" :
             "<id_nadkategorija>" + nadkat + "</id_nadkategorija>") +
            "</request>";
        XMLTabela.ispisi(sendRequest("POST", "/kategorije", xml, token));
    }

    // ================================================================
    // Artikli
    // ================================================================

    private static void mojiArtikli() {
         XMLTabela.ispisi(sendRequest("GET", "/artikli/moji", null, token));
    }

    private static void kreirajArtikl() {
        System.out.print("Naziv: ");
        String naziv = scanner.nextLine().trim();
        System.out.print("Opis: ");
        String opis = scanner.nextLine().trim();
        System.out.print("Cena: ");
        String cena = scanner.nextLine().trim();
        System.out.print("Popust (0 za bez popusta): ");
        String popust = scanner.nextLine().trim();
        pregledKategorija();
        System.out.print("ID kategorije: ");
        String idKat = scanner.nextLine().trim();

        String xml = "<request>" +
            "<naziv>"        + naziv  + "</naziv>" +
            "<opis>"         + opis   + "</opis>" +
            "<cena>"         + cena   + "</cena>" +
            "<popust>"       + popust + "</popust>" +
            "<id_kategorija>"+ idKat  + "</id_kategorija>" +
            "</request>";
        XMLTabela.ispisi(sendRequest("POST", "/artikli", xml, token));
    }

    private static void promeniCenu() {
        mojiArtikli();
        System.out.print("ID artikla: ");
        String idArtikl = scanner.nextLine().trim();
        System.out.print("Nova cena: ");
        String cena = scanner.nextLine().trim();

        String xml = "<request><cena>" + cena + "</cena></request>";
        XMLTabela.ispisi(
            sendRequest("PUT", "/artikli/" + idArtikl + "/cena", xml, token));
    }

    private static void postaviPopust() {
        mojiArtikli();
        System.out.print("ID artikla: ");
        String idArtikl = scanner.nextLine().trim();
        System.out.print("Popust (%): ");
        String popust = scanner.nextLine().trim();

        String xml = "<request><popust>" + popust + "</popust></request>";
        XMLTabela.ispisi(
            sendRequest("PUT", "/artikli/" + idArtikl + "/popust", xml, token));
    }

    // ================================================================
    // Korpa
    // ================================================================

    private static void pregledKorpe() {
        XMLTabela.ispisKorpa(sendRequest("GET", "/korpa", null, token));
    }

    private static void dodajUKorpu() {
        System.out.print("ID artikla: ");
        String idArtikl = scanner.nextLine().trim();
        System.out.print("Kolicina: ");
        String kolicina = scanner.nextLine().trim();

        String xml = "<request>" +
            "<id_artikl>" + idArtikl + "</id_artikl>" +
            "<kolicina>"  + kolicina + "</kolicina>" +
            "</request>";
        XMLTabela.ispisi(sendRequest("POST", "/korpa", xml, token));
    }

    private static void obrisiIzKorpe() {
        pregledKorpe();
        System.out.print("ID artikla: ");
        String idArtikl = scanner.nextLine().trim();
        System.out.print("Kolicina: ");
        String kolicina = scanner.nextLine().trim();

        String xml = "<request>" +
            "<id_artikl>" + idArtikl + "</id_artikl>" +
            "<kolicina>"  + kolicina + "</kolicina>" +
            "</request>";
        XMLTabela.ispisi(sendRequest("DELETE", "/korpa", xml, token));
    }

    // ================================================================
    // Wishlist
    // ================================================================

    private static void pregledWishlist() {
        XMLTabela.ispisi(sendRequest("GET", "/korpa/wishlist", null, token));
    }

    private static void dodajUWishlist() {
        System.out.print("ID artikla: ");
        String idArtikl = scanner.nextLine().trim();

        String xml = "<request>" +
            "<id_artikl>" + idArtikl + "</id_artikl></request>";
        XMLTabela.ispisi(
            sendRequest("POST", "/korpa/wishlist", xml, token));
    }

    private static void obrisiIzWishlist() {
        pregledWishlist();
        System.out.print("ID artikla za brisanje: ");
        String idArtikl = scanner.nextLine().trim();

        String xml = "<request>" +
            "<id_artikl>" + idArtikl + "</id_artikl></request>";
        XMLTabela.ispisi(
            sendRequest("DELETE", "/korpa/wishlist", xml, token));
    }

    // ================================================================
    // Narudzbine
    // ================================================================

    private static void mojeNarudzbine() {
        XMLTabela.ispisNarudzbine(
            sendRequest("GET", "/narudzbine/moje", null, token));
    }

    private static void sveNarudzbine() {
        XMLTabela.ispisNarudzbine(
            sendRequest("GET", "/narudzbine", null, token));
    }

    private static void sveTransakcije() {
        XMLTabela.ispisi(
            sendRequest("GET", "/narudzbine/transakcije", null, token));
    }

    private static void plati() {
        // Show cart first
        String korpa = sendRequest("GET", "/korpa", null, token);
        XMLTabela.ispisi(korpa);

        if (korpa.contains("prazna")) {
            XMLTabela.ispisi("Korpa je prazna, nema sta da se plati.");
            return;
        }

        System.out.print("Adresa dostave: ");
        String adresa = scanner.nextLine().trim();
        pregledGradova();
        System.out.print("ID grada dostave: ");
        String idGrad = scanner.nextLine().trim();

        // Build stavke from cart XML
        StringBuilder stavke = new StringBuilder("<stavke>");
        // Parse each stavka from korpa response
        String[] parts = korpa.split("<stavka>");
        for (int i = 1; i < parts.length; i++) {
            String idArtikl  = extractTag(parts[i], "id_artikl");
            String kolicina  = extractTag(parts[i], "kolicina");
            String cena      = extractTag(parts[i], "cena");
            stavke.append("<stavka>")
                  .append("<id_artikl>").append(idArtikl).append("</id_artikl>")
                  .append("<kolicina>").append(kolicina).append("</kolicina>")
                  .append("<jedinicna_cena>").append(cena).append("</jedinicna_cena>")
                  .append("</stavka>");
        }
        stavke.append("</stavke>");

        String xml = "<request>" +
            "<adresa>"  + adresa + "</adresa>" +
            "<id_grad>" + idGrad + "</id_grad>" +
            stavke +
            "</request>";

        String response = sendRequest("POST", "/narudzbine/plati", xml, token);
        if (response.contains("<placanje>")) {
            System.out.println("=== Placanje uspesno! ===");
            System.out.println("Narudzbina: " +
                extractTag(response, "id_narudzbine"));
            System.out.println("Placeno: " +
                extractTag(response, "placena_suma") + " RSD");
            System.out.println("Novo stanje: " +
                extractTag(response, "stanje_novca") + " RSD");
        } else {
            XMLTabela.ispisi(response);
        }
    }

    // ================================================================
    // HTTP helper
    // ================================================================

    private static String sendRequest(String method, String endpoint,
                                       String xmlBody, String bearerToken) {
        try {
            URL url = new URL(BASE_URL + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/xml");
            conn.setRequestProperty("Accept", "application/xml");

            if (bearerToken != null) {
                conn.setRequestProperty("Authorization",
                                        "Bearer " + bearerToken);
            }

            if (xmlBody != null) {
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(xmlBody.getBytes("UTF-8"));
                }
            }

            int status = conn.getResponseCode();
            BufferedReader reader;
            if (status >= 200 && status < 300) {
                reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                reader = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString().trim();

        } catch (Exception e) {
            return "<greska>" + e.getMessage() + "</greska>";
        }
    }
}