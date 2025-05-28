package com.example.orderservice.service;

import com.example.inventoryservice.grpc.InventoryServiceGrpc;
import com.example.inventoryservice.grpc.StockRequest;
import com.example.inventoryservice.grpc.StockResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderRepository;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private InventoryServiceGrpc.InventoryServiceBlockingStub inventoryServiceStub;

    @InjectMocks
    private OrderService orderService;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    /**
     * Sets up the test fixture before each test method is run.
     * <p>
     * Initializes the Mockito annotations for the test class, which
     * injects the mock objects into the test class. Also creates a new
     * instance of the order service with the mock order repository.
     * Finally, injects the mock inventory service stub using reflection
     * since it's annotated with {@link GrpcClient}.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(orderRepository);
        // Inject the mock stub via reflection since it's annotated with @GrpcClient
        try {
            java.lang.reflect.Field stubField = OrderService.class.getDeclaredField("inventoryServiceStub");
            stubField.setAccessible(true);
            stubField.set(orderService, inventoryServiceStub);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tests that an order is correctly rejected if the product is not available.
     * <p>
     * Mocks the stock service to return a response with the product as unavailable,
     * and the order repository to return a rejected order. Invokes the order service
     * to create the order, verifies that the response status is "REJECTED", and
     * that the order is saved with the same status.
     */
    @Test
    void testCreateOrder_ProductNotAvailable() {
        Order order = new Order();
        order.setProduct("Widget");
        order.setQuantity(3);

        StockResponse stockResponse = mock(StockResponse.class);
        when(stockResponse.getAvailable()).thenReturn(false);

        when(inventoryServiceStub.checkStock(any(StockRequest.class))).thenReturn(stockResponse);

        Order rejectedOrder = new Order();
        rejectedOrder.setProduct("Widget");
        rejectedOrder.setQuantity(3);
        rejectedOrder.setStatus("REJECTED");
        when(orderRepository.save(any(Order.class))).thenReturn(rejectedOrder);

        Order result = orderService.createOrder(order);

        assertEquals("REJECTED", result.getStatus());
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals("REJECTED", orderCaptor.getValue().getStatus());
    }

    /**
     * Tests that an order creation request with invalid order details
     * results in an IllegalArgumentException being thrown.
     * <p>
     * Sets up an invalid order with null product and zero quantity.
     * Mocks the order repository to return a rejected order with status
     * "REJECTED". Invokes the order service to create the order and
     * verifies that an IllegalArgumentException is thrown with the
     * expected message, and that the order is saved with the status
     * "REJECTED".
     */
    @Test
    void testCreateOrder_InvalidOrderDetails() {
        Order invalidOrder = new Order();
        invalidOrder.setProduct(null);
        invalidOrder.setQuantity(0);

        Order rejectedOrder = new Order();
        rejectedOrder.setStatus("REJECTED");
        when(orderRepository.save(any(Order.class))).thenReturn(rejectedOrder);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(invalidOrder));
        assertEquals("Invalid order details", ex.getMessage());
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals("REJECTED", orderCaptor.getValue().getStatus());
    }

    /**
     * Tests that an order creation request that results in a gRPC failure
     * (i.e., the inventory service is unavailable) results in a
     * {@link RuntimeException} being thrown.
     * <p>
     * Verifies that the runtime exception is thrown with a message that
     * contains the phrase "gRPC error", and that the order is saved with
     * the status "REJECTED".
     */
    @Test
    void testCreateOrder_GrpcFailure() {
        Order order = new Order();
        order.setProduct("Widget");
        order.setQuantity(1);

        StatusRuntimeException grpcException = new StatusRuntimeException(Status.UNAVAILABLE.withDescription("gRPC unavailable"));
        when(inventoryServiceStub.checkStock(any(StockRequest.class))).thenThrow(grpcException);

        Order rejectedOrder = new Order();
        rejectedOrder.setProduct("Widget");
        rejectedOrder.setQuantity(1);
        rejectedOrder.setStatus("REJECTED");
        when(orderRepository.save(any(Order.class))).thenReturn(rejectedOrder);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> orderService.createOrder(order));
        assertTrue(ex.getMessage().contains("gRPC error"));
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals("REJECTED", orderCaptor.getValue().getStatus());
    }

    /**
     * Tests that an order creation request with a null order input results
     * in an IllegalArgumentException being thrown.
     * <p>
     * Verifies that the exception is thrown with the message "Invalid order
     * details", and that the order is saved with the status "REJECTED".
     */
    @Test
    void testCreateOrder_NullOrderInput() {
        Order rejectedOrder = new Order();
        rejectedOrder.setStatus("REJECTED");
        when(orderRepository.save(any(Order.class))).thenReturn(rejectedOrder);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(null));
        assertEquals("Invalid order details", ex.getMessage());
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals("REJECTED", orderCaptor.getValue().getStatus());
    }
}