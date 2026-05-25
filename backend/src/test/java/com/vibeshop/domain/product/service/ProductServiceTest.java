package com.vibeshop.domain.product.service;

import com.vibeshop.domain.category.entity.Category;
import com.vibeshop.domain.category.repository.CategoryRepository;
import com.vibeshop.domain.product.dto.ProductRequest;
import com.vibeshop.domain.product.dto.ProductResponse;
import com.vibeshop.domain.product.entity.Product;
import com.vibeshop.domain.product.entity.ProductStatus;
import com.vibeshop.domain.product.repository.ProductRepository;
import com.vibeshop.global.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService 단위 테스트")
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("전자제품")
                .build();

        product = Product.builder()
                .id(1L)
                .name("테스트 상품")
                .description("테스트 설명")
                .price(BigDecimal.valueOf(10000))
                .stock(100)
                .imageUrl("http://example.com/image.jpg")
                .category(category)
                .build();
    }

    @Test
    @DisplayName("상품 단건 조회 - 성공")
    void findById_success() {
        // given
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        // when
        ProductResponse response = productService.findById(1L);

        // then
        assertThat(response.getName()).isEqualTo("테스트 상품");
        assertThat(response.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(10000));
        assertThat(response.getCategoryName()).isEqualTo("전자제품");
    }

    @Test
    @DisplayName("상품 단건 조회 - 존재하지 않는 상품")
    void findById_notFound() {
        // given
        given(productRepository.findById(99L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.findById(99L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("상품을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("상품 목록 조회 - 키워드 검색")
    void findAll_withKeyword() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(product));
        given(productRepository.searchByKeyword("테스트", ProductStatus.ACTIVE, pageable))
                .willReturn(page);

        // when
        Page<ProductResponse> result = productService.findAll(null, "테스트", pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("테스트 상품");
    }

    @Test
    @DisplayName("상품 목록 조회 - 카테고리 필터")
    void findAll_withCategory() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(product));
        given(productRepository.findByCategoryIdAndStatus(1L, ProductStatus.ACTIVE, pageable))
                .willReturn(page);

        // when
        Page<ProductResponse> result = productService.findAll(1L, null, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("상품 생성 - 성공")
    void create_success() {
        // given
        ProductRequest request = new ProductRequest();
        request.setName("새 상품");
        request.setDescription("새 설명");
        request.setPrice(BigDecimal.valueOf(20000));
        request.setStock(50);
        request.setCategoryId(1L);

        Product newProduct = Product.builder()
                .id(2L)
                .name("새 상품")
                .description("새 설명")
                .price(BigDecimal.valueOf(20000))
                .stock(50)
                .category(category)
                .build();

        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
        given(productRepository.save(any(Product.class))).willReturn(newProduct);

        // when
        ProductResponse response = productService.create(request);

        // then
        assertThat(response.getName()).isEqualTo("새 상품");
        assertThat(response.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(20000));
        then(productRepository).should().save(any(Product.class));
    }

    @Test
    @DisplayName("상품 생성 - 존재하지 않는 카테고리")
    void create_categoryNotFound() {
        // given
        ProductRequest request = new ProductRequest();
        request.setCategoryId(99L);
        given(categoryRepository.findById(99L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("카테고리를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("상품 삭제 - INACTIVE 상태로 변경")
    void delete_changesStatusToInactive() {
        // given
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        // when
        productService.delete(1L);

        // then
        assertThat(product.getStatus()).isEqualTo(ProductStatus.INACTIVE);
    }
}
