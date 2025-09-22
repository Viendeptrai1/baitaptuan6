package org.example.baitaptuan6.controller;

import org.example.baitaptuan6.entity.User;
import org.example.baitaptuan6.service.UserService;
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
@RequestMapping("/admin/users")
public class AdminUserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) User.UserRole role,
            Model model) {
        
        Page<User> users;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            users = userService.searchUsers(keyword, page, size, sortBy, sortDir);
            model.addAttribute("keyword", keyword);
        } else if (role != null) {
            users = userService.getUsersByRole(role, page, size, sortBy, sortDir);
            model.addAttribute("selectedRole", role);
        } else {
            users = userService.getAllActiveUsers(page, size, sortBy, sortDir);
        }
        
        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("totalItems", users.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("roles", User.UserRole.values());
        
        return "admin/users/list";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", User.UserRole.values());
        return "admin/users/form";
    }
    
    @PostMapping
    public String createUser(@Valid @ModelAttribute("user") User user,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/users/form";
        }
        
        try {
            userService.createUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo người dùng thành công!");
            return "redirect:/admin/users";
        } catch (RuntimeException e) {
            result.rejectValue("username", "error.user", e.getMessage());
            return "admin/users/form";
        }
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.getActiveUserById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));
        model.addAttribute("user", user);
        model.addAttribute("roles", User.UserRole.values());
        return "admin/users/form";
    }
    
    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id,
                            @Valid @ModelAttribute("user") User user,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/users/form";
        }
        
        try {
            userService.updateUser(id, user);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật người dùng thành công!");
            return "redirect:/admin/users";
        } catch (RuntimeException e) {
            result.rejectValue("username", "error.user", e.getMessage());
            return "admin/users/form";
        }
    }
    
    @GetMapping("/{id}/delete")
    public String showDeleteConfirmation(@PathVariable Long id, Model model) {
        User user = userService.getActiveUserById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));
        model.addAttribute("user", user);
        return "admin/users/delete";
    }
    
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id,
                            @RequestParam(defaultValue = "false") boolean hardDelete,
                            RedirectAttributes redirectAttributes) {
        try {
            if (hardDelete) {
                userService.deleteUser(id);
                redirectAttributes.addFlashAttribute("successMessage", "Xóa người dùng vĩnh viễn thành công!");
            } else {
                userService.deactivateUser(id);
                redirectAttributes.addFlashAttribute("successMessage", "Vô hiệu hóa người dùng thành công!");
            }
            return "redirect:/admin/users";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/users";
        }
    }
    
    @PostMapping("/{id}/toggle")
    public String toggleUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));
            
            if (user.getIsActive()) {
                userService.deactivateUser(id);
                redirectAttributes.addFlashAttribute("successMessage", "Vô hiệu hóa người dùng thành công!");
            } else {
                userService.activateUser(id);
                redirectAttributes.addFlashAttribute("successMessage", "Kích hoạt người dùng thành công!");
            }
            return "redirect:/admin/users";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/users";
        }
    }
    
    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = userService.getActiveUserById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));
        model.addAttribute("user", user);
        return "admin/users/detail";
    }
}
