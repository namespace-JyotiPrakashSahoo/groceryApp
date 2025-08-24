package com.example.groceryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequestDTO {
    private Long groceryItemId;
    
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity = 1;
}
