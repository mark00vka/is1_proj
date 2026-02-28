package podsistem3;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.*;
import podsistem3.jms.Podsistem3Listener;

public class Main {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Map<String, String> props = new HashMap<>();
            props.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
            props.put("javax.persistence.jdbc.url",
                      "jdbc:mysql://localhost:3306/podsistem3?useSSL=false");
            props.put("javax.persistence.jdbc.user", "root");
            props.put("javax.persistence.jdbc.password", "admin");

            EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("Podsistem3PU", props);
            EntityManager em = emf.createEntityManager();
            System.out.println("Podsistem3: DB konekcija OK");
            em.close();
            emf.close();
        } catch (Exception e) {
            System.err.println("Podsistem3: DB greska: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        Podsistem3Listener listener = new Podsistem3Listener();
        Thread t = new Thread(listener);
        t.setDaemon(false);
        t.start();
        System.out.println("Podsistem3 je pokrenut i ceka poruke...");
    }
}