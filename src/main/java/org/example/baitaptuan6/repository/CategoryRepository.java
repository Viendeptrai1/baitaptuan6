package org.example.baitaptuan6.repository;

import org.example.baitaptuan6.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // Find active categories
    List<Category> findByIsActiveTrue();
    
    // Find active categories with pagination
    Page<Category> findByIsActiveTrue(Pageable pageable);
    
    // Search categories by name or description
    @Query("SELECT c FROM Category c WHERE c.isActive = true AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Category> searchActiveCategories(@Param("keyword") String keyword, Pageable pageable);
    
    // Find category by name (case insensitive)
    Optional<Category> findByNameIgnoreCase(String name);
    
    // Check if category exists by name (excluding current category for update)
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.name = :name AND c.id != :id")
    boolean existsByNameAndIdNot(@Param("name") String name, @Param("id") Long id);
    
    // Count active categories
    long countByIsActiveTrue();
    
    // Find categories by name containing (case insensitive)
    List<Category> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
}
