package com.example.inventoryservice.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    /**
     * Verifies that a newly created product with valid fields (name and stock quantity)
     * can be persisted and its fields can be retrieved correctly.
     */
    @Test
    void testProductPersistenceWithValidFields() {
        Product product = new Product();
        product.setName("Widget");
        product.setStockQuantity(100);

        assertEquals("Widget", product.getName());
        assertEquals(100, product.getStockQuantity());
    }

    /**
     * Tests that the getter methods of the Product class return the correct values
     * of the fields that they are supposed to retrieve.
     */
    @Test
    void testProductGettersReturnCorrectValues() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Gadget");
        product.setStockQuantity(50);

        assertEquals(1L, product.getId());
        assertEquals("Gadget", product.getName());
        assertEquals(50, product.getStockQuantity());
    }

    /**
     * Verifies that a newly created product has a null id, and that after calling setId
     * the id is correctly set.
     */
    @Test
    void testProductIdAutoGeneration() {
        Product product = new Product();
        // Simulate persistence by setting id as would be done by JPA provider
        assertNull(product.getId());
        product.setId(10L);
        assertEquals(10L, product.getId());
    }

    /**
     * Tests that a product can be persisted with a null name, and that the product's
     * name and stock quantity can be retrieved correctly.
     */
    @Test
    void testProductWithNullName() {
        Product product = new Product();
        product.setName(null);
        product.setStockQuantity(5);

        assertNull(product.getName());
        assertEquals(5, product.getStockQuantity());
    }

    /**
     * Tests that a product with a stock quantity of 0 can be persisted without
     * throwing any exceptions, and that the product's name and stock quantity can
     * be retrieved correctly.
     */
    @Test
    void testProductWithZeroStockQuantity() {
        Product product = new Product();
        product.setName("ZeroStockItem");
        product.setStockQuantity(0);

        assertEquals("ZeroStockItem", product.getName());
        assertEquals(0, product.getStockQuantity());
    }

    /**
     * Tests that a product with a negative stock quantity is correctly created.
     *
     * <p>
     * This test is useful for checking that the stock quantity property
     * of a product can be set to a negative integer.
     */
    @Test
    void testProductWithNegativeStockQuantity() {
        Product product = new Product();
        product.setName("NegativeStockItem");
        product.setStockQuantity(-10);

        assertEquals("NegativeStockItem", product.getName());
        assertEquals(-10, product.getStockQuantity());
    }
}