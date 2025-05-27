package com.example.orderservice.controller;

import com.example.orderservice.entity.Order;
import com.example.orderservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Initializes the test environment before each test method execution.
     * Sets up Mockito annotations and configure MockMvc with the
     * OrderController under test.
     */
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    /**
     * Verifies that a POST to /orders with valid data will return
     * HTTP 200 OK and the created order with status "PENDING".
     * <p>
     * The test creates an order with id 1, product "Widget", quantity 5,
     * and status "PENDING". Then it verifies that the order
     * is returned and that the createOrder method was called once.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testCreateOrderWithValidData() throws Exception {
        Order inputOrder = new Order();
        inputOrder.setProduct("Widget");
        inputOrder.setQuantity(5);

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setProduct("Widget");
        savedOrder.setQuantity(5);
        savedOrder.setStatus("PENDING");

        when(orderService.createOrder(any(Order.class))).thenReturn(savedOrder);

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.product").value("Widget"))
                .andExpect(jsonPath("$.quantity").value(5))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(orderService, times(1)).createOrder(any(Order.class));
    }

    /**
     * Verifies that a created order has the status "PENDING".
     * <p>
     * The test sends a POST request to create an order with product "Gadget"
     * and quantity 2. It then checks that the response status is HTTP 200 OK
     * and that the status of the created order is "PENDING".
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testCreatedOrderHasPendingStatus() throws Exception {
        Order inputOrder = new Order();
        inputOrder.setProduct("Gadget");
        inputOrder.setQuantity(2);

        Order savedOrder = new Order();
        savedOrder.setId(2L);
        savedOrder.setProduct("Gadget");
        savedOrder.setQuantity(2);
        savedOrder.setStatus("PENDING");

        when(orderService.createOrder(any(Order.class))).thenReturn(savedOrder);

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    /**
     * Verifies that a POST request to create an order returns HTTP 200 OK
     * and the created order with status "PENDING".
     * <p>
     * The test sends a POST request to create an order with product "Book"
     * and quantity 1. It then checks that the response status is HTTP 200 OK
     * and that the status of the created order is "PENDING".
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testCreateOrderReturnsHttpOkAndOrder() throws Exception {
        Order inputOrder = new Order();
        inputOrder.setProduct("Book");
        inputOrder.setQuantity(1);

        Order savedOrder = new Order();
        savedOrder.setId(3L);
        savedOrder.setProduct("Book");
        savedOrder.setQuantity(1);
        savedOrder.setStatus("PENDING");

        when(orderService.createOrder(any(Order.class))).thenReturn(savedOrder);

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.product").value("Book"))
                .andExpect(jsonPath("$.quantity").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    /**
     * Verifies that a POST request to create an order with missing required fields
     * results in HTTP 400 Bad Request status.
     * <p>
     * The test sends a POST request to create an order with only the 'quantity'
     * field supplied. It then checks that the response status is HTTP 400 Bad
     * Request.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testCreateOrderWithMissingFields() throws Exception {
        // Missing required 'product' field
        String orderJson = "{\"quantity\": 10}";

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifies that a POST request to create an order with invalid data types
     * results in HTTP 400 Bad Request status.
     * <p>
     * The test sends a POST request to create an order with the 'quantity' field
     * specified as a String instead of an int. It then checks that the response
     * status is HTTP 400 Bad Request.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testCreateOrderWithInvalidDataTypes() throws Exception {
        // 'quantity' should be int but is given as a string
        String orderJson = "{\"product\": \"Pen\", \"quantity\": \"ten\"}";

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifies that a POST request to create an order handles service exceptions.
     * <p>
     * The test sends a POST request to create an order with product "Laptop"
     * and quantity 1. It mocks the orderService to throw a RuntimeException
     * simulating a service failure. It then checks that the response status
     * is HTTP 500 Internal Server Error.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testCreateOrderHandlesServiceException() throws Exception {
        Order inputOrder = new Order();
        inputOrder.setProduct("Laptop");
        inputOrder.setQuantity(1);

        when(orderService.createOrder(any(Order.class))).thenThrow(new RuntimeException("Service failure"));

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputOrder)))
                .andExpect(status().isInternalServerError());
    }
}