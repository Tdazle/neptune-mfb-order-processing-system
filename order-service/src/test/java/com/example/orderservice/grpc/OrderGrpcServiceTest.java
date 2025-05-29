package com.example.orderservice.grpc;

import com.example.orderservice.entity.Order;
import com.example.orderservice.service.OrderService;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderGrpcServiceTest {

    @Mock
    private OrderService orderService;

    @Mock
    private StreamObserver<OrderResponse> responseObserver;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    private OrderGrpcService orderGrpcService;

    /**
     * Sets up the test fixture before each test method is run.
     * <p>
     * Initializes the Mockito annotations for the test class, which
     * injects the mock objects into the test class. Also creates a new
     * instance of the order grpc service with the mock order service.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderGrpcService = new OrderGrpcService(orderService);
    }

    /**
     * Verifies that a successful order creation returns a response with the status
     * "CREATED".
     * <p>
     * Sets up a valid order request and a saved order with the status "CREATED".
     * Mocks the order service to return the saved order when the input order
     * is created. Invokes the order grpc service to create the order and verifies
     * that the response status is "CREATED".
     */
    @Test
    void testCreateOrderSuccessReturnsCreatedStatus() {
        OrderRequest request = OrderRequest.newBuilder()
                .setProduct("Widget")
                .setQuantity(5)
                .build();

        Order savedOrder = new Order();
        savedOrder.setProduct("Widget");
        savedOrder.setQuantity(5);
        savedOrder.setStatus("CREATED");

        when(orderService.createOrder(any(Order.class))).thenReturn(savedOrder);

        orderGrpcService.createOrder(request, responseObserver);

        ArgumentCaptor<OrderResponse> responseCaptor = ArgumentCaptor.forClass(OrderResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        assertEquals("CREATED", responseCaptor.getValue().getStatus());
    }

    /**
     * Verifies that a valid order creation request is persisted using the order
     * service.
     * <p>
     * Sets up a valid order request and a saved order with the status "CREATED".
     * Mocks the order service to return the saved order when the input order
     * is created. Invokes the order grpc service to create the order and verifies
     * that the order is persisted using the order service.
     */
    @Test
    void testOrderIsPersistedOnValidRequest() {
        OrderRequest request = OrderRequest.newBuilder()
                .setProduct("Gadget")
                .setQuantity(2)
                .build();

        Order persistedOrder = new Order();
        persistedOrder.setProduct("Gadget");
        persistedOrder.setQuantity(2);
        persistedOrder.setStatus("CREATED");

        when(orderService.createOrder(any(Order.class))).thenReturn(persistedOrder);

        orderGrpcService.createOrder(request, responseObserver);

        verify(orderService).createOrder(orderCaptor.capture());
        Order capturedOrder = orderCaptor.getValue();
        assertEquals("Gadget", capturedOrder.getProduct());
        assertEquals(2, capturedOrder.getQuantity());
    }

    /**
     * Verifies that the response observer is completed after a successful order
     * creation.
     * <p>
     * Sets up a valid order request and a saved order with the status "CREATED".
     * Mocks the order service to return the saved order when the input order
     * is created. Invokes the order grpc service to create the order and verifies
     * that the order is persisted using the order service, and that the response
     * observer is completed with the saved order.
     */
    @Test
    void testResponseObserverCompletedOnSuccess() {
        OrderRequest request = OrderRequest.newBuilder()
                .setProduct("Book")
                .setQuantity(1)
                .build();

        Order savedOrder = new Order();
        savedOrder.setProduct("Book");
        savedOrder.setQuantity(1);
        savedOrder.setStatus("CREATED");

        when(orderService.createOrder(any(Order.class))).thenReturn(savedOrder);

        orderGrpcService.createOrder(request, responseObserver);

        InOrder inOrder = inOrder(responseObserver);
        inOrder.verify(responseObserver).onNext(any(OrderResponse.class));
        inOrder.verify(responseObserver).onCompleted();
    }

    /**
     * Verifies that an invalid order creation request is handled with an
     * INVALID ARGUMENT response (HTTP 400).
     * <p>
     * Sets up an invalid order request and mocks the order service to throw an
     * exception with the correct error message. Invokes the order grpc service
     * to create the order and verifies that the response observer is invoked
     * with an error that is an instance of {@link StatusRuntimeException} with
     * the correct status code and error message.
     */
    @Test
    void testCreateOrderInvalidInputReturnsInvalidArgument() {
        OrderRequest request = OrderRequest.newBuilder()
                .setProduct("")
                .setQuantity(0)
                .build();

        when(orderService.createOrder(any(Order.class)))
                .thenThrow(new IllegalArgumentException("Invalid order details"));

        orderGrpcService.createOrder(request, responseObserver);

        ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(responseObserver).onError(errorCaptor.capture());
        Throwable thrown = errorCaptor.getValue();
        assertInstanceOf(StatusRuntimeException.class, thrown);
        StatusRuntimeException sre = (StatusRuntimeException) thrown;
        assertEquals(Status.INVALID_ARGUMENT.getCode(), sre.getStatus().getCode());
        assertNotNull(sre.getStatus().getDescription());
        assertTrue(sre.getStatus().getDescription().contains("Invalid order details"));
    }

    /**
     * Verifies that an unexpected error during order creation is handled with an
     * INVALID ARGUMENT response (HTTP 400).
     * <p>
     * Sets up a valid order request and mocks the order service to throw a
     * {@link RuntimeException} with the error message "Unexpected error".
     * Invokes the order grpc service to create the order and verifies that the
     * response observer is invoked with an error that is an instance of
     * {@link StatusRuntimeException} with the correct status code and error
     * message.
     */
    @Test
    void testCreateOrderRuntimeExceptionReturnsInvalidArgument() {
        OrderRequest request = OrderRequest.newBuilder()
                .setProduct("Pen")
                .setQuantity(3)
                .build();

        when(orderService.createOrder(any(Order.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        orderGrpcService.createOrder(request, responseObserver);

        ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(responseObserver).onError(errorCaptor.capture());
        Throwable thrown = errorCaptor.getValue();
        assertInstanceOf(StatusRuntimeException.class, thrown);
        StatusRuntimeException sre = (StatusRuntimeException) thrown;
        assertEquals(Status.INVALID_ARGUMENT.getCode(), sre.getStatus().getCode());
        assertNotNull(sre.getStatus().getDescription());
        assertTrue(sre.getStatus().getDescription().contains("Unexpected error"));
    }

    /**
     * Verifies that the response observer is never invoked with
     * {@link StreamObserver#onCompleted()} after an error has occurred.
     * <p>
     * Sets up a valid order request and mocks the order service to throw a
     * {@link RuntimeException} with the error message "Database error".
     * Invokes the order grpc service to create the order and verifies that the
     * response observer is invoked with an error and that
     * {@link StreamObserver#onCompleted()} is never invoked.
     */
    @Test
    void testNoOnCompletedAfterOnError() {
        OrderRequest request = OrderRequest.newBuilder()
                .setProduct("Laptop")
                .setQuantity(1)
                .build();

        when(orderService.createOrder(any(Order.class)))
                .thenThrow(new RuntimeException("Database error"));

        orderGrpcService.createOrder(request, responseObserver);

        verify(responseObserver).onError(any(Throwable.class));
        verify(responseObserver, never()).onCompleted();
    }
}