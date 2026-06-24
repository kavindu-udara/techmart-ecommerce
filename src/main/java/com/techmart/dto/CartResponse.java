package com.techmart.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartResponse {
    private Long cartId;
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;
    private int totalItems;

    public CartResponse() {
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public CartResponse(Long cartId, List<CartItemResponse> items, BigDecimal totalAmount, int totalItems) {
        this.cartId = cartId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.totalItems = totalItems;
    }

    public static class CartItemResponse {
        private Long itemId;
        private Long productId;
        private String productName;
        private String productImage;
        private BigDecimal unitPrice;
        private Integer quantity;
        private BigDecimal subtotal;

        public CartItemResponse() {
        }

        public CartItemResponse(Long itemId, Long productId, String productName, String productImage,
                                BigDecimal unitPrice, Integer quantity, BigDecimal subtotal) {
            this.itemId = itemId;
            this.productId = productId;
            this.productName = productName;
            this.productImage = productImage;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
            this.subtotal = subtotal;
        }

        public Long getItemId() {
            return itemId;
        }

        public void setItemId(Long itemId) {
            this.itemId = itemId;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductImage() {
            return productImage;
        }

        public void setProductImage(String productImage) {
            this.productImage = productImage;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
        }
    }
}
