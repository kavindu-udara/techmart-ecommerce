package com.techmart.controller;

import com.techmart.ejb.ProductCacheBean;
import com.techmart.entity.Product;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class ProductController {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private ProductCacheBean productCacheBean;

    public List<Product> getAllProducts(){
        return productCacheBean.getAllProducts();
    }

}
