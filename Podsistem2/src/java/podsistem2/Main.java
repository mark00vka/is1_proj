package podsistem2;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.*;
import podsistem2.jms.Podsistem2Listener;

public class Main {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Map<String, String> props = new HashMap<>();
            props.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
            props.put("javax.persistence.jdbc.url",
                      "jdbc:mysql://localhost:3306/podsistem2?useSSL=false");
            props.put("javax.persistence.jdbc.user", "root");
            props.put("javax.persistence.jdbc.password", "admin");

            EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("Podsistem2PU", props);
            EntityManager em = emf.createEntityManager();
            System.out.println("Podsistem2: DB konekcija OK");
            em.close();
            emf.close();
        } catch (Exception e) {
            System.err.println("Podsistem2: DB greska: " + e.getMessage());
            return;
        }

        Podsistem2Listener listener = new Podsistem2Listener();
        Thread t = new Thread(listener);
        t.setDaemon(false);
        t.start();
        System.out.println("Podsistem2 je pokrenut i ceka poruke...");
    }
}