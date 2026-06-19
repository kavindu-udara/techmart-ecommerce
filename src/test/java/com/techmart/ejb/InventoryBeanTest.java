package com.techmart.ejb;

import com.techmart.entity.Product;
import jakarta.ejb.EJB;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ArquillianExtension.class)
public class InventoryBeanTest{
    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addClasses(Product.class, InventoryBean.class)
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @EJB
    private InventoryBean inventoryBean;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void testDeductStockSuccess(){
//        create a product with a 10 units in the test DB
        Product product = new Product();
        product.setName("Test Laptop");
        product.setPrice(new BigDecimal("1000.00"));
        product.setStockQuantity(10);
        em.persist(product);

//        deduct 2 units
        boolean result = inventoryBean.deductStock(product.getId(), 2);

//        verify: Assert the transaction succeeded and stock is updated
        assertTrue(result, "Stock deduction should be successful");
        Product updatedProduct = em.find(Product.class, product.getId());
        assertEquals(8, updatedProduct.getStockQuantity(), "Stock should be reduced to 8");
    }

    @Test
    public void testDeductStockInsufficientQuantity() {
        Product product = new Product();
        product.setName("Test Mouse");
        product.setPrice(new BigDecimal("50.00"));
        product.setStockQuantity(5);
        em.persist(product);

        // attempt to deduct more than available
        boolean result = inventoryBean.deductStock(product.getId(), 10);

        assertFalse(result, "Stock deduction should fail due to insufficient quantity");
        Product updatedProduct = em.find(Product.class, product.getId());
        assertEquals(5, updatedProduct.getStockQuantity(), "Stock should remain unchanged");
    }

}