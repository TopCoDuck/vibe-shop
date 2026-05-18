package com.vibeshop.domain.category.controller;

import com.vibeshop.domain.category.entity.Category;
import com.vibeshop.domain.category.service.CategoryService;
import com.vibeshop.global.common.ApiResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.findAll()));
    }

    @GetMapping("/roots")
    public ResponseEntity<ApiResponse<List<Category>>> getRoots() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.findRootCategories()));
    }

    @PostMapping("/admin")
    public ResponseEntity<ApiResponse<Category>> create(@RequestBody CategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                categoryService.create(request.getName(), request.getDescription(), request.getParentId())));
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<Category>> update(@PathVariable Long id, @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                categoryService.update(id, request.getName(), request.getDescription())));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("카테고리가 삭제되었습니다.", null));
    }

    @Getter
    static class CategoryRequest {
        private String name;
        private String description;
        private Long parentId;
    }
}
