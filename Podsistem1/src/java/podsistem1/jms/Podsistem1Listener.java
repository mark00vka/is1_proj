/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package podsistem1.jms;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import podsistem1.service.Podsistem1Service;

public class Podsistem1Listener implements Runnable {

    private static final String CONNECTION_FACTORY = "jms/ConnectionFactory";
    private static final String REQUEST_QUEUE      = "jms/Podsistem1Queue";
    
    private Connection connection;
    private Session session;
    private MessageConsumer consumer;
    private final Podsistem1Service service;

    public Podsistem1Listener() {
        this.service = new Podsistem1Service();
    }

    public void start() {
        try {
            Context ctx = new InitialContext();
            ConnectionFactory factory = 
                (ConnectionFactory) ctx.lookup(CONNECTION_FACTORY);
            Queue requestQueue = (Queue) ctx.lookup(REQUEST_QUEUE);

            connection = factory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(requestQueue);
            connection.start();

            System.out.println("Podsistem1: JMS Listener started, waiting for messages...");

            consumer.setMessageListener(message -> {
                try {
                    if (message instanceof TextMessage) {
                        TextMessage textMsg = (TextMessage) message;
                        String action = textMsg.getStringProperty("action");
                        String body   = textMsg.getText();
                        Destination replyTo = textMsg.getJMSReplyTo();
                        String correlationId = textMsg.getJMSCorrelationID();

                        System.out.println("Podsistem1 received action: " + action);

                        // Process the request
                        String response = service.process(action, body);

                        // Send reply
                        if (replyTo != null) {
                            MessageProducer producer = 
                                session.createProducer(replyTo);
                            TextMessage reply = 
                                session.createTextMessage(response);
                            reply.setJMSCorrelationID(correlationId);
                            producer.send(reply);
                            producer.close();
                        }
                    }
                } catch (JMSException e) {
                    System.err.println("Podsistem1 JMS error: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            System.err.println("Podsistem1: Failed to start listener: " 
                               + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (consumer   != null) consumer.close();
            if (session    != null) session.close();
            if (connection != null) connection.close();
            System.out.println("Podsistem1: JMS Listener stopped.");
        } catch (JMSException e) {
            System.err.println("Podsistem1: Error stopping listener: " 
                               + e.getMessage());
        }
    }

    @Override
    public void run() {
        start();
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            stop();
        }
    }
}
