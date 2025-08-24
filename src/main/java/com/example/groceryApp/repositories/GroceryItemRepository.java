package com.example.groceryApp.repositories;

import com.example.groceryApp.models.GroceryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface GroceryItemRepository extends JpaRepository<GroceryItem, Long> {
    List<GroceryItem> findByCategory(String category);
    List<GroceryItem> findByNameContainingIgnoreCase(String name);
}
