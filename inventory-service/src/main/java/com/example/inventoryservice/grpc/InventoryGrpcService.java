package com.example.inventoryservice.grpc;

import com.example.inventoryservice.entity.Product;
import com.example.inventoryservice.service.InventoryService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class InventoryGrpcService extends InventoryServiceGrpc.InventoryServiceImplBase {
    private final InventoryService inventoryService;

    public InventoryGrpcService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Check if the stock of the given product is available.
     *
     * @param request  request containing the product name and quantity
     * @param responseObserver  observer to be called with the response
     */
    @Override
    public void checkStock(StockRequest request, StreamObserver<StockResponse> responseObserver) {
        boolean isAvailable = inventoryService.checkStock(request.getProduct(), request.getQuantity());
        Product product = inventoryService.getProductByName(request.getProduct());
        int stockQuantity = product != null ? product.getStockQuantity() : 0;

        StockResponse response = StockResponse.newBuilder()
                .setAvailable(isAvailable)
                .setStockQuantity(stockQuantity)
                .setMessage(isAvailable ? "Stock available" : "Insufficient stock")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * Update the stock of the given product.
     *
     * @param request  request containing the product name and quantity
     * @param responseObserver  observer to be called with the response
     */
    @Override
    public void updateStock(UpdateStockRequest request, StreamObserver<StockResponse> responseObserver) {
        boolean updated = inventoryService.updateStock(request.getProduct(), request.getQuantity());
        Product product = inventoryService.getProductByName(request.getProduct());
        int stockQuantity = product != null ? product.getStockQuantity() : 0;

        StockResponse response = StockResponse.newBuilder()
                .setAvailable(updated)
                .setStockQuantity(stockQuantity)
                .setMessage(updated ? "Stock updated successfully" : "Failed to update stock")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}