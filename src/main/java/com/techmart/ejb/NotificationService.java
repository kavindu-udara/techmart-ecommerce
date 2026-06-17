package com.techmart.ejb;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import java.util.concurrent.Future;
import java.util.logging.Logger;

@Stateless
public class NotificationService {
    private static final Logger logger = Logger.getLogger(NotificationService.class.getName());

    @Asynchronous
    public Future<String> sendOrderConfirmation(Long orderId, String customerEmail) {
        try {

            logger.info("Starting async notification for Order #" + orderId + " to " + customerEmail);

            // Simulate external API call delay (e.g., SendGrid, AWS SES)
            Thread.sleep(3000);

            // Simulate a random failure to demonstrate error handling
            if (Math.random() < 0.1) {
                throw new RuntimeException("External Email API Timeout");
            }

            logger.info("Successfully sent notification for Order #" + orderId);
            // Return success result
            return new AsyncResult<>("SUCCESS: Email sent to " + customerEmail);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new AsyncResult<>("FAILED: Thread interrupted");
        } catch (Exception e) {
            logger.severe("Failed to send notification for Order #" + orderId + ": " + e.getMessage());
            return new AsyncResult<>("FAILED: " + e.getMessage());
        }
    }
}
