package org.example.baitaptuan6.controller;

import org.example.baitaptuan6.entity.Category;
import org.example.baitaptuan6.service.CategoryService;
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
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping
    public String listCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String keyword,
            Model model) {
        
        Page<Category> categories;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            categories = categoryService.searchCategories(keyword, page, size, sortBy, sortDir);
            model.addAttribute("keyword", keyword);
        } else {
            categories = categoryService.getAllActiveCategories(page, size, sortBy, sortDir);
        }
        
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categories.getTotalPages());
        model.addAttribute("totalItems", categories.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        
        return "admin/categories/list";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/categories/form";
    }
    
    @PostMapping
    public String createCategory(@Valid @ModelAttribute("category") Category category,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/categories/form";
        }
        
        try {
            categoryService.createCategory(category);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo danh mục thành công!");
            return "redirect:/admin/categories";
        } catch (RuntimeException e) {
            result.rejectValue("name", "error.category", e.getMessage());
            return "admin/categories/form";
        }
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getActiveCategoryById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));
        model.addAttribute("category", category);
        return "admin/categories/form";
    }
    
    @PostMapping("/{id}")
    public String updateCategory(@PathVariable Long id,
                                @Valid @ModelAttribute("category") Category category,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/categories/form";
        }
        
        try {
            categoryService.updateCategory(id, category);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật danh mục thành công!");
            return "redirect:/admin/categories";
        } catch (RuntimeException e) {
            result.rejectValue("name", "error.category", e.getMessage());
            return "admin/categories/form";
        }
    }
    
    @GetMapping("/{id}/delete")
    public String showDeleteConfirmation(@PathVariable Long id, Model model) {
        Category category = categoryService.getActiveCategoryById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));
        model.addAttribute("category", category);
        return "admin/categories/delete";
    }
    
    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id,
                                @RequestParam(defaultValue = "false") boolean hardDelete,
                                RedirectAttributes redirectAttributes) {
        try {
            if (hardDelete) {
                categoryService.deleteCategory(id);
                redirectAttributes.addFlashAttribute("successMessage", "Xóa danh mục vĩnh viễn thành công!");
            } else {
                categoryService.deactivateCategory(id);
                redirectAttributes.addFlashAttribute("successMessage", "Vô hiệu hóa danh mục thành công!");
            }
            return "redirect:/admin/categories";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/categories";
        }
    }
    
    @PostMapping("/{id}/toggle")
    public String toggleCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));
            
            if (category.getIsActive()) {
                categoryService.deactivateCategory(id);
                redirectAttributes.addFlashAttribute("successMessage", "Vô hiệu hóa danh mục thành công!");
            } else {
                categoryService.activateCategory(id);
                redirectAttributes.addFlashAttribute("successMessage", "Kích hoạt danh mục thành công!");
            }
            return "redirect:/admin/categories";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/categories";
        }
    }
    
    @GetMapping("/{id}")
    public String viewCategory(@PathVariable Long id, Model model) {
        Category category = categoryService.getActiveCategoryById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));
        model.addAttribute("category", category);
        return "admin/categories/detail";
    }
}
