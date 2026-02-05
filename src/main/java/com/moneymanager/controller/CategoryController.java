package com.moneymanager.controller;

import com.moneymanager.dto.ApiResponse;
import com.moneymanager.model.Category;
import com.moneymanager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Category>> createCategory(@RequestBody Category category) {
        Category created = categoryService.createCategory(category);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(created, "Category created successfully"));
    }
    
    @PostMapping("/initialize")
    public ResponseEntity<ApiResponse<Void>> initializeCategories() {
        categoryService.initializeDefaultCategories();
        return ResponseEntity.ok(ApiResponse.success(null, "Default categories initialized"));
    }
}
