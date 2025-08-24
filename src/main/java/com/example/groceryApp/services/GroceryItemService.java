package com.example.groceryApp.services;

import com.example.groceryApp.models.GroceryItem;
import com.example.groceryApp.exceptions.ResourceNotFoundException;
import com.example.groceryApp.repositories.GroceryItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroceryItemService {
    
    @Autowired
    private GroceryItemRepository groceryItemRepository;
    
    public List<GroceryItem> getAllGroceryItems() {
        return groceryItemRepository.findAll();
    }
    
    public GroceryItem getGroceryItemById(Long id) {
        return groceryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grocery item not found with id: " + id));
    }
    
    public List<GroceryItem> getGroceryItemsByCategory(String category) {
        return groceryItemRepository.findByCategory(category);
    }
    
    public GroceryItem createGroceryItem(GroceryItem groceryItem) {
        return groceryItemRepository.save(groceryItem);
    }
    
    public GroceryItem updateGroceryItem(Long id, GroceryItem groceryItemDetails) {
        GroceryItem groceryItem = getGroceryItemById(id);
        groceryItem.setName(groceryItemDetails.getName());
        groceryItem.setCategory(groceryItemDetails.getCategory());
        groceryItem.setPrice(groceryItemDetails.getPrice());
        groceryItem.setQuantity(groceryItemDetails.getQuantity());
        return groceryItemRepository.save(groceryItem);
    }
    
    public void deleteGroceryItem(Long id) {
        GroceryItem groceryItem = getGroceryItemById(id);
        groceryItemRepository.delete(groceryItem);
    }
}
