package org.example.baitaptuan6.service;

import org.example.baitaptuan6.entity.Video;
import org.example.baitaptuan6.entity.Category;
import org.example.baitaptuan6.entity.User;
import org.example.baitaptuan6.repository.VideoRepository;
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
public class VideoService {
    
    @Autowired
    private VideoRepository videoRepository;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private UserService userService;
    
    // Get all active videos
    public List<Video> getAllActiveVideos() {
        return videoRepository.findByIsActiveTrue();
    }
    
    // Get all active videos with pagination
    public Page<Video> getAllActiveVideos(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return videoRepository.findByIsActiveTrue(pageable);
    }
    
    // Search videos
    public Page<Video> searchVideos(String keyword, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return videoRepository.searchActiveVideos(keyword, pageable);
    }
    
    // Get videos by category
    public List<Video> getVideosByCategory(Long categoryId) {
        return videoRepository.findByCategoryIdAndIsActiveTrue(categoryId);
    }
    
    // Get videos by category with pagination
    public Page<Video> getVideosByCategory(Long categoryId, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return videoRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable);
    }
    
    // Get videos by user
    public List<Video> getVideosByUser(Long userId) {
        return videoRepository.findByUserIdAndIsActiveTrue(userId);
    }
    
    // Get videos by user with pagination
    public Page<Video> getVideosByUser(Long userId, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return videoRepository.findByUserIdAndIsActiveTrue(userId, pageable);
    }
    
    // Get videos by category and user
    public List<Video> getVideosByCategoryAndUser(Long categoryId, Long userId) {
        return videoRepository.findByCategoryIdAndUserIdAndIsActiveTrue(categoryId, userId);
    }
    
    // Get videos by category and user with pagination
    public Page<Video> getVideosByCategoryAndUser(Long categoryId, Long userId, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return videoRepository.findByCategoryIdAndUserIdAndIsActiveTrue(categoryId, userId, pageable);
    }
    
    // Get most viewed videos
    public Page<Video> getMostViewedVideos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return videoRepository.findMostViewedVideos(pageable);
    }
    
    // Get most liked videos
    public Page<Video> getMostLikedVideos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return videoRepository.findMostLikedVideos(pageable);
    }
    
    // Get recent videos
    public Page<Video> getRecentVideos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return videoRepository.findRecentVideos(pageable);
    }
    
    // Get video by ID
    public Optional<Video> getVideoById(Long id) {
        return videoRepository.findById(id);
    }
    
    // Get active video by ID
    public Optional<Video> getActiveVideoById(Long id) {
        return videoRepository.findById(id)
            .filter(video -> video.getIsActive());
    }
    
    // Create new video
    public Video createVideo(Video video) {
        // Validate category exists and is active
        Category category = categoryService.getActiveCategoryById(video.getCategory().getId())
            .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại hoặc không hoạt động"));
        
        // Validate user exists and is active
        User user = userService.getActiveUserById(video.getUser().getId())
            .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại hoặc không hoạt động"));
        
        video.setCategory(category);
        video.setUser(user);
        
        return videoRepository.save(video);
    }
    
    // Update video
    public Video updateVideo(Long id, Video videoDetails) {
        Video video = videoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy video với ID: " + id));
        
        // Validate category exists and is active
        Category category = categoryService.getActiveCategoryById(videoDetails.getCategory().getId())
            .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại hoặc không hoạt động"));
        
        // Validate user exists and is active
        User user = userService.getActiveUserById(videoDetails.getUser().getId())
            .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại hoặc không hoạt động"));
        
        video.setTitle(videoDetails.getTitle());
        video.setDescription(videoDetails.getDescription());
        video.setUrl(videoDetails.getUrl());
        video.setDuration(videoDetails.getDuration());
        video.setIsActive(videoDetails.getIsActive());
        video.setCategory(category);
        video.setUser(user);
        
        return videoRepository.save(video);
    }
    
    // Soft delete video (deactivate)
    public void deactivateVideo(Long id) {
        Video video = videoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy video với ID: " + id));
        video.setIsActive(false);
        videoRepository.save(video);
    }
    
    // Activate video
    public void activateVideo(Long id) {
        Video video = videoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy video với ID: " + id));
        video.setIsActive(true);
        videoRepository.save(video);
    }
    
    // Hard delete video
    public void deleteVideo(Long id) {
        Video video = videoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy video với ID: " + id));
        videoRepository.delete(video);
    }
    
    // Increment view count
    public void incrementViews(Long id) {
        Video video = videoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy video với ID: " + id));
        video.setViews(video.getViews() + 1);
        videoRepository.save(video);
    }
    
    // Increment like count
    public void incrementLikes(Long id) {
        Video video = videoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy video với ID: " + id));
        video.setLikes(video.getLikes() + 1);
        videoRepository.save(video);
    }
    
    // Count active videos
    public long countActiveVideos() {
        return videoRepository.countByIsActiveTrue();
    }
    
    // Count videos by category
    public long countVideosByCategory(Long categoryId) {
        return videoRepository.countByCategoryIdAndIsActiveTrue(categoryId);
    }
    
    // Count videos by user
    public long countVideosByUser(Long userId) {
        return videoRepository.countByUserIdAndIsActiveTrue(userId);
    }
}
