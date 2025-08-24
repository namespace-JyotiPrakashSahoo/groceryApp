package com.example.groceryApp.repositories;

import com.example.groceryApp.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
// import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);
    
    List<Order> findByOrderDateBetween(Date startDate, Date endDate);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.customer.id = :customerId")
    List<Order> findByCustomerIdWithItems(@Param("customerId") Long customerId);

    // @Query("SELECT o FROM Order o JOIN FETCH o.orderItems WHERE o.customer.id = :customerId")
    // List<Order> findByCustomerIdWithItems(@Param("customerId") Long customerId);

    // @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :id")
    // Optional<Order> findByIdWithItems(@Param("id") Long id);

    // fetch all orders with items
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems")
    List<Order> findAllWithItems();
}
