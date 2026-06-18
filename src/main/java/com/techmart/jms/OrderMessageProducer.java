package com.techmart.jms;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;
import java.util.logging.Logger;

@Stateless
public class OrderMessageProducer {

    private static final Logger logger = Logger.getLogger(OrderMessageProducer.class.getName());

    @Inject
    private JMSContext context;

    @jakarta.annotation.Resource(lookup = "java:/jms/queue/OrderQueue")
    private Queue orderQueue;

    public void sendOrderForProcessing(Long orderId) {
        try {
            String messageText = "PROCESS_ORDER:" + orderId;
            context.createProducer().send(orderQueue, messageText);
            logger.info("Successfully dispatched Order #" + orderId + " to JMS Queue.");
        } catch (Exception e) {
            logger.severe("CRITICAL: Failed to send Order #" + orderId + " to JMS Queue. Error: " + e.getMessage());
            // In a real system, we would implement a Dead Letter Queue (DLQ) or fallback mechanism here.
        }
    }
}
