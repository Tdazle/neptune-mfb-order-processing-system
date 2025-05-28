package com.example.inventoryservice.repository;

import com.example.inventoryservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    /**
     * Find a product by name.
     *
     * @param name the name of the product
     * @return the product with the given name, or {@code null} if no such product exists
     */
    Product findByName(String name);
}