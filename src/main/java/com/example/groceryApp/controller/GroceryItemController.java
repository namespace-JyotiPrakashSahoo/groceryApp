package com.example.groceryApp.controller;

import com.example.groceryApp.models.GroceryItem;
import com.example.groceryApp.services.GroceryItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/grocery-items")
@Validated
public class GroceryItemController {
    
    @Autowired
    private GroceryItemService groceryItemService;
    
    @GetMapping
    public ResponseEntity<List<GroceryItem>> getAllGroceryItems() {
        return ResponseEntity.ok(groceryItemService.getAllGroceryItems());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<GroceryItem> getGroceryItemById(@PathVariable Long id) {
        return ResponseEntity.ok(groceryItemService.getGroceryItemById(id));
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<GroceryItem>> getGroceryItemsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(groceryItemService.getGroceryItemsByCategory(category));
    }
    
    @PostMapping
    public ResponseEntity<GroceryItem> createGroceryItem(@Valid @RequestBody GroceryItem groceryItem) {
        return new ResponseEntity<>(groceryItemService.createGroceryItem(groceryItem), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<GroceryItem> updateGroceryItem(@PathVariable Long id, @Valid @RequestBody GroceryItem groceryItemDetails) {
        return ResponseEntity.ok(groceryItemService.updateGroceryItem(id, groceryItemDetails));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroceryItem(@PathVariable Long id) {
        groceryItemService.deleteGroceryItem(id);
        return ResponseEntity.noContent().build();
    }
}
