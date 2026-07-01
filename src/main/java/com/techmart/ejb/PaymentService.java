package com.techmart.ejb;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.techmart.controller.CartController;
import com.techmart.controller.OrderController;
import com.techmart.dto.PaymentIntentResponse;
import com.techmart.entity.Cart;
import com.techmart.entity.CartItem;
import com.techmart.entity.Order;
import com.techmart.monitoring.Monitored;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.math.BigDecimal;
import java.util.logging.Logger;

@Stateless
@Monitored
public class PaymentService {
    private static final Logger logger = Logger.getLogger(PaymentService.class.getName());

    @Inject
    @ConfigProperty(name = "stripe.secret.key")
    private String stripeSecretKey;

    @Inject
    @ConfigProperty(name = "stripe.public.key")
    private String stripePublicKey;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private CartController cartController;

    @Inject
    private OrderController orderController;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
        logger.info("Stripe SDK initialized successfully");
    }

    public PaymentIntentResponse createPaymentIntent(Long userId) throws StripeException {

        Cart cart = cartController.getOrCreateCart(userId);

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        // Calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            BigDecimal subtotal = item.getProduct().getPrice()
                    .multiply(new BigDecimal(item.getQuantity()));
            totalAmount = totalAmount.add(subtotal);
        }

        // Convert to cents
        long amountInCents = totalAmount.multiply(new BigDecimal("100")).longValue();

        // Create PaymentIntent with Stripe
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("usd")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .putMetadata("userId", userId.toString())
                .build();

        PaymentIntent intent = PaymentIntent.create(params);

        logger.info("PaymentIntent created: " + intent.getId() + " for $" + totalAmount);

        return new PaymentIntentResponse(
                intent.getId(),
                intent.getClientSecret(),
                totalAmount,
                "usd",
                intent.getStatus()
        );
    }

    public void handlePaymentSuccess(String paymentIntentId, Long userId) {
        try {
            // Check if order already exists for this payment
            Order existingOrder = orderController.getOrderByPaymentIntentId(paymentIntentId);

            if (existingOrder != null) {
                logger.info("⚠️ Order already exists for payment: " + paymentIntentId);
                return;
            }

            // Get user's cart
            Cart cart = cartController.getCartEntity(userId);
            if (cart == null || cart.getItems().isEmpty()) {
                logger.warning("Cart is empty for user: " + userId);
                return;
            }

            // Create Order
            Order order = new Order();
            order.setUser(em.find(com.techmart.entity.User.class, userId));
            order.setStatus(Order.OrderStatus.PENDING);
            order.setPaymentIntentId(paymentIntentId);
            order.setPaymentStatus("PAID");

            // Add items from cart
            for (CartItem cartItem : cart.getItems()) {
                com.techmart.entity.OrderItem orderItem = new com.techmart.entity.OrderItem();
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setUnitPrice(cartItem.getProduct().getPrice());
                order.addItem(orderItem);
            }

            orderController.CreateOrder(order);

            logger.info("Order #" + order.getId() + " created from payment: " + paymentIntentId);

            // Clear cart
            cartController.clearCart(userId);

            // Trigger async processing (stock deduction, notifications)
            logger.info("Payment successful, order queued for processing");

        } catch (Exception e) {
            logger.severe("Error handling payment success: " + e.getMessage());
            throw e;
        }
    }

    public String getStripePublicKey() {
        return stripePublicKey;
    }
}
