package com.example.orderservice.service;

import com.example.inventoryservice.grpc.InventoryServiceGrpc;
import com.example.inventoryservice.grpc.StockRequest;
import com.example.inventoryservice.grpc.UpdateStockRequest;
import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderRepository;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @GrpcClient("inventory-service")
    private InventoryServiceGrpc.InventoryServiceBlockingStub inventoryServiceStub;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Creates a new order by checking the stock for the given product and quantity, updating the stock if the product is available, and saving the order to the database.
     * If the product is not available or the stock update fails, the order is marked as REJECTED and an exception is thrown.
     *
     * @param order the order to be created, which should contain a product and quantity
     * @return the created order
     * @throws IllegalArgumentException if the order details are invalid
     * @throws RuntimeException if the stock check or update fails
     */
    public Order createOrder(Order order) {
        if (order == null || order.getProduct() == null || order.getQuantity() <= 0) {
            order = new Order();
            order.setStatus("REJECTED");
            orderRepository.save(order);
            throw new IllegalArgumentException("Invalid order details");
        }

        try {
            StockRequest stockRequest = StockRequest.newBuilder()
                    .setProduct(order.getProduct())
                    .setQuantity(order.getQuantity())
                    .build();

            var stockResponse = inventoryServiceStub.checkStock(stockRequest);

            if (stockResponse.getAvailable()) {
                UpdateStockRequest updateRequest = UpdateStockRequest.newBuilder()
                        .setProduct(order.getProduct())
                        .setQuantity(order.getQuantity())
                        .build();
                var updateResponse = inventoryServiceStub.updateStock(updateRequest);

                if (updateResponse.getAvailable()) {
                    order.setStatus("CREATED");
                    return orderRepository.save(order);
                } else {
                    order.setStatus("REJECTED");
                    orderRepository.save(order);
                    throw new RuntimeException("Failed to update stock: " + updateResponse.getMessage());
                }
            } else {
                order.setStatus("REJECTED");
                return orderRepository.save(order);
            }
        } catch (StatusRuntimeException e) {
            order.setStatus("REJECTED");
            orderRepository.save(order);
            throw new RuntimeException("gRPC error: " + e.getStatus().getDescription());
        }
    }

    /**
     * Retrieves all orders from the database.
     *
     * @return list of all orders
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}