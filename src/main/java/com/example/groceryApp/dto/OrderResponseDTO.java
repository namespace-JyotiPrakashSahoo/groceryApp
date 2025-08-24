package com.example.groceryApp.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private Long id;
    private CustomerResponseDTO customer;
    private List<OrderItemResponseDTO> items;
    private Date orderDate;
    private Double totalPrice;
}

