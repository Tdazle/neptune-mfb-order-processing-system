package com.example.inventoryservice.service;

import com.example.inventoryservice.entity.Product;
import com.example.inventoryservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InventoryService {
    private final ProductRepository productRepository;

    public InventoryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Checks if the specified quantity of a product is available in stock.
     *
     * @param productName the name of the product to check
     * @param quantity the quantity to check for availability
     * @return {@code true} if the product exists, and the stock quantity is enough,
     *         {@code false} otherwise
     */
    public boolean checkStock(String productName, int quantity) {
        Product product = productRepository.findByName(productName);
        return product != null && product.getStockQuantity() >= quantity;
    }

    /**
     * Updates the stock quantity of a specified product by reducing it by the given amount.
     *
     * @param productName the name of the product to update
     * @param quantity the amount to reduce the product's stock quantity by
     * @return {@code true} if the stock was successfully updated, {@code false} if the product
     *         does not exist or there is not enough stock to fulfill the update
     */
    public boolean updateStock(String productName, int quantity) {
        Product product = productRepository.findByName(productName);
        if (product != null && product.getStockQuantity() >= quantity) {
            product.setStockQuantity(product.getStockQuantity() - quantity);
            productRepository.save(product);
            return true;
        }
        return false;
    }

    /**
     * Retrieve a list of all products.
     *
     * @return a list of all products in the inventory
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Retrieves a product by its name.
     *
     * @param productName the name of the product to retrieve
     * @return the product with the given name, or {@code null} if no such product exists
     */
    public Product getProductByName(String productName) {
        return productRepository.findByName(productName);
    }
}