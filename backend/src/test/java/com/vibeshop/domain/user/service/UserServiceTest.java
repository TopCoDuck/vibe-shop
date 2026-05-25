package com.vibeshop.domain.user.service;

import com.vibeshop.domain.user.dto.LoginRequest;
import com.vibeshop.domain.user.dto.SignupRequest;
import com.vibeshop.domain.user.dto.TokenResponse;
import com.vibeshop.domain.user.dto.UserResponse;
import com.vibeshop.domain.user.entity.Role;
import com.vibeshop.domain.user.entity.User;
import com.vibeshop.domain.user.repository.UserRepository;
import com.vibeshop.global.exception.BusinessException;
import com.vibeshop.global.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .name("테스트유저")
                .phone("010-1234-5678")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("회원가입 - 성공")
    void signup_success() {
        // given
        SignupRequest request = new SignupRequest();
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setName("신규유저");
        request.setPhone("010-0000-0000");

        given(userRepository.existsByEmail("new@example.com")).willReturn(false);
        given(passwordEncoder.encode("password123")).willReturn("encodedPw");
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        UserResponse response = userService.signup(request);

        // then
        assertThat(response).isNotNull();
        then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 - 이메일 중복")
    void signup_duplicateEmail() {
        // given
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        given(userRepository.existsByEmail("test@example.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signup(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("이미 사용 중인 이메일");
    }

    @Test
    @DisplayName("로그인 - 성공")
    void login_success() {
        // given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(user));
        given(jwtTokenProvider.generateAccessToken("test@example.com")).willReturn("access-token");
        given(jwtTokenProvider.generateRefreshToken("test@example.com")).willReturn("refresh-token");

        // when
        TokenResponse response = userService.login(request);

        // then
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        then(authenticationManager).should().authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("로그인 - 존재하지 않는 이메일")
    void login_userNotFound() {
        // given
        LoginRequest request = new LoginRequest();
        request.setEmail("notfound@example.com");
        request.setPassword("password123");

        given(userRepository.findByEmail("notfound@example.com")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("내 정보 조회 - 성공")
    void getMyInfo_success() {
        // given
        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(user));

        // when
        UserResponse response = userService.getMyInfo("test@example.com");

        // then
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getName()).isEqualTo("테스트유저");
    }
}
