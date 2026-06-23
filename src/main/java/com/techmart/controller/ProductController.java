package com.techmart.controller;

import com.techmart.ejb.ProductCacheBean;
import com.techmart.entity.Product;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Stateless
public class ProductController {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private ProductCacheBean productCacheBean;

    public List<Product> getAllProducts() {
        return productCacheBean.getAllProducts();
    }

    public Product getProductById(Long id) {
        Product product = productCacheBean.getProductById(id);

        // Fallback to database if not in cache
        if (product == null) {
            product = em.find(Product.class, id);
        }

        return product;
    }

    public List<Product> searchProducts(String query){
        return em.createQuery(
                        "SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(:query)",
                        Product.class)
                .setParameter("query", "%" + query.trim() + "%")
                .getResultList();
    }

    public Long getProductsCount(){
        return em.createQuery("SELECT COUNT(p) FROM Product p", Long.class)
                .getSingleResult();
    }

    public List<Product> getPaginatedProducts(int offset, int size){

        TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p ORDER BY p.id", Product.class);
        query.setFirstResult(offset);
        query.setMaxResults(size);

        return query.getResultList();
    }

}
