package com.example.orderservice.grpc;

import com.example.orderservice.entity.Order;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.grpc.OrderServiceGrpc.OrderServiceImplBase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class OrderGrpcService extends OrderServiceImplBase {
    private final OrderService orderService;

    public OrderGrpcService(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Handles the creation of an order based on the provided request.
     *
     * @param request the request containing order details such as product and quantity
     * @param responseObserver the observer to send responses back to the client
     *<p>
     * This method processes the order request, persists the order using the order service,
     * and sends a response back to the client with the order status.
     */
    @Override
    public void createOrder(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        Order order = new Order();
        order.setProduct(request.getProduct());
        order.setQuantity(request.getQuantity());
        Order savedOrder = orderService.createOrder(order);

        OrderResponse response = OrderResponse.newBuilder()
                .setStatus(savedOrder.getStatus())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}