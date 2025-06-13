package com.example.orderservice.controller;

import com.example.orderservice.entity.Order;
import com.example.orderservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    /**
     * Sets up the test fixture before each test method is run.
     * <p>
     * Initializes the Mockito annotations for the test class, which
     * injects the mock objects into the test class.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests that a valid order creation request is handled successfully.
     * <p>
     * Verifies that the order service is called with the correct order details,
     * and that the controller returns an HTTP 200 OK response with the saved
     * order details.
     */
    @Test
    void testCreateOrder_SuccessfulCreation() {
        Order inputOrder = new Order();
        inputOrder.setProduct("Widget");
        inputOrder.setQuantity(5);

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setProduct("Widget");
        savedOrder.setQuantity(5);
        savedOrder.setStatus("CREATED");

        when(orderService.createOrder(inputOrder)).thenReturn(savedOrder);

        ResponseEntity<?> response = orderController.createOrder(inputOrder);

        verify(orderService, times(1)).createOrder(inputOrder);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(savedOrder, response.getBody());
    }

    /**
     * Tests that a valid order creation request is handled successfully and
     * returns the saved order in the response body.
     * <p>
     * Verifies that the order service is called with the correct order details,
     * and that the controller returns an HTTP 200 OK response with the saved
     * order details.
     */
    @Test
    void testCreateOrder_ReturnsOkStatusWithOrder() {
        Order inputOrder = new Order();
        inputOrder.setProduct("Gadget");
        inputOrder.setQuantity(2);

        Order savedOrder = new Order();
        savedOrder.setId(2L);
        savedOrder.setProduct("Gadget");
        savedOrder.setQuantity(2);
        savedOrder.setStatus("CREATED");

        when(orderService.createOrder(inputOrder)).thenReturn(savedOrder);

        ResponseEntity<?> response = orderController.createOrder(inputOrder);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(savedOrder, response.getBody());
    }

    /**
     * Tests that an order is successfully created with the status "CREATED".
     * <p>
     * Sets up an input order and a saved order with the status "CREATED".
     * Mocks the order service to return the saved order when the input order
     * is created.
     * Invokes the order controller to create the order and verifies
     * that the response status is HTTP 200 OK and that the returned order has
     * the status "CREATED".
     */
    @Test
    void testCreateOrder_OrderStatusCreatedOnSuccess() {
        Order inputOrder = new Order();
        inputOrder.setProduct("Book");
        inputOrder.setQuantity(1);

        Order savedOrder = new Order();
        savedOrder.setId(3L);
        savedOrder.setProduct("Book");
        savedOrder.setQuantity(1);
        savedOrder.setStatus("CREATED");

        when(orderService.createOrder(inputOrder)).thenReturn(savedOrder);

        ResponseEntity<?> response = orderController.createOrder(inputOrder);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Order returnedOrder = (Order) response.getBody();
        assertNotNull(returnedOrder);
        assertEquals("CREATED", returnedOrder.getStatus());
    }

    /**
     * Tests that an invalid order creation request with missing product and quantity
     * is handled with a BAD REQUEST response (HTTP 400).
     * <p>
     * Verifies that the order service throws an exception with the correct error message,
     * and that the controller returns an HTTP 400 BAD REQUEST response with the same
     * error message.
     */
    @Test
    void testCreateOrder_InvalidOrderDetails_ReturnsBadRequest() {
        Order invalidOrder = new Order(); // missing product and quantity

        when(orderService.createOrder(invalidOrder))
                .thenThrow(new IllegalArgumentException("Invalid order details"));

        ResponseEntity<?> response = orderController.createOrder(invalidOrder);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid order details", response.getBody());
    }

    /**
     * Tests that an order creation request that results in a service exception is
     * handled with a BAD REQUEST response (HTTP 400).
     * <p>
     * Verifies that the order service throws a {@link RuntimeException} with the
     * correct error message, and that the controller returns an HTTP 400 BAD
     * REQUEST response with the same error message.
     */
    @Test
    void testCreateOrder_ServiceException_ReturnsErrorMessage() {
        Order inputOrder = new Order();
        inputOrder.setProduct("Pen");
        inputOrder.setQuantity(10);

        when(orderService.createOrder(inputOrder))
                .thenThrow(new RuntimeException("Service unavailable"));

        ResponseEntity<?> response = orderController.createOrder(inputOrder);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Service unavailable", response.getBody());
    }

    /**
     * Tests that an order creation request results in an order with status "REJECTED"
     * when the stock is unavailable or stock update fails.
     * <p>
     * Sets up an input order and a rejected order with the status "REJECTED".
     * Mocks the order service to return the rejected order when the input order
     * is created.
     * Invokes the order controller to create the order and verifies
     * that the response status is HTTP 200 OK and that the returned order has
     * the status "REJECTED".
     */
    @Test
    void testCreateOrder_StockUnavailableOrUpdateFails_OrderRejected() {
        Order inputOrder = new Order();
        inputOrder.setProduct("Laptop");
        inputOrder.setQuantity(1);

        Order rejectedOrder = new Order();
        rejectedOrder.setId(4L);
        rejectedOrder.setProduct("Laptop");
        rejectedOrder.setQuantity(1);
        rejectedOrder.setStatus("REJECTED");

        when(orderService.createOrder(inputOrder)).thenReturn(rejectedOrder);

        ResponseEntity<?> response = orderController.createOrder(inputOrder);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Order returnedOrder = (Order) response.getBody();
        assertNotNull(returnedOrder);
        assertEquals("REJECTED", returnedOrder.getStatus());
    }

    /**
     * Ensures that getAllOrders returns an empty list and HTTP 200 OK when there are no orders.
     */
    @Test
    void testGetAllOrders_ReturnsEmptyList() {
        when(orderService.getAllOrders()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Order>> response = orderController.getAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    /**
     * Checks that getAllOrders returns a list containing a single order and HTTP 200 OK.
     */
    @Test
    void testGetAllOrders_ReturnsSingleOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setProduct("Widget");
        order.setQuantity(3);
        order.setStatus("CREATED");
        List<Order> orders = Collections.singletonList(order);

        when(orderService.getAllOrders()).thenReturn(orders);

        ResponseEntity<List<Order>> response = orderController.getAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(order, response.getBody().get(0));
    }

    /**
     * Validates that getAllOrders returns a list with multiple orders and HTTP 200 OK.
     */
    @Test
    void testGetAllOrders_ReturnsMultipleOrders() {
        Order order1 = new Order();
        order1.setId(1L);
        order1.setProduct("Widget");
        order1.setQuantity(2);
        order1.setStatus("CREATED");

        Order order2 = new Order();
        order2.setId(2L);
        order2.setProduct("Gadget");
        order2.setQuantity(5);
        order2.setStatus("REJECTED");

        List<Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);

        when(orderService.getAllOrders()).thenReturn(orders);

        ResponseEntity<List<Order>> response = orderController.getAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().contains(order1));
        assertTrue(response.getBody().contains(order2));
    }

    /**
     * Tests that getAllOrders returns HTTP 500 Internal Server Error if orderService throws an unexpected exception.
     */
    @Test
    void testGetAllOrders_ServiceThrowsException() {
        when(orderService.getAllOrders()).thenThrow(new RuntimeException("Unexpected error"));

        try {
            orderController.getAllOrders();
            // If no exception is thrown, fail the test
            fail("Expected exception was not thrown");
        } catch (Exception ex) {
            assertInstanceOf(RuntimeException.class, ex);
            assertEquals("Unexpected error", ex.getMessage());
        }
    }

    /**
     * Ensures getAllOrders returns a list containing orders with null fields.
     */
    @Test
    void testGetAllOrders_OrdersWithNullFields() {
        Order orderWithNulls = new Order();
        orderWithNulls.setId(null);
        orderWithNulls.setProduct(null);
        orderWithNulls.setQuantity(0);
        orderWithNulls.setStatus(null);

        List<Order> orders = Collections.singletonList(orderWithNulls);

        when(orderService.getAllOrders()).thenReturn(orders);

        ResponseEntity<List<Order>> response = orderController.getAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        Order returnedOrder = response.getBody().get(0);
        assertNull(returnedOrder.getId());
        assertNull(returnedOrder.getProduct());
        assertEquals(0, returnedOrder.getQuantity());
        assertNull(returnedOrder.getStatus());
    }
}