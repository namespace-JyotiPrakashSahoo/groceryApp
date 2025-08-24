package com.example.groceryApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grocery_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "orderItems")
@EqualsAndHashCode(exclude = "orderItems")
public class GroceryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Column(nullable = false, unique = true)
    private String name;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Double price;
    
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
    
    @OneToMany(mappedBy = "groceryItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OrderItem> orderItems = new ArrayList<>();
}