package com.techmart.controller;

import com.techmart.entity.Order;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class OrderController {
    @PersistenceContext
    private EntityManager em;

    public List<Order> getOrdersByUserId(Long userId) {
        return em.createQuery(
                        "SELECT DISTINCT o FROM Order o " +
                                "LEFT JOIN FETCH o.items " +
                                "LEFT JOIN FETCH o.items.product " +
                                "WHERE o.user.id = :userId " +
                                "ORDER BY o.createdAt DESC", Order.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public Order getOrderByPaymentIntentId(String paymentIntentId) {
        return em.createQuery(
                        "SELECT o FROM Order o WHERE o.paymentIntentId = :paymentIntentId", Order.class)
                .setParameter("paymentIntentId", paymentIntentId)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public void CreateOrder(Order order){
        em.persist(order);
    }

}
