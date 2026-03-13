/*
 * ============================================================
 * POKRETANJE NA ODBRANI — UPUTSTVO ZA DEPLOY NA DRUGOM RACUNARU
 * ============================================================
 *
 * 1. POKRENUTI MySQL WORKBENCH
 *    - Pokrenuti MySQL server ako nije vec pokrenut.
 *
 * 2. UCITAVANJE BAZA PODATAKA
 *    - Pokrenuti PRVO setup.sql (kreira MySQL korisnika root/admin)
 *    - Zatim ucitati redom: podsistem1.sql, podsistem2.sql, podsistem3.sql
 *    - Moze se koristiti File -> Open SQL Script u Workbench-u,
 *      ili rucno napraviti baze pa izvrsiti skripte.
 *    - Persistence.xml koristi: user=root, password=admin u sva tri podsistema.
 *
 * 3. GLASSFISH SERVER
 *    - U NetBeans: Services -> Servers -> dodati GlassFish sa putanje
 *      C:\Program1\glassfish5 (ako nije vec dodat).
 *    - Pokrenuti GlassFish server.
 *
 * 4. KREIRANJE JMS RESURSA (na GlassFish-u)
 *    - Potrebni resursi (kreirati preko GlassFish admin konzole ili asadmin):
 *        Connection Factory:  jms/MyConnectionFactory
 *        Queues:
 *          jms/Subsystem1Queue
 *          jms/Subsystem1ReplyQueue
 *          jms/Subsystem2Queue
 *          jms/Subsystem2ReplyQueue
 *          jms/Subsystem3Queue
 *          jms/Subsystem3ReplyQueue
 *    - JDBC resursi za bazu NISU potrebni (koristimo RESOURCE_LOCAL).
 *      Ako postoji <jta-data-source> u nekom persistence.xml, moze se ukloniti.
 *
 * 5. OTVARANJE PROJEKATA U NETBEANS-U
 *    - CentralniServer — Maven projekat
 *    - Podsistem1, Podsistem2, Podsistem3 — Ant projekti
 *      (Java With Ant -> Java Enterprise -> Enterprise Application Client)
 *    - KlijentskaAplikacija — Ant projekat (obican Java SE)
 *
 * 6. PROMENA VERZIJE JAVE NA JDK 1.8
 *    - Ant projekti: Properties -> Libraries -> Java Platform -> JDK 1.8
 *    - Maven (CentralniServer): Properties -> Compile -> JDK 1.8
 *    - Podrazumevano je JDK 11 na lab racunarima, MORA se promeniti na 1.8!
 *
 * 7. PODESAVANJE BIBLIOTEKA (BITNO — putanja sa razmakom!)
 * -------------TREBALO BI DA RADI VEC SA RELATIVNIM PUTANJAMA------------------
 *    - Na lab racunarima NetBeans je instaliran na putanji sa razmakom
 *      (C:\Program Files\NetBeans\...) sto pravi problem GlassFish-u.
 *    - Sve potrebne biblioteke su upakovane u /libraries/ direktorijum projekta:
 *        libraries/eclipselink/         — EclipseLink (JPA 2.1) JAR-ovi
 *        libraries/javaee-api-8.0.jar   — Java EE 8 API
 *        libraries/mysql-connector-java-5.1.49.jar — MySQL JDBC konektor
 *    - U JEDNOM Ant projektu (npr. Podsistem1):
 *        Properties -> Libraries -> Edit nad "EclipseLink (JPA 2.1)"
 *        -> ukloniti postojece JAR fajlove
 *        -> dodati sve JAR-ove iz libraries/eclipselink/
 *    - Isto uraditi za "Java EE 8 API":
 *        -> ukloniti postojeci JAR
 *        -> dodati libraries/javaee-api-8.0.jar
 *    - Dodati MySQL konektor (libraries/mysql-connector-java-5.1.49.jar)
 *      kao biblioteku u svim Ant podsistemima.
 *    - Ovo ce se automatski primeniti na sve projekte koji koriste iste biblioteke.
 *
 * 8. POKRETANJE
 *    - Prvo deployovati CentralniServer na GlassFish (Run)
 *    - Zatim pokrenuti Podsistem1, Podsistem2, Podsistem3 (Run)
 *    - Na kraju pokrenuti KlijentskaAplikacija
 *
 * 9. NAPOMENE
 *    - Svi persistence.xml koriste transaction-type="RESOURCE_LOCAL",
 *      tako da JDBC/JTA resursi na GlassFish-u NISU potrebni za bazu.
 *    - MySQL konektor verzija nije bitna (5.1.49 ili 8.0.20 rade),
 *      ali driver klasa mora da odgovara:
 *        5.x -> com.mysql.jdbc.Driver
 *        8.x -> com.mysql.cj.jdbc.Driver
 *    - Trenutno projekat koristi 5.1.49 sa com.mysql.jdbc.Driver.
 *
 * ============================================================
 */
package centralniserver;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api")
public class RestApplication extends Application {
        
}