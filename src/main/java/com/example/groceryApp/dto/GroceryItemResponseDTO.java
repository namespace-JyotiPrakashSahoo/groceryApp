package com.example.groceryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroceryItemResponseDTO {
    private Long id;
    private String name;
    private String category;
    private Double price;
    private Integer quantity;
}
