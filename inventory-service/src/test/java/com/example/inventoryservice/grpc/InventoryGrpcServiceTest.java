package com.example.inventoryservice.grpc;

import com.example.inventoryservice.entity.Product;
import com.example.inventoryservice.service.InventoryService;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class InventoryGrpcServiceTest {

    private InventoryService inventoryService;
    private InventoryGrpcService inventoryGrpcService;
    private StreamObserver<StockResponse> responseObserver;

    @BeforeEach
    void setUp() {
        inventoryService = mock(InventoryService.class);
        inventoryGrpcService = new InventoryGrpcService(inventoryService);
        responseObserver = mock(StreamObserver.class);
    }

    /**
     * Test that a call to checkStock with a request quantity of 5
     * for a product with a stock quantity of 10 results in a response
     * with available = true, stockQuantity = 10, and message = "Stock available".
     */
    @Test
    void testCheckStock_SufficientStock_ReturnsAvailable() {
        String productName = "Widget";
        int requestQty = 5;
        Product product = new Product();
        product.setName(productName);
        product.setStockQuantity(10);

        when(inventoryService.checkStock(productName, requestQty)).thenReturn(true);
        when(inventoryService.getProductByName(productName)).thenReturn(product);

        StockRequest request = StockRequest.newBuilder()
                .setProduct(productName)
                .setQuantity(requestQty)
                .build();

        ArgumentCaptor<StockResponse> captor = ArgumentCaptor.forClass(StockResponse.class);

        inventoryGrpcService.checkStock(request, responseObserver);

        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();

        StockResponse response = captor.getValue();
        assertTrue(response.getAvailable());
        assertEquals(10, response.getStockQuantity());
        assertEquals("Stock available", response.getMessage());
    }

    /**
     * Test that a call to updateStock with a request quantity of 3
     * for a product with a stock quantity of 7 results in a response
     * with available = true, stockQuantity = 7, and message = "Stock updated successfully".
     */
    @Test
    void testUpdateStock_SufficientStock_UpdatesSuccessfully() {
        String productName = "Widget";
        int requestQty = 3;
        Product product = new Product();
        product.setName(productName);
        product.setStockQuantity(7);

        when(inventoryService.updateStock(productName, requestQty)).thenReturn(true);
        when(inventoryService.getProductByName(productName)).thenReturn(product);

        UpdateStockRequest request = UpdateStockRequest.newBuilder()
                .setProduct(productName)
                .setQuantity(requestQty)
                .build();

        ArgumentCaptor<StockResponse> captor = ArgumentCaptor.forClass(StockResponse.class);

        inventoryGrpcService.updateStock(request, responseObserver);

        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();

        StockResponse response = captor.getValue();
        assertTrue(response.getAvailable());
        assertEquals(7, response.getStockQuantity());
        assertEquals("Stock updated successfully", response.getMessage());
    }

    /**
     * Test that a call to updateStock with a request quantity of 2
     * for a product with a stock quantity of 8 results in a response
     * with available = true, stockQuantity = 8, and message = "Stock updated successfully".
     * This test is like {@link #testUpdateStock_SufficientStock_UpdatesSuccessfully()}, but
     * with a different quantity.
     */
    @Test
    void testUpdateStock_ResponseReflectsNewQuantityAndMessage() {
        String productName = "Gadget";
        int requestQty = 2;
        Product product = new Product();
        product.setName(productName);
        product.setStockQuantity(8);

        when(inventoryService.updateStock(productName, requestQty)).thenReturn(true);
        when(inventoryService.getProductByName(productName)).thenReturn(product);

        UpdateStockRequest request = UpdateStockRequest.newBuilder()
                .setProduct(productName)
                .setQuantity(requestQty)
                .build();

        ArgumentCaptor<StockResponse> captor = ArgumentCaptor.forClass(StockResponse.class);

        inventoryGrpcService.updateStock(request, responseObserver);

        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();

        StockResponse response = captor.getValue();
        assertTrue(response.getAvailable());
        assertEquals(8, response.getStockQuantity());
        assertEquals("Stock updated successfully", response.getMessage());
    }

    /**
     * Test that a call to checkStock with a request quantity of 1
     * for a non-existent product results in a response
     * with available = false, stockQuantity = 0, and message = "Insufficient stock".
     */
    @Test
    void testCheckStock_NonExistentProduct_ReturnsUnavailable() {
        String productName = "NonExistent";
        int requestQty = 1;

        when(inventoryService.checkStock(productName, requestQty)).thenReturn(false);
        when(inventoryService.getProductByName(productName)).thenReturn(null);

        StockRequest request = StockRequest.newBuilder()
                .setProduct(productName)
                .setQuantity(requestQty)
                .build();

        ArgumentCaptor<StockResponse> captor = ArgumentCaptor.forClass(StockResponse.class);

        inventoryGrpcService.checkStock(request, responseObserver);

        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();

        StockResponse response = captor.getValue();
        assertFalse(response.getAvailable());
        assertEquals(0, response.getStockQuantity());
        assertEquals("Insufficient stock", response.getMessage());
    }

    /**
     * Test that a call to updateStock with a request quantity that exceeds the current stock of
     * the given product results in a response with available = false, stockQuantity = 10, and
     * message = "Failed to update stock".
     */
    @Test
    void testUpdateStock_InsufficientStock_ReturnsFailure() {
        String productName = "Widget";
        int requestQty = 100;
        Product product = new Product();
        product.setName(productName);
        product.setStockQuantity(10);

        when(inventoryService.updateStock(productName, requestQty)).thenReturn(false);
        when(inventoryService.getProductByName(productName)).thenReturn(product);

        UpdateStockRequest request = UpdateStockRequest.newBuilder()
                .setProduct(productName)
                .setQuantity(requestQty)
                .build();

        ArgumentCaptor<StockResponse> captor = ArgumentCaptor.forClass(StockResponse.class);

        inventoryGrpcService.updateStock(request, responseObserver);

        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();

        StockResponse response = captor.getValue();
        assertFalse(response.getAvailable());
        assertEquals(10, response.getStockQuantity());
        assertEquals("Failed to update stock", response.getMessage());
    }

    /**
     * Test that a call to checkStock or updateStock with a null or empty product name is handled
     * gracefully and results in a response with available = false, stockQuantity = 0, and message =
     * "Insufficient stock" or "Failed to update stock".
     */
    @Test
    void testCheckAndUpdateStock_NullOrEmptyProductName_HandledGracefully() {
        // Test with null product name
        String nullProductName = null;
        int requestQty = 5;

        when(inventoryService.checkStock(nullProductName, requestQty)).thenReturn(false);
        when(inventoryService.getProductByName(nullProductName)).thenReturn(null);
        when(inventoryService.updateStock(nullProductName, requestQty)).thenReturn(false);

        StockRequest checkRequestNull = StockRequest.newBuilder()
                .setProduct("")
                .setQuantity(requestQty)
                .build();

        UpdateStockRequest updateRequestNull = UpdateStockRequest.newBuilder()
                .setProduct("")
                .setQuantity(requestQty)
                .build();

        ArgumentCaptor<StockResponse> checkCaptor = ArgumentCaptor.forClass(StockResponse.class);
        ArgumentCaptor<StockResponse> updateCaptor = ArgumentCaptor.forClass(StockResponse.class);

        // CheckStock with null/empty product name
        inventoryGrpcService.checkStock(checkRequestNull, responseObserver);
        verify(responseObserver).onNext(checkCaptor.capture());
        verify(responseObserver, atLeastOnce()).onCompleted();

        StockResponse checkResponse = checkCaptor.getValue();
        assertFalse(checkResponse.getAvailable());
        assertEquals(0, checkResponse.getStockQuantity());
        assertEquals("Insufficient stock", checkResponse.getMessage());

        // UpdateStock with null/empty product name
        inventoryGrpcService.updateStock(updateRequestNull, responseObserver);
        verify(responseObserver, atLeast(2)).onNext(updateCaptor.capture());
        verify(responseObserver, atLeast(2)).onCompleted();

        StockResponse updateResponse = updateCaptor.getValue();
        assertFalse(updateResponse.getAvailable());
        assertEquals(0, updateResponse.getStockQuantity());
        assertEquals("Failed to update stock", updateResponse.getMessage());
    }
}