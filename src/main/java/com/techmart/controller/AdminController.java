package com.techmart.controller;

import com.techmart.dto.AdminDashboardResponse;
import com.techmart.dto.ProductRequest;
import com.techmart.ejb.ProductCacheBean;
import com.techmart.entity.Order;
import com.techmart.entity.Product;
import com.techmart.entity.User;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.util.List;

@Stateless
public class AdminController {
    @PersistenceContext
    private EntityManager em;

    @Inject
    private ProductCacheBean productCacheBean;

    public AdminDashboardResponse getDashboardStats() {
        long totalUsers = em.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
        long totalOrders = em.createQuery("SELECT COUNT(o) FROM Order o", Long.class).getSingleResult();
        long totalProducts = em.createQuery("SELECT COUNT(p) FROM Product p", Long.class).getSingleResult();

        BigDecimal totalRevenue = em.createQuery(
                        "SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = :status", BigDecimal.class)
                .setParameter("status", Order.OrderStatus.COMPLETED)
                .getSingleResult();

        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        long pendingOrders = em.createQuery(
                        "SELECT COUNT(o) FROM Order o WHERE o.status = :status", Long.class)
                .setParameter("status", Order.OrderStatus.PENDING)
                .getSingleResult();

        long completedOrders = em.createQuery(
                        "SELECT COUNT(o) FROM Order o WHERE o.status = :status", Long.class)
                .setParameter("status", Order.OrderStatus.COMPLETED)
                .getSingleResult();

        return new AdminDashboardResponse(totalUsers, totalOrders, totalProducts,
                totalRevenue, pendingOrders, completedOrders);
    }

    public List<User> getAllUsers() {
        return em.createQuery("SELECT u FROM User u ORDER BY u.createdAt DESC", User.class)
                .getResultList();
    }

    public List<Order> getAllOrders() {
        return em.createQuery(
                        "SELECT DISTINCT o FROM Order o " +
                                "LEFT JOIN FETCH o.items " +
                                "LEFT JOIN FETCH o.items.product " +
                                "ORDER BY o.createdAt DESC", Order.class)
                .getResultList();
    }

    public Product addProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());

        em.persist(product);
        productCacheBean.refreshCache();

        return product;
    }

    public Product updateProduct(Long productId, ProductRequest request) {
        Product product = em.find(Product.class, productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());

        em.merge(product);
        productCacheBean.refreshCache();

        return product;
    }

    public void deleteProduct(Long productId) {
        Product product = em.find(Product.class, productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }

        em.remove(product);
        productCacheBean.refreshCache();
    }
}
