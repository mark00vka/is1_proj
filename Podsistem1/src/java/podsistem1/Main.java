package podsistem1;

import podsistem1.jms.Podsistem1Listener;

public class Main {

    public static void main(String[] args) {
        // Start JMS listener in a background thread
        Podsistem1Listener listener = new Podsistem1Listener();
        Thread listenerThread = new Thread(listener);
        listenerThread.setDaemon(false);
        listenerThread.start();

        System.out.println("Podsistem1 je pokrenut i ceka poruke...");
    }
}
