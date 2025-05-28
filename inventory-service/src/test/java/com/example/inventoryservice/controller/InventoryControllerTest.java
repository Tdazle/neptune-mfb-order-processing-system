package com.example.inventoryservice.controller;

import com.example.inventoryservice.entity.Product;
import com.example.inventoryservice.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class InventoryControllerTest {

    private InventoryService inventoryService;
    private InventoryController inventoryController;
    private MockMvc mockMvc;

    /**
     * Sets up the test environment before each test.
     * Initializes a mock InventoryService, an InventoryController with the mocked service,
     * and a MockMvc instance for testing the controller.
     */
    @BeforeEach
    void setUp() {
        inventoryService = mock(InventoryService.class);
        inventoryController = new InventoryController(inventoryService);
        mockMvc = MockMvcBuilders.standaloneSetup(inventoryController).build();
    }

    /**
     * Test that the getAllProducts method returns a list of products.
     * Sets up two products with specified IDs, names, and stock quantities.
     * Mocks the inventoryService to return these products.
     * It Verifies that the response status is 200 OK and the response body contains
     * exactly these products.
     */
    @Test
    void testGetAllProductsReturnsProductList() {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Widget");
        product1.setStockQuantity(10);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Gadget");
        product2.setStockQuantity(5);

        List<Product> products = Arrays.asList(product1, product2);
        when(inventoryService.getAllProducts()).thenReturn(products);

        ResponseEntity<List<Product>> response = inventoryController.getAllProducts();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).containsExactlyElementsOf(products);
    }

    /**
     * Test that the getAllProducts method returns an empty list.
     * Mocks the inventoryService to return an empty list.
     * Verifies that the response status is 200 OK and the response body is an empty list.
     */
    @Test
    void testGetAllProductsReturnsEmptyList() {
        when(inventoryService.getAllProducts()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Product>> response = inventoryController.getAllProducts();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEmpty();
    }

    /**
     * Verifies that the {@code getAllProducts()} method returns a response with a
     * 200 OK status code, even if the list of products is empty.
     */
    @Test
    void testGetAllProductsReturnsHttpOk() {
        when(inventoryService.getAllProducts()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Product>> response = inventoryController.getAllProducts();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    /**
     * Verifies that the {@code getAllProducts()} method returns an empty JSON
     * array when the list of products is empty.
     * <p>
     * This test sends a GET request to {@code /inventory/products} and verifies
     * that the response status is 200 OK and the response body is a valid JSON
     * representation of an empty array.
     */
    @Test
    void testGetAllProductsReturnsValidJsonOnEmptyList() throws Exception {
        when(inventoryService.getAllProducts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/inventory/products")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    /**
     * Verifies that the products endpoint rejects unsupported HTTP methods.
     * <p>
     * This test sends POST, PUT, and DELETE requests to the products endpoint
     * and verifies that each request is rejected with a 405 Method Not Allowed
     * status code.
     */
    @Test
    void testProductsEndpointRejectsUnsupportedMethods() throws Exception {
        mockMvc.perform(post("/inventory/products"))
                .andExpect(status().isMethodNotAllowed());

        mockMvc.perform(put("/inventory/products"))
                .andExpect(status().isMethodNotAllowed());

        mockMvc.perform(delete("/inventory/products"))
                .andExpect(status().isMethodNotAllowed());
    }
}