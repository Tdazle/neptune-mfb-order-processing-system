package com.example.orderservice.grpc;

import com.example.orderservice.entity.Order;
import com.example.orderservice.service.OrderService;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderGrpcServiceTest {

    @InjectMocks
    private OrderGrpcService orderGrpcService;

    @Mock
    private OrderService orderService;

    @Mock
    private StreamObserver<OrderResponse> responseObserver;

    @Captor
    private ArgumentCaptor<OrderResponse> orderResponseCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrderWithValidInput() {
        OrderRequest request = OrderRequest.newBuilder()
                .setProduct("Widget")
                .setQuantity(5)
                .build();

        Order savedOrder = new Order();
        savedOrder.setProduct("Widget");
        savedOrder.setQuantity(5);
        savedOrder.setStatus("PENDING");

        when(orderService.createOrder(any(Order.class))).thenReturn(savedOrder);

        orderGrpcService.createOrder(request, responseObserver);

        verify(responseObserver).onNext(orderResponseCaptor.capture());
        verify(responseObserver).onCompleted();

        OrderResponse response = orderResponseCaptor.getValue();
        assertEquals("PENDING", response.getStatus());
    }

    @Test
    void testOrderStatusIsPendingAfterCreation() {
        OrderRequest request = OrderRequest.newBuilder()
                .setProduct("Gadget")
                .setQuantity(2)
                .build();

        Order savedOrder = new Order();
        savedOrder.setProduct("Gadget");
        savedOrder.setQuantity(2);
        savedOrder.setStatus("PENDING");

        when(orderService.createOrder(any(Order.class))).thenReturn(savedOrder);

        orderGrpcService.createOrder(request, responseObserver);

        verify(responseObserver).onNext(orderResponseCaptor.capture());
        OrderResponse response = orderResponseCaptor.getValue();
        assertEquals("PENDING", response.getStatus());
    }

    @Test
    void testOrderResponseStatusMatchesSavedOrder() {
        OrderRequest request = OrderRequest.newBuilder()
                .setProduct("Book")
                .setQuantity(1)
                .build();

        Order savedOrder = new Order();
        savedOrder.setProduct("Book");
        savedOrder.setQuantity(1);
        savedOrder.setStatus("CONFIRMED");

        when(orderService.createOrder(any(Order.class))).thenReturn(savedOrder);

        orderGrpcService.createOrder(request, responseObserver);

        verify(responseObserver).onNext(orderResponseCaptor.capture());
        OrderResponse response = orderResponseCaptor.getValue();
        assertEquals("CONFIRMED", response.getStatus());
    }

    @Test
    void testCreateOrderWithEmptyProduct() {
        OrderRequest request = OrderRequest.newBuilder()
                .setProduct("")
                .setQuantity(3)
                .build();

        // Simulate that OrderService will not be called and no order is created
        // Optionally, you may want to throw an exception or handle this gracefully in the service
        // For this test, let's assume the service throws IllegalArgumentException

        doThrow(new IllegalArgumentException("Product cannot be empty"))
                .when(orderService).createOrder(any(Order.class));

        orderGrpcService.createOrder(request, responseObserver);

        verify(responseObserver, never()).onNext(any());
        verify(responseObserver).onError(any(IllegalArgumentException.class));
        verify(responseObserver, never()).onCompleted();
    }

    @Test
    void testCreateOrderWithInvalidQuantity() {
        OrderRequest request = OrderRequest.newBuilder()
                .setProduct("Pen")
                .setQuantity(0)
                .build();

        doThrow(new IllegalArgumentException("Quantity must be positive"))
                .when(orderService).createOrder(any(Order.class));

        orderGrpcService.createOrder(request, responseObserver);

        verify(responseObserver, never()).onNext(any());
        verify(responseObserver).onError(any(IllegalArgumentException.class));
        verify(responseObserver, never()).onCompleted();
    }

    @Test
    void testCreateOrderHandlesOrderServiceException() {
        OrderRequest request = OrderRequest.newBuilder()
                .setProduct("Laptop")
                .setQuantity(1)
                .build();

        RuntimeException serviceException = new RuntimeException("Database error");
        when(orderService.createOrder(any(Order.class))).thenThrow(serviceException);

        orderGrpcService.createOrder(request, responseObserver);

        verify(responseObserver, never()).onNext(any());
        verify(responseObserver).onError(serviceException);
        verify(responseObserver, never()).onCompleted();
    }
}