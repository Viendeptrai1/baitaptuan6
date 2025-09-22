package org.example.baitaptuan6.controller;

import org.example.baitaptuan6.service.CategoryService;
import org.example.baitaptuan6.service.UserService;
import org.example.baitaptuan6.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private VideoService videoService;
    
    @GetMapping("/")
    public String home(Model model) {
        long categoryCount = categoryService.countActiveCategories();
        long userCount = userService.countActiveUsers();
        long videoCount = videoService.countActiveVideos();
        
        model.addAttribute("categoryCount", categoryCount);
        model.addAttribute("userCount", userCount);
        model.addAttribute("videoCount", videoCount);
        
        return "home";
    }
    
    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        long categoryCount = categoryService.countActiveCategories();
        long userCount = userService.countActiveUsers();
        long videoCount = videoService.countActiveVideos();
        
        model.addAttribute("categoryCount", categoryCount);
        model.addAttribute("userCount", userCount);
        model.addAttribute("videoCount", videoCount);
        
        return "admin/dashboard";
    }
}
