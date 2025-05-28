package com.example.inventoryservice.entity;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import jakarta.persistence.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    /**
     * Verifies that a valid product can be persisted and retrieved with all fields intact.
     */
    @Test
    void testProductPersistenceWithValidFields() {
        Product product = new Product();
        product.setName("Widget");
        product.setStockQuantity(100);

        Product saved = entityManager.persistFlushFind(product);

        assertNotNull(saved.getId());
        assertEquals("Widget", saved.getName());
        assertEquals(100, saved.getStockQuantity());
    }

    /**
     * Verifies that a persisted product can be retrieved with all of its fields intact,
     * demonstrating the integrity of the product entity's data.
     */
    @Test
    void testProductRetrievalIntegrity() {
        Product product = new Product();
        product.setName("Gadget");
        product.setStockQuantity(50);

        entityManager.persistAndFlush(product);

        Product found = entityManager.find(Product.class, product.getId());

        assertNotNull(found);
        assertEquals(product.getId(), found.getId());
        assertEquals("Gadget", found.getName());
        assertEquals(50, found.getStockQuantity());
    }

    /**
     * Verifies that the product entity's getters and setters work as expected.
     */
    @Test
    void testProductGettersAndSetters() {
        Product product = new Product();
        product.setId(42L);
        product.setName("TestProduct");
        product.setStockQuantity(10);

        assertEquals(42L, product.getId());
        assertEquals("TestProduct", product.getName());
        assertEquals(10, product.getStockQuantity());
    }

    /**
     * Verifies that attempting to persist a product with a null name results in a {@link PersistenceException}.
     */
    @Test
    void testProductPersistenceWithNullName() {
        Product product = new Product();
        product.setName(null);
        product.setStockQuantity(5);

        assertThrows(PersistenceException.class, () -> entityManager.persistAndFlush(product));
    }

    /**
     * Verifies that a product with a negative stock quantity can be persisted, and that its
     * negative stock quantity is retained after persistence.
     */
    @Test
    void testProductPersistenceWithNegativeStockQuantity() {
        Product product = new Product();
        product.setName("NegativeStock");
        product.setStockQuantity(-10);

        Product saved = entityManager.persistFlushFind(product);

        assertNotNull(saved.getId());
        assertEquals("NegativeStock", saved.getName());
        assertEquals(-10, saved.getStockQuantity());
    }

    /**
     * Verifies that a product with a manually-set ID can be persisted, and that the database assigns a new ID.
     * This test is meant to verify that the {@code @GeneratedValue} annotation on the ID field is working correctly.
     */
    @Test
    void testProductPersistenceWithManualId() {
        Product product = new Product();
        product.setId(999L);
        product.setName("ManualIdProduct");
        product.setStockQuantity(20);

        Product saved = entityManager.persistFlushFind(product);

        assertNotEquals(999L, saved.getId());
        assertEquals("ManualIdProduct", saved.getName());
        assertEquals(20, saved.getStockQuantity());
    }
}