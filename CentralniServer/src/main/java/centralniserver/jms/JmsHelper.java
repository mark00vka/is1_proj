/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package centralniserver.jms;

import javax.annotation.Resource;
import javax.jms.*;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JmsHelper {

    @Resource(lookup = "jms/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/Podsistem1Queue")
    private Queue podsistem1Queue;

    @Resource(lookup = "jms/Podsistem1ReplyQueue")
    private Queue podsistem1ReplyQueue;

    @Resource(lookup = "jms/Podsistem2Queue")
    private Queue podsistem2Queue;

    @Resource(lookup = "jms/Podsistem2ReplyQueue")
    private Queue podsistem2ReplyQueue;

    @Resource(lookup = "jms/Podsistem3Queue")
    private Queue podsistem3Queue;

    @Resource(lookup = "jms/Podsistem3ReplyQueue")
    private Queue podsistem3ReplyQueue;

    /**
     * Sends a JSON message to the given queue and waits for a reply.
     * @param queue      destination queue
     * @param replyQueue queue to receive response on
     * @param jsonBody   the JSON string to send
     * @param action     the action name (e.g. "GET_ALL_GRADOVI")
     * @return JSON string response from the subsystem
     */
    public String sendAndReceive(Queue queue, Queue replyQueue,
                                  String jsonBody, String action) {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession(false,
                                    Session.AUTO_ACKNOWLEDGE)) {

            // Create message
            TextMessage message = session.createTextMessage(jsonBody);
            message.setStringProperty("action", action);
            message.setJMSReplyTo(replyQueue);

            // Unique correlation ID so we match the right reply
            String correlationId = java.util.UUID.randomUUID().toString();
            message.setJMSCorrelationID(correlationId);

            // Send to subsystem
            MessageProducer producer = session.createProducer(queue);
            producer.send(message);

            // Wait for reply (timeout 10 seconds)
            connection.start();
            String selector = "JMSCorrelationID = '" + correlationId + "'";
            MessageConsumer consumer = session.createConsumer(replyQueue, selector);
            TextMessage reply = (TextMessage) consumer.receive(10000);

            if (reply != null) {
                return reply.getText();
            } else {
                return "{\"error\":\"Timeout - podsistem nije odgovorio\"}";
            }

        } catch (JMSException e) {
            e.printStackTrace();
            return "{\"error\":\"JMS greška: " + e.getMessage() + "\"}";
        }
    }

    // Convenience methods per subsystem
    public String sendToPodsistem1(String jsonBody, String action) {
        return sendAndReceive(podsistem1Queue, podsistem1ReplyQueue,
                              jsonBody, action);
    }

    public String sendToPodsistem2(String jsonBody, String action) {
        return sendAndReceive(podsistem2Queue, podsistem2ReplyQueue,
                              jsonBody, action);
    }

    public String sendToPodsistem3(String jsonBody, String action) {
        return sendAndReceive(podsistem3Queue, podsistem3ReplyQueue,
                              jsonBody, action);
    }
}
