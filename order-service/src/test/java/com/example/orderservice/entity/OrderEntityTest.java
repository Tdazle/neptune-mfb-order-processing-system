package com.example.orderservice.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

@DataJpaTest
public class OrderEntityTest {

    @Autowired
    private EntityManager entityManager;

    /**
     * Tests that an Order entity with valid fields is persisted correctly.
     * <p>
     * Creates an Order object with specific product, quantity, and status fields,
     * persists it using the entity manager, and then retrieves it to verify
     * that it has been saved with the correct details.
     */
    @Test
    @Transactional
    @Rollback
    public void testOrderEntityPersistenceWithValidFields() {
        Order order = new Order();
        order.setProduct("Laptop");
        order.setQuantity(2);
        order.setStatus("NEW");

        entityManager.persist(order);
        entityManager.flush();

        Order found = entityManager.find(Order.class, order.getId());
        Assertions.assertNotNull(found);
        Assertions.assertEquals("Laptop", found.getProduct());
        Assertions.assertEquals(2, found.getQuantity());
        Assertions.assertEquals("NEW", found.getStatus());
    }

    /**
     * Tests that the getters of the Order entity return the correct values.
     * <p>
     * Creates an Order object, sets its product, quantity, and status fields,
     * and verifies that the getters return the expected values.
     */
    @Test
    public void testOrderEntityGettersReturnCorrectValues() {
        Order order = new Order();
        order.setProduct("Phone");
        order.setQuantity(5);
        order.setStatus("SHIPPED");

        Assertions.assertEquals("Phone", order.getProduct());
        Assertions.assertEquals(5, order.getQuantity());
        Assertions.assertEquals("SHIPPED", order.getStatus());
    }

    /**
     * Tests that the Order entity automatically generates a unique ID upon persistence.
     * <p>
     * Persists two Order objects with different product, quantity, and status fields,
     * verifies that each order is assigned a non-null ID, and checks that the IDs are
     * unique between the two orders.
     */
    @Test
    @Transactional
    @Rollback
    public void testOrderEntityAutoGeneratesId() {
        Order order1 = new Order();
        order1.setProduct("Tablet");
        order1.setQuantity(1);
        order1.setStatus("PROCESSING");

        entityManager.persist(order1);
        entityManager.flush();

        Assertions.assertNotNull(order1.getId());

        Order order2 = new Order();
        order2.setProduct("Monitor");
        order2.setQuantity(3);
        order2.setStatus("NEW");

        entityManager.persist(order2);
        entityManager.flush();

        Assertions.assertNotNull(order2.getId());
        Assertions.assertNotEquals(order1.getId(), order2.getId());
    }

    /**
     * Tests that the Order entity persists with a quantity of zero.
     * <p>
     * Creates an Order object with a product, quantity of zero, and status fields,
     * persists the order, and verifies that the order is found with the correct
     * quantity of zero.
     */
    @Test
    @Transactional
    @Rollback
    public void testOrderEntityWithZeroQuantity() {
        Order order = new Order();
        order.setProduct("Book");
        order.setQuantity(0);
        order.setStatus("PENDING");

        entityManager.persist(order);
        entityManager.flush();

        Order found = entityManager.find(Order.class, order.getId());
        Assertions.assertNotNull(found);
        Assertions.assertEquals(0, found.getQuantity());
    }
}