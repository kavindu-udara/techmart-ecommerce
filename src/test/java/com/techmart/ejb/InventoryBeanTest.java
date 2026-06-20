package com.techmart.ejb;

import com.techmart.entity.Product;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ArquillianExtension.class)
public class InventoryBeanTest {

    // =======================================================================
    // HELPER BEAN: Guarantees EntityManager injection inside the container
    // =======================================================================
    @Stateless
    public static class DatabaseHelperBean {
        @PersistenceContext
        private EntityManager em;

        public void persistAndFlush(Object entity) {
            em.persist(entity);
            em.flush(); // Force write to DB immediately so other beans can see it
        }

        public <T> T find(Class<T> entityClass, Object primaryKey) {
            return em.find(entityClass, primaryKey);
        }

        public void clear() {
            em.clear(); // Clears the cache so we fetch fresh data from the DB
        }
    }

    // =======================================================================
    // DEPLOYMENT: Packages the test into a micro-WAR
    // =======================================================================
    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                // CRITICAL: We must include the DatabaseHelperBean in the deployment!
                .addClasses(Product.class, InventoryBean.class, DatabaseHelperBean.class, InventoryBeanTest.class)
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    // =======================================================================
    // INJECTIONS: We only use @EJB now, which is 100% reliable
    // =======================================================================
    @EJB
    private InventoryBean inventoryBean;

    @EJB
    private DatabaseHelperBean dbHelper;

    // =======================================================================
    // TEST CASES
    // =======================================================================

    @Test
    public void testDeductStockSuccess() {
        // 1. Setup: Create a product
        Product product = new Product();
        product.setName("Test Laptop");
        product.setPrice(new BigDecimal("1000.00"));
        product.setStockQuantity(10);

        // Use the helper bean to persist and flush to the actual database
        dbHelper.persistAndFlush(product);

        // 2. Execute: Deduct 2 units via the InventoryBean EJB
        boolean result = inventoryBean.deductStock(product.getId(), 2);

        // 3. Verify: Assert the transaction succeeded
        assertTrue(result, "Stock deduction should be successful");

        // Clear the persistence context to ensure we read fresh data from the DB
        dbHelper.clear();

        Product updatedProduct = dbHelper.find(Product.class, product.getId());
        assertNotNull(updatedProduct, "Product should exist in the database");
        assertEquals(8, updatedProduct.getStockQuantity(), "Stock should be reduced to 8");
    }

    @Test
    public void testDeductStockInsufficientQuantity() {
        Product product = new Product();
        product.setName("Test Mouse");
        product.setPrice(new BigDecimal("50.00"));
        product.setStockQuantity(5);

        dbHelper.persistAndFlush(product);

        // Attempt to deduct more than available
        boolean result = inventoryBean.deductStock(product.getId(), 10);

        assertFalse(result, "Stock deduction should fail due to insufficient quantity");

        dbHelper.clear();
        Product updatedProduct = dbHelper.find(Product.class, product.getId());
        assertNotNull(updatedProduct, "Product should exist in the database");
        assertEquals(5, updatedProduct.getStockQuantity(), "Stock should remain unchanged");
    }
}