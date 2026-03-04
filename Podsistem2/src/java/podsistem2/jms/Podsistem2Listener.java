package podsistem2.jms;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import podsistem2.service.Podsistem2Service;

public class Podsistem2Listener implements Runnable {

    private static final String CONNECTION_FACTORY = "jms/ConnectionFactory";
    private static final String REQUEST_QUEUE      = "jms/Podsistem2Queue";

    private Connection connection;
    private Session session;
    private MessageConsumer consumer;
    private final Podsistem2Service service;

    public Podsistem2Listener() {
        this.service = new Podsistem2Service();
    }

    public void start() {
        try {
            Context ctx = new InitialContext();
            ConnectionFactory factory =
                (ConnectionFactory) ctx.lookup(CONNECTION_FACTORY);
            Queue requestQueue = (Queue) ctx.lookup(REQUEST_QUEUE);

            connection = factory.createConnection();
            session    = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer   = session.createConsumer(requestQueue);
            connection.start();

            System.out.println("Podsistem2: JMS Listener je pokrenut i ceka poruke...");

            consumer.setMessageListener(message -> {
                try {
                    if (message instanceof TextMessage) {
                        TextMessage txt  = (TextMessage) message;
                        String action    = txt.getStringProperty("action");
                        String body      = txt.getText();
                        Destination replyTo = txt.getJMSReplyTo();
                        String corrId    = txt.getJMSCorrelationID();

                        System.out.println("Podsistem2 received: " + action);
                        String response = service.process(action, body);

                        if (replyTo != null) {
                            MessageProducer producer =
                                session.createProducer(replyTo);
                            TextMessage reply =
                                session.createTextMessage(response);
                            reply.setJMSCorrelationID(corrId);
                            producer.send(reply);
                            producer.close();
                        }
                    }
                } catch (JMSException e) {
                    System.err.println("Podsistem2 JMS error: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            System.err.println("Podsistem2 failed to start: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (consumer   != null) consumer.close();
            if (session    != null) session.close();
            if (connection != null) connection.close();
        } catch (JMSException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        start();
        try { Thread.currentThread().join(); }
        catch (InterruptedException e) { stop(); }
    }
}