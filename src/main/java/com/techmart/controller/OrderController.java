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

}
