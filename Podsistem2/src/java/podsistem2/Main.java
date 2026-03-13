package podsistem2;

import podsistem2.jms.Podsistem2Listener;

public class Main {
    public static void main(String[] args) {
        Podsistem2Listener listener = new Podsistem2Listener();
        Thread t = new Thread(listener);
        t.setDaemon(false);
        t.start();
        System.out.println("Podsistem2 je pokrenut i ceka poruke...");
    }
}
