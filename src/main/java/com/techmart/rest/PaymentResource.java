package com.techmart.rest;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.techmart.config.Secured;
import com.techmart.dto.PaymentIntentResponse;
import com.techmart.ejb.PaymentService;
import com.techmart.monitoring.Monitored;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Stateless
@Path("/payments")
@Monitored
public class PaymentResource {
    private static final Logger logger = Logger.getLogger(PaymentResource.class.getName());

    @Inject
    private PaymentService paymentService;

    @Inject
    @ConfigProperty(name = "stripe.webhook.secret")
    private String webhookSecret;

    @POST
    @Path("/create-intent")
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPaymentIntent(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("authenticatedUserId");
            if (userId == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Not authenticated\"}").build();
            }

            PaymentIntentResponse intent = paymentService.createPaymentIntent(userId);
            return Response.ok(intent).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        } catch (StripeException e) {
            logger.severe("Stripe error: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Payment service error\"}").build();
        } catch (Exception e) {
            logger.severe("Error creating payment intent: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Server error\"}").build();
        }
    }

    @POST
    @Path("/webhook")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response handleWebhook(String payload, @HeaderParam("Stripe-Signature") String sigHeader) {
        try {
            // Verify webhook signature
            Event event;
            try {
                event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            } catch (Exception e) {
                logger.warning("Invalid webhook signature: " + e.getMessage());
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Invalid signature\"}").build();
            }

            if ("payment_intent.succeeded".equals(event.getType())) {
                PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer().getObject().get();
                String paymentIntentId = intent.getId();
                Long userId = Long.parseLong(intent.getMetadata().get("userId"));

                logger.info("Payment succeeded: " + paymentIntentId + " for user " + userId);

                paymentService.handlePaymentSuccess(paymentIntentId, userId);
            }

            return Response.ok().build();

        } catch (Exception e) {
            logger.severe("Webhook error: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Webhook processing error\"}").build();
        }
    }

    @GET
    @Path("/public-key")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPublicKey() {
        Map<String, String> response = new HashMap<>();
        response.put("publicKey", paymentService.getStripePublicKey());
        return Response.ok(response).build();
    }
}
