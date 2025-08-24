package com.example.groceryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroceryItemDTO {
    private Long id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Double price;
    
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
}
