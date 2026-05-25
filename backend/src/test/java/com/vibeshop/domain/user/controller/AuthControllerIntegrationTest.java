package com.vibeshop.domain.user.controller;

import com.vibeshop.domain.user.entity.User;
import com.vibeshop.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("AuthController 통합 테스트")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.save(User.builder()
                .email("exist@example.com")
                .password(passwordEncoder.encode("password123"))
                .name("기존유저")
                .build());
    }

    @Test
    @DisplayName("회원가입 - 성공")
    void signup_success() throws Exception {
        String requestBody = """
                {
                    "email": "new@example.com",
                    "password": "password123",
                    "name": "신규유저",
                    "phone": "010-1234-5678"
                }
                """;

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("new@example.com"))
                .andExpect(jsonPath("$.data.name").value("신규유저"));
    }

    @Test
    @DisplayName("회원가입 - 이메일 중복")
    void signup_duplicateEmail() throws Exception {
        String requestBody = """
                {
                    "email": "exist@example.com",
                    "password": "password123",
                    "name": "중복유저"
                }
                """;

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 - 성공 및 토큰 반환")
    void login_success() throws Exception {
        String requestBody = """
                {
                    "email": "exist@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.email").value("exist@example.com"));
    }

    @Test
    @DisplayName("로그인 - 잘못된 비밀번호")
    void login_wrongPassword() throws Exception {
        String requestBody = """
                {
                    "email": "exist@example.com",
                    "password": "wrongPassword"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }
}
