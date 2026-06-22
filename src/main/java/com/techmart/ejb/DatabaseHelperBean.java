package com.techmart.ejb;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class DatabaseHelperBean {

    @PersistenceContext
    private EntityManager em;

    public void persistAndFlush(Object entity) {
        em.persist(entity);
        em.flush();
    }

    public <T> T find(Class<T> entityClass, Object primaryKey) {
        return em.find(entityClass, primaryKey);
    }

    public void clear() {
        em.clear();
    }
}