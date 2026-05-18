package com.vibeshop.domain.category.service;

import com.vibeshop.domain.category.entity.Category;
import com.vibeshop.domain.category.repository.CategoryRepository;
import com.vibeshop.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public List<Category> findRootCategories() {
        return categoryRepository.findByParentIsNull();
    }

    @Transactional
    public Category create(String name, String description, Long parentId) {
        Category parent = null;
        if (parentId != null) {
            parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> BusinessException.notFound("부모 카테고리를 찾을 수 없습니다."));
        }
        Category category = Category.builder()
                .name(name)
                .description(description)
                .parent(parent)
                .build();
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(Long id, String name, String description) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("카테고리를 찾을 수 없습니다."));
        category.update(name, description);
        return category;
    }

    @Transactional
    public void delete(Long id) {
        categoryRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("카테고리를 찾을 수 없습니다."));
        categoryRepository.deleteById(id);
    }
}
