package com.techmart.controller;

import com.techmart.dto.AddToCartRequest;
import com.techmart.dto.CartResponse;
import com.techmart.ejb.ProductCacheBean;
import com.techmart.entity.Cart;
import com.techmart.entity.CartItem;
import com.techmart.entity.Product;
import com.techmart.entity.User;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class CartController {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private ProductCacheBean productCacheBean;

    public Cart getOrCreateCart(Long userId) {
        Cart cart = em.createQuery("SELECT c FROM Cart c WHERE c.user.id = :userId", Cart.class)
                .setParameter("userId", userId)
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (cart == null) {
            User user = em.find(User.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }
            cart = new Cart();
            cart.setUser(user);
            em.persist(cart);
            em.flush();
        }

        return cart;
    }

    /**
     * Add item to cart (or update quantity if already exists)
     */
    public Cart addItem(Long userId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId);

        // Check if product exists
        Product product = productCacheBean.getProductById(request.getProductId());
        if (product == null) {
            product = em.find(Product.class, request.getProductId());
        }
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }

        // Check stock availability
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
        }

        // Check if item already in cart
        CartItem existingItem = em.createQuery(
                        "SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.product.id = :productId",
                        CartItem.class)
                .setParameter("cartId", cart.getId())
                .setParameter("productId", request.getProductId())
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Update quantity
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
        } else {
            // Add new item
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            cart.addItem(newItem);
        }

        em.merge(cart);
        return cart;
    }

    /**
     * Update item quantity
     */
    public Cart updateItemQuantity(Long userId, Long itemId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = em.find(CartItem.class, itemId);
        if (item == null || !item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item not found");
        }

        // Check stock
        Product product = item.getProduct();
        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        if (quantity <= 0) {
            // Remove item if quantity is 0 or negative
            cart.removeItem(item);
            em.remove(item);
        } else {
            item.setQuantity(quantity);
            em.merge(item);
        }

        return cart;
    }

    /**
     * Remove item from cart
     */
    public Cart removeItem(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = em.find(CartItem.class, itemId);
        if (item == null || !item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item not found");
        }

        cart.removeItem(item);
        em.remove(item);

        return cart;
    }

    /**
     * Clear entire cart
     */
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        em.merge(cart);
    }

    /**
     * Get cart with calculated totals
     */
    public CartResponse getCartResponse(Long userId) {
        Cart cart = getOrCreateCart(userId);

        List<CartResponse.CartItemResponse> itemResponses = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalItems = 0;

        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            BigDecimal subtotal = product.getPrice().multiply(new BigDecimal(item.getQuantity()));
            totalAmount = totalAmount.add(subtotal);
            totalItems += item.getQuantity();

            CartResponse.CartItemResponse itemResponse = new CartResponse.CartItemResponse(
                    item.getId(),
                    product.getId(),
                    product.getName(),
                    product.getImageUrl(),
                    product.getPrice(),
                    item.getQuantity(),
                    subtotal
            );
            itemResponses.add(itemResponse);
        }

        return new CartResponse(cart.getId(), itemResponses, totalAmount, totalItems);
    }

    public Cart getCartEntity(Long userId) {
        return em.createQuery(
                        "SELECT DISTINCT c FROM Cart c " +
                                "LEFT JOIN FETCH c.items " +
                                "LEFT JOIN FETCH c.items.product " +
                                "WHERE c.user.id = :userId", Cart.class)
                .setParameter("userId", userId)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
}
