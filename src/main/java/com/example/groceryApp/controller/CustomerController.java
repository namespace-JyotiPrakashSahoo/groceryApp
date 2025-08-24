package com.example.groceryApp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.groceryApp.dto.CustomerDTO;
import com.example.groceryApp.dto.CustomerResponseDTO;

import com.example.groceryApp.services.CustomerService;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer Management", description = "APIs for managing customers")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    @Operation(summary = "Get all customers")
    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }
    
    @Operation(summary = "Get customer by ID")
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }
    
    @Operation(summary = "Create a new customer")
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        return new ResponseEntity<>(customerService.createCustomer(customerDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Update customer by ID")
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @Parameter(description = "ID of the customer to be updated", required = true)
            @PathVariable Long id,
            
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated customer data", required = true)
            @Valid @RequestBody CustomerDTO customerDTO) {
        
        return ResponseEntity.ok(customerService.updateCustomer(id, customerDTO));
    }
    
    @Operation(summary = "Delete customer by ID")
    @ApiResponse(responseCode = "204", description = "Customer deleted successfully")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    @ApiResponse(responseCode = "409", description = "Customer has existing orders and cannot be deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "ID of the customer to be deleted", required = true)
            @PathVariable Long id) {
        
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}