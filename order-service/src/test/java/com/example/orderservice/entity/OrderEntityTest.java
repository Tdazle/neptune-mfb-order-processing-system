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