package org.example.baitaptuan6.controller;

import org.example.baitaptuan6.entity.Category;
import org.example.baitaptuan6.entity.User;
import org.example.baitaptuan6.entity.Video;
import org.example.baitaptuan6.service.CategoryService;
import org.example.baitaptuan6.service.UserService;
import org.example.baitaptuan6.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/videos")
public class AdminVideoController {
    
    @Autowired
    private VideoService videoService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public String listVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long userId,
            Model model) {
        
        Page<Video> videos;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            videos = videoService.searchVideos(keyword, page, size, sortBy, sortDir);
            model.addAttribute("keyword", keyword);
        } else if (categoryId != null && userId != null) {
            videos = videoService.getVideosByCategoryAndUser(categoryId, userId, page, size, sortBy, sortDir);
            model.addAttribute("selectedCategoryId", categoryId);
            model.addAttribute("selectedUserId", userId);
        } else if (categoryId != null) {
            videos = videoService.getVideosByCategory(categoryId, page, size, sortBy, sortDir);
            model.addAttribute("selectedCategoryId", categoryId);
        } else if (userId != null) {
            videos = videoService.getVideosByUser(userId, page, size, sortBy, sortDir);
            model.addAttribute("selectedUserId", userId);
        } else {
            videos = videoService.getAllActiveVideos(page, size, sortBy, sortDir);
        }
        
        List<Category> categories = categoryService.getAllActiveCategories();
        List<User> users = userService.getAllActiveUsers();
        
        model.addAttribute("videos", videos);
        model.addAttribute("categories", categories);
        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", videos.getTotalPages());
        model.addAttribute("totalItems", videos.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        
        return "admin/videos/list";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        List<Category> categories = categoryService.getAllActiveCategories();
        List<User> users = userService.getAllActiveUsers();
        
        model.addAttribute("video", new Video());
        model.addAttribute("categories", categories);
        model.addAttribute("users", users);
        return "admin/videos/form";
    }
    
    @PostMapping
    public String createVideo(@Valid @ModelAttribute("video") Video video,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            List<Category> categories = categoryService.getAllActiveCategories();
            List<User> users = userService.getAllActiveUsers();
            return "admin/videos/form";
        }
        
        try {
            videoService.createVideo(video);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo video thành công!");
            return "redirect:/admin/videos";
        } catch (RuntimeException e) {
            result.rejectValue("title", "error.video", e.getMessage());
            List<Category> categories = categoryService.getAllActiveCategories();
            List<User> users = userService.getAllActiveUsers();
            return "admin/videos/form";
        }
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Video video = videoService.getActiveVideoById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy video với ID: " + id));
        List<Category> categories = categoryService.getAllActiveCategories();
        List<User> users = userService.getAllActiveUsers();
        
        model.addAttribute("video", video);
        model.addAttribute("categories", categories);
        model.addAttribute("users", users);
        return "admin/videos/form";
    }
    
    @PostMapping("/{id}")
    public String updateVideo(@PathVariable Long id,
                             @Valid @ModelAttribute("video") Video video,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            List<Category> categories = categoryService.getAllActiveCategories();
            List<User> users = userService.getAllActiveUsers();
            return "admin/videos/form";
        }
        
        try {
            videoService.updateVideo(id, video);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật video thành công!");
            return "redirect:/admin/videos";
        } catch (RuntimeException e) {
            result.rejectValue("title", "error.video", e.getMessage());
            List<Category> categories = categoryService.getAllActiveCategories();
            List<User> users = userService.getAllActiveUsers();
            return "admin/videos/form";
        }
    }
    
    @GetMapping("/{id}/delete")
    public String showDeleteConfirmation(@PathVariable Long id, Model model) {
        Video video = videoService.getActiveVideoById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy video với ID: " + id));
        model.addAttribute("video", video);
        return "admin/videos/delete";
    }
    
    @PostMapping("/{id}/delete")
    public String deleteVideo(@PathVariable Long id,
                             @RequestParam(defaultValue = "false") boolean hardDelete,
                             RedirectAttributes redirectAttributes) {
        try {
            if (hardDelete) {
                videoService.deleteVideo(id);
                redirectAttributes.addFlashAttribute("successMessage", "Xóa video vĩnh viễn thành công!");
            } else {
                videoService.deactivateVideo(id);
                redirectAttributes.addFlashAttribute("successMessage", "Vô hiệu hóa video thành công!");
            }
            return "redirect:/admin/videos";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/videos";
        }
    }
    
    @PostMapping("/{id}/toggle")
    public String toggleVideo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Video video = videoService.getVideoById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy video với ID: " + id));
            
            if (video.getIsActive()) {
                videoService.deactivateVideo(id);
                redirectAttributes.addFlashAttribute("successMessage", "Vô hiệu hóa video thành công!");
            } else {
                videoService.activateVideo(id);
                redirectAttributes.addFlashAttribute("successMessage", "Kích hoạt video thành công!");
            }
            return "redirect:/admin/videos";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/videos";
        }
    }
    
    @GetMapping("/{id}")
    public String viewVideo(@PathVariable Long id, Model model) {
        Video video = videoService.getActiveVideoById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy video với ID: " + id));
        model.addAttribute("video", video);
        return "admin/videos/detail";
    }
    
    @PostMapping("/{id}/increment-views")
    public String incrementViews(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            videoService.incrementViews(id);
            redirectAttributes.addFlashAttribute("successMessage", "Tăng lượt xem thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/videos/" + id;
    }
    
    @PostMapping("/{id}/increment-likes")
    public String incrementLikes(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            videoService.incrementLikes(id);
            redirectAttributes.addFlashAttribute("successMessage", "Tăng lượt thích thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/videos/" + id;
    }
}
