package com.example.inventoryservice.service;

import com.example.inventoryservice.entity.Product;
import com.example.inventoryservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

    private ProductRepository productRepository;
    private InventoryService inventoryService;

    /**
     * Sets up the test environment before each test.
     *
     * <p>
     * This method creates a mock instance of the {@link ProductRepository} and
     * initializes the {@link InventoryService} with it.
     */
    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        inventoryService = new InventoryService(productRepository);
    }

    /**
     * Test that the checkStock method returns true when the product exists
     * and there is enough stock quantity to fulfill the requested amount.
     *
     * <p>
     * This test sets up a product with a given name and stock quantity, and uses
     * Mockito to mock the productRepository's findByName method to return the product.
     * It then calls the checkStock method with a quantity less than the stock quantity
     * and verifies that the method returns true, indicating sufficient stock availability.
     */
    @Test
    void testCheckStock_ProductExistsAndSufficientQuantity() {
        Product product = new Product();
        product.setName("Widget");
        product.setStockQuantity(10);

        when(productRepository.findByName("Widget")).thenReturn(product);

        boolean result = inventoryService.checkStock("Widget", 5);

        assertTrue(result);
        verify(productRepository).findByName("Widget");
    }

    /**
     * Tests that the updateStock method successfully reduces the stock quantity of a product
     * when the product exists, and there are enough stocks to fulfill the update.
     *
     * <p>
     * This test sets up a product with a given name and stock quantity, and uses Mockito to
     * mock the productRepository's findByName and save methods to return the product.
     * It then calls the updateStock method and verifies that the method returns true,
     * and that the given amount has successfully reduced the product's stock quantity.
     */
    @Test
    void testUpdateStock_ProductExistsAndSufficientStock() {
        Product product = new Product();
        product.setName("Gadget");
        product.setStockQuantity(20);

        when(productRepository.findByName("Gadget")).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        boolean result = inventoryService.updateStock("Gadget", 5);

        assertTrue(result);
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertEquals(15, captor.getValue().getStockQuantity());
    }

    /**
     * Test that the getAllProducts method returns all products in the inventory.
     *
     * <p>
     * This test sets up two products with specified names and stock quantities.
     * It uses Mockito to mock the productRepository's findAll method to return
     * these products.
     * It then calls the getAllProducts method and verifies that the response
     * contains both products.
     */
    @Test
    void testGetAllProducts_ReturnsAllProducts() {
        Product product1 = new Product();
        product1.setName("Item1");
        product1.setStockQuantity(5);

        Product product2 = new Product();
        product2.setName("Item2");
        product2.setStockQuantity(8);

        List<Product> products = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = inventoryService.getAllProducts();

        assertEquals(2, result.size());
        assertTrue(result.contains(product1));
        assertTrue(result.contains(product2));
        verify(productRepository).findAll();
    }

    /**
     * Test that the checkStock method returns false when checking the stock for a
     * non-existent product. Verifies that the findByName method of the productRepository
     * is called with the correct product name.
     */
    @Test
    void testCheckStock_ProductDoesNotExist() {
        when(productRepository.findByName("NonExistent")).thenReturn(null);

        boolean result = inventoryService.checkStock("NonExistent", 1);

        assertFalse(result);
        verify(productRepository).findByName("NonExistent");
    }

    /**
     * Test that updateStock returns false when the requested quantity is greater than
     * the current stock quantity of the given product. Verifies that the repository's
     * safe method is not called in this case.
     */
    @Test
    void testUpdateStock_InsufficientStock() {
        Product product = new Product();
        product.setName("Limited");
        product.setStockQuantity(2);

        when(productRepository.findByName("Limited")).thenReturn(product);

        boolean result = inventoryService.updateStock("Limited", 5);

        assertFalse(result);
        verify(productRepository, never()).save(any(Product.class));
    }

    /**
     * Test that getProductByName returns null when the product does not exist.
     * Verifies that the repository's findByName method is called with the correct product name.
     */
    @Test
    void testGetProductByName_ProductDoesNotExist() {
        when(productRepository.findByName("Ghost")).thenReturn(null);

        Product result = inventoryService.getProductByName("Ghost");

        assertNull(result);
        verify(productRepository).findByName("Ghost");
    }
}