package podsistem1;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import podsistem1.jms.Podsistem1Listener;

public class Main {

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Map<String, String> props = new HashMap<>();
            props.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
            props.put("javax.persistence.jdbc.url",
                      "jdbc:mysql://localhost:3306/podsistem1?useSSL=false");
            props.put("javax.persistence.jdbc.user", "root");
            props.put("javax.persistence.jdbc.password", "admin");

            EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("Podsistem1PU", props);
            EntityManager em = emf.createEntityManager();
            System.out.println("Podsistem1: DB konekcija OK");
            em.close();
            emf.close();
        } catch (Exception e) {
            System.err.println("Podsistem1: DB greska: " + e.getMessage());
            return;
        }

        // Start JMS listener in a background thread
        Podsistem1Listener listener = new Podsistem1Listener();
        Thread listenerThread = new Thread(listener);
        listenerThread.setDaemon(false);
        listenerThread.start();

        System.out.println("Podsistem1 je pokrenut i ceka poruke...");
    }
}