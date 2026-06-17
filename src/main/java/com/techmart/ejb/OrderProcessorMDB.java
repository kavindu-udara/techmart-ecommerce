package com.techmart.ejb;

import com.techmart.entity.Order;
import com.techmart.entity.OrderItem;
import com.techmart.monitoring.Monitored;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.concurrent.Future;
import java.util.logging.Logger;

@MessageDriven(
        name = "OrderProcessorMDB",
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/OrderQueue"),
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
                @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),

//                Limits the number of concurrent threads processing messages
//                Prevents database connection pool exhaustion during peak loads
                @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "15"),

                // Limits how many messages a single MDB instance processes before being returned to the pool.
                // Optimizes memory and resource utilization patterns.
                @ActivationConfigProperty(propertyName = "maxMessagesPerSession", propertyValue = "5"),
        }
)
@Monitored
public class OrderProcessorMDB implements MessageListener {
    private static final Logger logger = Logger.getLogger(OrderProcessorMDB.class.getName());

    @PersistenceContext
    private EntityManager em;

    @EJB
    private InventoryBean inventoryBean;

    @EJB
    private NotificationService notificationService;

    @Resource
    private MessageDrivenContext mdbContext;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String payload = textMessage.getText();

                if (payload.startsWith("PROCESS_ORDER:")) {
                    Long orderId = Long.parseLong(payload.substring("PROCESS_ORDER:".length()));
                    processOrder(orderId);
                }
            }
        } catch (JMSException e) {
            logger.severe("JMS Exception while processing message: " + e.getMessage());
            mdbContext.setRollbackOnly();
        } catch (Exception e) {
            logger.severe("Unexpected error processing order message: " + e.getMessage());
            mdbContext.setRollbackOnly(); // Triggers JMS redelivery
        }
    }

    private void processOrder(Long orderId) {
        logger.info("MDB started processing Order #" + orderId);

        Order order = em.find(Order.class, orderId);
        if (order == null) {
            logger.warning("Order #" + orderId + " not found in DB. Discarding message.");
            return;
        }

        try {
            // Update status to PROCESSING
            order.setStatus(Order.OrderStatus.PROCESSING);
            em.merge(order);

            boolean stockSuccess = true;
            for (OrderItem item : order.getItems()) {
                boolean deducted = inventoryBean.deductStock(item.getProduct().getId(), item.getQuantity());
                if (!deducted) {
                    stockSuccess = false;
                    break;
                }
            }

            if (stockSuccess) {
                order.setStatus(Order.OrderStatus.COMPLETED);
                em.merge(order);
                logger.info("Order #" + orderId + " successfully completed and stock deducted.");

                Future<String> notificationResult = notificationService.sendOrderConfirmation(orderId, order.getUser().getEmail());
            } else {
                order.setStatus(Order.OrderStatus.FAILED);
                em.merge(order);
                logger.warning("Order #" + orderId + " failed due to insufficient stock.");
            }

        } catch (Exception e) {
            logger.severe("Error processing Order #" + orderId + ": " + e.getMessage());
            order.setStatus(Order.OrderStatus.FAILED);
            em.merge(order);
            throw e;
        }
    }
}
