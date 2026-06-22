package com.techmart.ejb;

import com.techmart.entity.Product;
import jakarta.ejb.EJB;
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

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                // We include the top-level DatabaseHelperBean class here
                .addClasses(Product.class, InventoryBean.class, DatabaseHelperBean.class, InventoryBeanTest.class)
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @EJB
    private InventoryBean inventoryBean;

    @EJB
    private DatabaseHelperBean dbHelper;

    @Test
    public void testDeductStockSuccess() {
        Product product = new Product();
        product.setName("Test Laptop");
        product.setPrice(new BigDecimal("1000.00"));
        product.setStockQuantity(10);

        dbHelper.persistAndFlush(product);
        boolean result = inventoryBean.deductStock(product.getId(), 2);

        assertTrue(result, "Stock deduction should be successful");
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
        boolean result = inventoryBean.deductStock(product.getId(), 10);

        assertFalse(result, "Stock deduction should fail due to insufficient quantity");
        dbHelper.clear();

        Product updatedProduct = dbHelper.find(Product.class, product.getId());
        assertNotNull(updatedProduct, "Product should exist in the database");
        assertEquals(5, updatedProduct.getStockQuantity(), "Stock should remain unchanged");
    }
}