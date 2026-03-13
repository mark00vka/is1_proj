package podsistem3;

import podsistem3.jms.Podsistem3Listener;

public class Main {
    public static void main(String[] args) {
        Podsistem3Listener listener = new Podsistem3Listener();
        Thread t = new Thread(listener);
        t.setDaemon(false);
        t.start();
        System.out.println("Podsistem3 je pokrenut i ceka poruke...");
    }
}
