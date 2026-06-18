package com.techmart.ejb;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.PostActivate;
import jakarta.ejb.PrePassivate;
import jakarta.ejb.Remove;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ShoppingCartBean implements Serializable {
    private static final Logger logger = Logger.getLogger(ShoppingCartBean.class.getName());

    // Inner DTO
    public static class CartItem implements Serializable{
        private Long productId;
        private String productName;
        private BigDecimal unitPrice;
        private int quantity;

        public CartItem(Long productId, String productName, BigDecimal unitPrice, int quantity){
            this.productId = productId;
            this.productName = productName;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
        }

        public BigDecimal getSubtotal(){
            return unitPrice.multiply(new BigDecimal(quantity));
        }

        public Long getProductId() { return productId; }
        public String getProductName() { return productName; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public int getQuantity() { return quantity; }

    }

    private List<CartItem> items;

    @PostConstruct
    public void init(){
        logger.info("Shopping Cart created for user session.");
        this.items = new ArrayList<>();
    }

    @PreDestroy
    public void destroy(){
        logger.info("Shopping Cart destroyed. Session ended.");
        this.items = null;
    }

    // LIFECYCLE OPTIMIZATION: Passivation & Activation
    @PrePassivate
    public void onPassivate(){
        logger.info("Shopping Cart passivating (saving to disk) to save memory...");
    }

    @PostActivate
    public void onActivate(){
        logger.info("Shopping Cart activated (loaded from disk) for returning user.");
    }

//    BUSINESS LOGIC
    public void addItem(Long productId, String productName, BigDecimal unitPrice, int quantity){
//        check if item already in cart
        for (CartItem item : items) {
            if(item.getProductId().equals(productId)){
                items.remove(item);
                quantity += item.getQuantity();
                break;
            }
        }
        items.add(new CartItem(productId, productName, unitPrice, quantity));
    }

    public void removeItem(Long productId){
        items.removeIf(item -> item.getProductId().equals(productId));
    }

    public List<CartItem> getItems() {
        return items;
    }

    public BigDecimal getTotalAmount(){
        return items.stream().map(CartItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Remove
    public void checkout(){
        logger.info("Checkout initiated. Cart will be destroyed after processing.");
    }
}
