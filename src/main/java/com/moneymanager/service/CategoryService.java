package com.moneymanager.service;

import com.moneymanager.model.Category;
import com.moneymanager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }
    
    public void initializeDefaultCategories() {
        if (categoryRepository.count() == 0) {
            List<Category> defaultCategories = List.of(
                // Income categories
                new Category(null, "salary", Category.CategoryType.INCOME, "💰"),
                new Category(null, "freelance", Category.CategoryType.INCOME, "💼"),
                new Category(null, "investment", Category.CategoryType.INCOME, "📈"),
                new Category(null, "gift", Category.CategoryType.INCOME, "🎁"),
                new Category(null, "other-income", Category.CategoryType.INCOME, "💵"),
                
                // Expense categories
                new Category(null, "fuel", Category.CategoryType.BOTH, "⛽"),
                new Category(null, "food", Category.CategoryType.EXPENSE, "🍔"),
                new Category(null, "movie", Category.CategoryType.EXPENSE, "🎬"),
                new Category(null, "medical", Category.CategoryType.EXPENSE, "🏥"),
                new Category(null, "loan", Category.CategoryType.EXPENSE, "🏦"),
                new Category(null, "rent", Category.CategoryType.EXPENSE, "🏠"),
                new Category(null, "utilities", Category.CategoryType.EXPENSE, "💡"),
                new Category(null, "shopping", Category.CategoryType.EXPENSE, "🛍️"),
                new Category(null, "transportation", Category.CategoryType.EXPENSE, "🚗"),
                new Category(null, "entertainment", Category.CategoryType.EXPENSE, "🎮"),
                new Category(null, "education", Category.CategoryType.EXPENSE, "📚"),
                new Category(null, "other-expense", Category.CategoryType.EXPENSE, "💳")
            );
            
            categoryRepository.saveAll(defaultCategories);
        }
    }
}
