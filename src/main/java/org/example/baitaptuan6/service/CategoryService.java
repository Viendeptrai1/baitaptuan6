package org.example.baitaptuan6.service;

import org.example.baitaptuan6.entity.Category;
import org.example.baitaptuan6.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    // Get all active categories
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findByIsActiveTrue();
    }
    
    // Get all active categories with pagination
    public Page<Category> getAllActiveCategories(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return categoryRepository.findByIsActiveTrue(pageable);
    }
    
    // Search categories
    public Page<Category> searchCategories(String keyword, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return categoryRepository.searchActiveCategories(keyword, pageable);
    }
    
    // Get category by ID
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    
    // Get active category by ID
    public Optional<Category> getActiveCategoryById(Long id) {
        return categoryRepository.findById(id)
            .filter(category -> category.getIsActive());
    }
    
    // Create new category
    public Category createCategory(Category category) {
        // Check if category name already exists
        if (categoryRepository.findByNameIgnoreCase(category.getName()).isPresent()) {
            throw new RuntimeException("Tên danh mục đã tồn tại");
        }
        return categoryRepository.save(category);
    }
    
    // Update category
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));
        
        // Check if new name already exists (excluding current category)
        if (categoryRepository.existsByNameAndIdNot(categoryDetails.getName(), id)) {
            throw new RuntimeException("Tên danh mục đã tồn tại");
        }
        
        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        category.setIsActive(categoryDetails.getIsActive());
        
        return categoryRepository.save(category);
    }
    
    // Soft delete category (deactivate)
    public void deactivateCategory(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));
        category.setIsActive(false);
        categoryRepository.save(category);
    }
    
    // Activate category
    public void activateCategory(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));
        category.setIsActive(true);
        categoryRepository.save(category);
    }
    
    // Hard delete category
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));
        categoryRepository.delete(category);
    }
    
    // Count active categories
    public long countActiveCategories() {
        return categoryRepository.countByIsActiveTrue();
    }
    
    // Check if category name exists
    public boolean existsByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name).isPresent();
    }
}
