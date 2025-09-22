package org.example.baitaptuan6.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "videos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Video {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Tiêu đề video không được để trống")
    @Size(min = 2, max = 200, message = "Tiêu đề video phải có từ 2 đến 200 ký tự")
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Size(max = 1000, message = "Mô tả video không được vượt quá 1000 ký tự")
    @Column(name = "description", length = 1000)
    private String description;
    
    @NotBlank(message = "URL video không được để trống")
    @Size(max = 500, message = "URL video không được vượt quá 500 ký tự")
    @Column(name = "url", nullable = false, length = 500)
    private String url;
    
    @Column(name = "duration")
    private Integer duration; // Duration in seconds
    
    @Column(name = "views", nullable = false)
    private Long views = 0L;
    
    @Column(name = "likes", nullable = false)
    private Long likes = 0L;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Danh mục không được để trống")
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Người dùng không được để trống")
    private User user;
    
    // Constructor for creating new video
    public Video(String title, String description, String url, Integer duration, Category category, User user) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.duration = duration;
        this.category = category;
        this.user = user;
        this.views = 0L;
        this.likes = 0L;
        this.isActive = true;
    }
}
