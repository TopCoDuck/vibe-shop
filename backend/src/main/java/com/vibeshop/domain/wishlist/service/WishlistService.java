package com.vibeshop.domain.wishlist.service;

import com.vibeshop.domain.product.entity.Product;
import com.vibeshop.domain.product.repository.ProductRepository;
import com.vibeshop.domain.user.entity.User;
import com.vibeshop.domain.user.repository.UserRepository;
import com.vibeshop.domain.wishlist.dto.WishlistResponse;
import com.vibeshop.domain.wishlist.entity.Wishlist;
import com.vibeshop.domain.wishlist.repository.WishlistRepository;
import com.vibeshop.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public List<WishlistResponse> getMyWishlist(String email) {
        User user = getUser(email);
        return wishlistRepository.findAllByUserIdWithProduct(user.getId())
                .stream().map(WishlistResponse::new).toList();
    }

    public Set<Long> getMyWishlistProductIds(String email) {
        User user = getUser(email);
        return new HashSet<>(wishlistRepository.findProductIdsByUserId(user.getId()));
    }

    public boolean isWished(String email, Long productId) {
        User user = getUser(email);
        return wishlistRepository.existsByUserIdAndProductId(user.getId(), productId);
    }

    @Transactional
    public boolean toggle(String email, Long productId) {
        User user = getUser(email);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> BusinessException.notFound("상품을 찾을 수 없습니다."));

        return wishlistRepository.findByUserIdAndProductId(user.getId(), productId)
                .map(existing -> {
                    wishlistRepository.delete(existing);
                    return false; // removed
                })
                .orElseGet(() -> {
                    wishlistRepository.save(Wishlist.builder()
                            .user(user)
                            .product(product)
                            .build());
                    return true; // added
                });
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> BusinessException.notFound("사용자를 찾을 수 없습니다."));
    }
}
