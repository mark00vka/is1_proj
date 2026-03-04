package klijentskaaplikacija;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class XMLTabela {

    // Auto-detect item tag and print as table
    static void ispisi(String xml) {
        if (xml == null || xml.trim().isEmpty()) {
            System.out.println("Nema podataka.");
            return;
        }
        if (xml.contains("<greska>")) {
            System.out.println("Greska: " + extractTag(xml, "greska"));
            return;
        }

        // Detect repeating item tag (first child of root)
        String itemTag = pronadjiItemTag(xml);
        if (itemTag == null) {
            // Single item — just print fields vertically
            ispisSingleItem(xml);
            return;
        }

        // Split into items
        String[] items = xml.split("<" + itemTag + ">");
        if (items.length <= 1) {
            System.out.println("Nema podataka.");
            return;
        }

        // Collect all field names from first item
        String[] polja = izvuciPolja(items[1]);
        if (polja.length == 0) {
            System.out.println("Nema podataka.");
            return;
        }

        // Collect all row data
        String[][] rows = new String[items.length - 1][polja.length];
        for (int i = 1; i < items.length; i++) {
            for (int j = 0; j < polja.length; j++) {
                rows[i - 1][j] = extractTag(items[i], polja[j]);
            }
        }

        // Calculate column widths
        int[] sirine = new int[polja.length];
        for (int j = 0; j < polja.length; j++) {
            sirine[j] = polja[j].length();
        }
        for (String[] row : rows) {
            for (int j = 0; j < polja.length; j++) {
                if (row[j].length() > sirine[j])
                    sirine[j] = row[j].length();
            }
        }

        // Print table
        System.out.println();
        ispisSeparator(sirine);
        ispisRed(polja, sirine, true);
        ispisSeparator(sirine);
        for (String[] row : rows) {
            ispisRed(row, sirine, false);
        }
        ispisSeparator(sirine);
        System.out.println("Ukupno: " + (items.length - 1) + " stavki");
    }

    // Print a single item vertically as key: value pairs
    static void ispisSingleItem(String xml) {
        System.out.println();
        // Find all tags and their values
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
            "<([a-zA-Z_]+)>([^<]*)</\\1>");
        java.util.regex.Matcher m = p.matcher(xml);

        int maxKey = 0;
        java.util.List<String[]> pairs = new java.util.ArrayList<>();
        while (m.find()) {
            String key = m.group(1);
            String val = m.group(2).trim();
            if (!val.isEmpty()) {
                pairs.add(new String[]{key, val});
                if (key.length() > maxKey) maxKey = key.length();
            }
        }

        String separator = "+" + repeat('-', maxKey + 2) +
                           "+" + repeat('-', 32) + "+";
        System.out.println(separator);
        for (String[] pair : pairs) {
            System.out.printf("| %-" + maxKey + "s | %-30s |%n",
                              pair[0], truncate(pair[1], 30));
        }
        System.out.println(separator);
    }

    // Print korpa in a special format
    static void ispisKorpa(String xml) {
        if (xml.contains("prazna")) {
            System.out.println("\nKorpa je prazna.");
            return;
        }
        if (xml.contains("<greska>")) {
            System.out.println("Greska: " + extractTag(xml, "greska"));
            return;
        }

        System.out.println("\n=== KORPA ===");
        String[] stavke = xml.split("<stavka>");
        String[] polja  = {"naziv", "cena", "kolicina"};
        int[]    sirine = {30, 6, 12, 10, 10};

        ispisSeparator(sirine);
        ispisRed(new String[]{"NAZIV", "ID", "CENA (RSD)", "Popust (%)", "KOLICINA"}, sirine, true);
        ispisSeparator(sirine);

        for (int i = 1; i < stavke.length; i++) {
            String[] red = {
                extractTag(stavke[i], "naziv"),
                extractTag(stavke[i], "id_artikl"),
                extractTag(stavke[i], "cena"),
                extractTag(stavke[i], "popust"),
                extractTag(stavke[i], "kolicina")
            };
            ispisRed(red, sirine, false);
        }

        ispisSeparator(sirine);
        Float ukupno = Float.parseFloat(extractTag(xml, "ukupna_cena"));
        String ukupnoFormated = String.format("%.2f", ukupno);
        System.out.printf("  %-60s  %s RSD%n", "UKUPNO:", ukupnoFormated);
        ispisSeparator(sirine);
    }

    // Print wishlist in a special format
    static void ispisWishlist(String xml) {
        if (xml.contains("prazan")) {
            System.out.println("\nWishlist je prazan.");
            return;
        }
        if (xml.contains("<greska>")) {
            System.out.println("Greska: " + extractTag(xml, "greska"));
            return;
        }

        System.out.println("\n=== WISHLIST ===");
        String[] artikli = xml.split("<artikl>");
        int[]    sirine  = {6, 30, 12, 30};

        ispisSeparator(sirine);
        ispisRed(new String[]{"ID", "NAZIV", "CENA (RSD)", "DATUM DODAVANJA"}, sirine, true);
        ispisSeparator(sirine);

        for (int i = 1; i < artikli.length; i++) {
            String[] red = {
                extractTag(artikli[i], "id_artikl"),
                extractTag(artikli[i], "naziv"),
                extractTag(artikli[i], "cena"),
                extractTag(artikli[i], "datum_dodavanja")
            };
            ispisRed(red, sirine, false);
        }

        ispisSeparator(sirine);
        System.out.println("Ukupno: " + (artikli.length - 1) + " artikala u wishlist-u");
    }

    // Print narudzbine
    static void ispisNarudzbine(String xml) {
        if (xml.contains("<greska>")) {
            System.out.println("Greska: " + extractTag(xml, "greska"));
            return;
        }

        String[] narudzbine = xml.split("<narudzbina>");
        if (narudzbine.length <= 1) {
            System.out.println("Nema narudzbina.");
            return;
        }

        for (int i = 1; i < narudzbine.length; i++) {
            String n = narudzbine[i];
            System.out.println("\n" + repeat('=', 55));
            System.out.printf("  Narudzbina #%-5s  Datum: %-20s%n",
                extractTag(n, "id_narudzbine"),
                skratiDatum(extractTag(n, "vreme_kreiranja")));
            System.out.printf("  Adresa: %s, %s%n",
                extractTag(n, "adresa"),
                extractTag(n, "grad"));
            System.out.println(repeat('=', 55));

            String[] stavke  = n.split("<stavka>");
            String[] polja   = {"naziv", "kolicina", "jedinicna_cena"};
            int[]    sirine  = {28, 10, 14};

            ispisSeparator(sirine);
            ispisRed(new String[]{"ARTIKL", "KOLICINA", "CENA (RSD)"}, sirine, true);
            ispisSeparator(sirine);

            for (int j = 1; j < stavke.length; j++) {
                ispisRed(new String[]{
                    extractTag(stavke[j], "naziv"),
                    extractTag(stavke[j], "kolicina"),
                    extractTag(stavke[j], "jedinicna_cena")
                }, sirine, false);
            }

            ispisSeparator(sirine);
            System.out.printf("  %-28s  %s RSD%n",
                "UKUPNO:", extractTag(n, "ukupna_cena"));
        }
    }

    // ============================================================
    // Internal helpers
    // ============================================================

    private static String pronadjiItemTag(String xml) {
        // Find root tag
        java.util.regex.Matcher m =
            java.util.regex.Pattern.compile("<([a-zA-Z_]+)>")
                                   .matcher(xml.trim());
        if (!m.find()) return null;
        String rootTag = m.group(1);

        // Find first child tag
        String inner = xml.substring(xml.indexOf(">") + 1).trim();
        java.util.regex.Matcher m2 =
            java.util.regex.Pattern.compile("<([a-zA-Z_]+)>")
                                   .matcher(inner);
        if (!m2.find()) return null;
        String childTag = m2.group(1);

        // Check it repeats
        if (xml.split("<" + childTag + ">").length > 2) return childTag;

        return null;
    }

    private static String[] izvuciPolja(String itemXml) {
        java.util.List<String> polja = new java.util.ArrayList<>();
        java.util.regex.Matcher m =
            java.util.regex.Pattern.compile("<([a-zA-Z_]+)>([^<]*)</\\1>")
                                   .matcher(itemXml);
        while (m.find()) {
            String tag = m.group(1);
            String val = m.group(2).trim();
            if (!val.isEmpty() && !polja.contains(tag)) {
                polja.add(tag);
            }
        }
        return polja.toArray(new String[0]);
    }

    private static void ispisSeparator(int[] sirine) {
        StringBuilder sb = new StringBuilder("+");
        for (int s : sirine) sb.append(repeat('-', s + 2)).append("+");
        System.out.println(sb);
    }

    private static void ispisRed(String[] vrednosti,
                                  int[] sirine, boolean header) {
        StringBuilder sb = new StringBuilder("|");
        for (int j = 0; j < vrednosti.length; j++) {
            String val = truncate(vrednosti[j], sirine[j]);
            if (header) {
                sb.append(String.format(" %-" + sirine[j] + "s |",
                                       val.toUpperCase()));
            } else {
                sb.append(String.format(" %-" + sirine[j] + "s |", val));
            }
        }
        System.out.println(sb);
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }

    private static String skratiDatum(String datum) {
        if (datum == null || datum.length() < 19) return datum;
        return datum.substring(0, 19);
    }
    
    public static String extractTag(String xml, String tag) {
        String open  = "<"  + tag + ">";
        String close = "</" + tag + ">";
        int start = xml.indexOf(open);
        int end   = xml.indexOf(close);
        if (start == -1 || end == -1) return "";
        return xml.substring(start + open.length(), end).trim();
    }
    
    private static String repeat(char c, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(c);
        return sb.toString();
    }
}
