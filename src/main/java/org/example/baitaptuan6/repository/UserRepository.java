package org.example.baitaptuan6.repository;

import org.example.baitaptuan6.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by username
    Optional<User> findByUsername(String username);
    
    // Find user by email
    Optional<User> findByEmail(String email);
    
    // Find active users
    List<User> findByIsActiveTrue();
    
    // Find active users with pagination
    Page<User> findByIsActiveTrue(Pageable pageable);
    
    // Search users by username, email, or full name
    @Query("SELECT u FROM User u WHERE u.isActive = true AND " +
           "(LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<User> searchActiveUsers(@Param("keyword") String keyword, Pageable pageable);
    
    // Find users by role
    List<User> findByRoleAndIsActiveTrue(User.UserRole role);
    
    // Find users by role with pagination
    Page<User> findByRoleAndIsActiveTrue(User.UserRole role, Pageable pageable);
    
    // Check if username exists (excluding current user for update)
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.id != :id")
    boolean existsByUsernameAndIdNot(@Param("username") String username, @Param("id") Long id);
    
    // Check if email exists (excluding current user for update)
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.id != :id")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") Long id);
    
    // Count active users
    long countByIsActiveTrue();
    
    // Count users by role
    long countByRoleAndIsActiveTrue(User.UserRole role);
}
