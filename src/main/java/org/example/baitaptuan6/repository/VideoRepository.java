package org.example.baitaptuan6.repository;

import org.example.baitaptuan6.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    
    // Find active videos
    List<Video> findByIsActiveTrue();
    
    // Find active videos with pagination
    Page<Video> findByIsActiveTrue(Pageable pageable);
    
    // Search videos by title or description
    @Query("SELECT v FROM Video v WHERE v.isActive = true AND " +
           "(LOWER(v.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Video> searchActiveVideos(@Param("keyword") String keyword, Pageable pageable);
    
    // Find videos by category
    List<Video> findByCategoryIdAndIsActiveTrue(Long categoryId);
    
    // Find videos by category with pagination
    Page<Video> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);
    
    // Find videos by user
    List<Video> findByUserIdAndIsActiveTrue(Long userId);
    
    // Find videos by user with pagination
    Page<Video> findByUserIdAndIsActiveTrue(Long userId, Pageable pageable);
    
    // Find videos by category and user
    List<Video> findByCategoryIdAndUserIdAndIsActiveTrue(Long categoryId, Long userId);
    
    // Find videos by category and user with pagination
    Page<Video> findByCategoryIdAndUserIdAndIsActiveTrue(Long categoryId, Long userId, Pageable pageable);
    
    // Find most viewed videos
    @Query("SELECT v FROM Video v WHERE v.isActive = true ORDER BY v.views DESC")
    Page<Video> findMostViewedVideos(Pageable pageable);
    
    // Find most liked videos
    @Query("SELECT v FROM Video v WHERE v.isActive = true ORDER BY v.likes DESC")
    Page<Video> findMostLikedVideos(Pageable pageable);
    
    // Find recent videos
    @Query("SELECT v FROM Video v WHERE v.isActive = true ORDER BY v.createdAt DESC")
    Page<Video> findRecentVideos(Pageable pageable);
    
    // Count active videos
    long countByIsActiveTrue();
    
    // Count videos by category
    long countByCategoryIdAndIsActiveTrue(Long categoryId);
    
    // Count videos by user
    long countByUserIdAndIsActiveTrue(Long userId);
}
