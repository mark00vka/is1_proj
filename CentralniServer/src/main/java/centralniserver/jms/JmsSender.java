/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package centralniserver.jms;

import java.io.StringWriter;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.jms.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.StringReader;
import org.xml.sax.InputSource;

@ApplicationScoped
public class JmsSender {

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

    public String sendAndReceive(Queue requestQueue,
                                  Queue replyQueue,
                                  String xmlBody,
                                  String action) {
        try (Connection conn = connectionFactory.createConnection();
             Session session  = conn.createSession(false,
                                    Session.AUTO_ACKNOWLEDGE)) {

            // Build message
            TextMessage message = session.createTextMessage(xmlBody);
            message.setStringProperty("action", action);
            message.setJMSReplyTo(replyQueue);

            String correlationId = java.util.UUID.randomUUID().toString();
            message.setJMSCorrelationID(correlationId);

            // Send
            MessageProducer producer = session.createProducer(requestQueue);
            producer.send(message);

            // Wait for reply (10 second timeout)
            conn.start();
            String selector = "JMSCorrelationID = '" + correlationId + "'";
            MessageConsumer consumer = session.createConsumer(replyQueue,
                                                              selector);
            TextMessage reply = (TextMessage) consumer.receive(10000);

            if (reply != null) return reply.getText();
            return xmlError("Timeout - podsistem nije odgovorio");

        } catch (JMSException e) {
            return xmlError("JMS greska: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // Convenience methods per subsystem
    // ----------------------------------------------------------------
    public String sendToPodsistem1(String xmlBody, String action) {
        return sendAndReceive(podsistem1Queue, podsistem1ReplyQueue,
                              xmlBody, action);
    }

    public String sendToPodsistem2(String xmlBody, String action) {
        return sendAndReceive(podsistem2Queue, podsistem2ReplyQueue,
                              xmlBody, action);
    }

    public String sendToPodsistem3(String xmlBody, String action) {
        return sendAndReceive(podsistem3Queue, podsistem3ReplyQueue,
                              xmlBody, action);
    }

    public Document parse(String xml) throws Exception {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        DocumentBuilder b = f.newDocumentBuilder();
        return b.parse(new InputSource(new StringReader(xml)));
    }

    public String getText(Document doc, String tag) {
        NodeList list = doc.getElementsByTagName(tag);
        if (list.getLength() > 0) return list.item(0).getTextContent();
        return "";
    }

    public String toXml(Document doc) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter sw = new StringWriter();
        t.transform(new DOMSource(doc), new StreamResult(sw));
        return sw.toString();
    }

    private String xmlError(String msg) {
        return "<greska>" + msg + "</greska>";
    }
}