package com.techmart.ejb;

import com.techmart.entity.Product;
import com.techmart.monitoring.Monitored;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import java.util.logging.Logger;

@Stateless
@Monitored
public class InventoryBean {

    private static final Logger logger = Logger.getLogger(InventoryBean.class.getName());
    private static final int MAX_RETRIES = 3;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private ProductCacheBean productCacheBean;

    public boolean deductStock(Long productId, int quantity) {
        int attempts = 0;

        while (attempts < MAX_RETRIES) {
            try {
                Product product = em.find(Product.class, productId);

                if (product == null) {
                    logger.warning("Product not found with ID: " + productId);
                    return false;
                }

                if (product.getStockQuantity() < quantity) {
                    logger.warning("Insufficient stock for product ID: " + productId);
                    return false;
                }

                product.setStockQuantity(product.getStockQuantity() - quantity);
                em.flush(); // force the DB to update to trigger the version

                logger.info("Deducted " + quantity + " units from product ID: " + productId);

//                refresh cache
                productCacheBean.refreshCache();

                return true;

            } catch (OptimisticLockException ex) {
                attempts++;
                logger.warning("Optimistic Lock Exception on product : ." + productId + ".. Retrying... Attempt: " + attempts);
            }
        }

        logger.severe("Failed to deduct stock after " + MAX_RETRIES + " retries due to high concurrency.");
        return false;
    }

    public void addStock(Long productId, int quantity) {
        Product product = em.find(Product.class, productId);
        if (product != null) {
            product.setStockQuantity(product.getStockQuantity() + quantity);
            productCacheBean.refreshCache();
        }
    }

}
