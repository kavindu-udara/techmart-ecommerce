package com.techmart.ejb;

import com.techmart.entity.Product;
import com.techmart.monitoring.Monitored;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Logger;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Monitored
public class ProductCacheBean {

    private static final Logger logger = Logger.getLogger(ProductCacheBean.class.getName());

    @PersistenceContext
    private EntityManager em;

    //    In memo cache
    private List<Product> productCache;

    @PostConstruct
    public void init() {
        logger.info("Initializing ProductCacheBean");
        loadCache();
        logger.info("ProductCacheBean initialized successfully with " + productCache.size() + " products.");
    }

    @PreDestroy
    public void cleanup() {
        logger.info("Destroying Product Cache and freeing memory...");
        productCache = null;
    }

    @Lock(LockType.READ)
    public Product getProductById(Long id) {
        return productCache.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }

    @Lock(LockType.WRITE)
    public void refreshCache() {
        logger.info("Refreshing Product Cache...");
        loadCache();
    }

    private void loadCache() {
        this.productCache = em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
    }
}
