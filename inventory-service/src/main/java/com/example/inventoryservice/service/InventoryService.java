package com.example.inventoryservice.service;

import com.example.inventoryservice.entity.Product;
import com.example.inventoryservice.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    private final ProductRepository productRepository;

    public InventoryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public boolean checkStock(String productName, int quantity) {
        Product product = productRepository.findByName(productName);
        return product != null && product.getStockQuantity() >= quantity;
    }

    public boolean updateStock(String productName, int quantity) {
        Product product = productRepository.findByName(productName);
        if (product != null && product.getStockQuantity() >= quantity) {
            product.setStockQuantity(product.getStockQuantity() - quantity);
            productRepository.save(product);
            return true;
        }
        return false;
    }

    public Product getProductByName(String productName) {
        return productRepository.findByName(productName);
    }
}