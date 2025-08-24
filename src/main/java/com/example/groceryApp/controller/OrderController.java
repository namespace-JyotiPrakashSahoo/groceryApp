package com.example.groceryApp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.groceryApp.dto.OrderRequestDTO;
import com.example.groceryApp.dto.OrderResponseDTO;
import com.example.groceryApp.services.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "APIs for managing orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "Get all orders")
    @GetMapping
    // @Transactional(readOnly = true) // Add this annotation
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @Operation(summary = "Get order by ID")
    @GetMapping("/{id}")
    // @Transactional(readOnly = true) // Add this annotation
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @Operation(summary = "Get orders by customer ID")
    @GetMapping("/customer/{customerId}")
    // @Transactional(readOnly = true) // Add this annotation
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomerId(customerId));
    }

    @Operation(summary = "Create a new order")
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        return new ResponseEntity<>(orderService.createOrder(orderRequestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete order by ID")
    @ApiResponse(responseCode = "204", description = "Order deleted successfully")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "ID of the order to be deleted", required = true)
            @PathVariable Long id) {
        
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete all orders for a customer")
    @ApiResponse(responseCode = "204", description = "All orders deleted successfully")
    @ApiResponse(responseCode = "404", description = "No orders found for customer")
    @DeleteMapping("/customer/{customerId}")
    public ResponseEntity<Void> deleteOrdersByCustomerId(
            @Parameter(description = "ID of the customer whose orders should be deleted", required = true)
            @PathVariable Long customerId) {
        
        orderService.deleteOrdersByCustomerId(customerId);
        return ResponseEntity.noContent().build();
    }
}