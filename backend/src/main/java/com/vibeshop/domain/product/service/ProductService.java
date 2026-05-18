package com.vibeshop.domain.product.service;

import com.vibeshop.domain.category.entity.Category;
import com.vibeshop.domain.category.repository.CategoryRepository;
import com.vibeshop.domain.product.dto.ProductRequest;
import com.vibeshop.domain.product.dto.ProductResponse;
import com.vibeshop.domain.product.entity.Product;
import com.vibeshop.domain.product.entity.ProductStatus;
import com.vibeshop.domain.product.repository.ProductRepository;
import com.vibeshop.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Page<ProductResponse> findAll(Long categoryId, String keyword, Pageable pageable) {
        if (StringUtils.hasText(keyword)) {
            return productRepository.searchByKeyword(keyword, ProductStatus.ACTIVE, pageable)
                    .map(ProductResponse::new);
        }
        if (categoryId != null) {
            return productRepository.findByCategoryIdAndStatus(categoryId, ProductStatus.ACTIVE, pageable)
                    .map(ProductResponse::new);
        }
        return productRepository.findByStatus(ProductStatus.ACTIVE, pageable)
                .map(ProductResponse::new);
    }

    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("상품을 찾을 수 없습니다."));
        return new ProductResponse(product);
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> BusinessException.notFound("카테고리를 찾을 수 없습니다."));
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .category(category)
                .build();
        return new ProductResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("상품을 찾을 수 없습니다."));
        product.update(request.getName(), request.getDescription(), request.getPrice(),
                request.getStock(), request.getImageUrl());
        return new ProductResponse(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("상품을 찾을 수 없습니다."));
        product.changeStatus(ProductStatus.INACTIVE);
    }

    public Page<ProductResponse> findAllForAdmin(Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductResponse::new);
    }
}
