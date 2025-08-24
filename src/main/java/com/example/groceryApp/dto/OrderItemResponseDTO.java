package com.example.groceryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDTO {
    private GroceryItemResponseDTO groceryItem;
    private Integer quantity;
    private Double itemPrice;
    private Double subtotal;
}
