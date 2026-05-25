package com.vibeshop.domain.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vibeshop.domain.category.entity.Category;
import com.vibeshop.domain.category.repository.CategoryRepository;
import com.vibeshop.domain.product.entity.Product;
import com.vibeshop.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("ProductController 통합 테스트")
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        category = categoryRepository.save(Category.builder()
                .name("전자제품")
                .build());

        product = productRepository.save(Product.builder()
                .name("테스트 상품")
                .description("테스트 설명")
                .price(BigDecimal.valueOf(10000))
                .stock(100)
                .category(category)
                .build());
    }

    @Test
    @DisplayName("상품 목록 조회 - 인증 없이 접근 가능")
    void getAll_publicAccess() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @DisplayName("상품 단건 조회 - 성공")
    void getOne_success() throws Exception {
        mockMvc.perform(get("/api/products/{id}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("테스트 상품"))
                .andExpect(jsonPath("$.data.price").value(10000));
    }

    @Test
    @DisplayName("상품 단건 조회 - 존재하지 않는 상품")
    void getOne_notFound() throws Exception {
        mockMvc.perform(get("/api/products/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("상품 등록 - ADMIN 권한 필요")
    @WithMockUser(roles = "ADMIN")
    void create_withAdminRole() throws Exception {
        String requestBody = """
                {
                    "name": "신규 상품",
                    "description": "신규 설명",
                    "price": 50000,
                    "stock": 30,
                    "categoryId": %d
                }
                """.formatted(category.getId());

        mockMvc.perform(post("/api/products/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("신규 상품"));
    }

    @Test
    @DisplayName("상품 등록 - 인증 없이 접근 불가")
    void create_unauthorized() throws Exception {
        String requestBody = """
                {
                    "name": "신규 상품",
                    "price": 50000,
                    "stock": 30,
                    "categoryId": 1
                }
                """;

        mockMvc.perform(post("/api/products/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("상품 목록 조회 - 키워드 검색")
    void getAll_withKeyword() throws Exception {
        mockMvc.perform(get("/api/products").param("keyword", "테스트"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].name").value("테스트 상품"));
    }
}
