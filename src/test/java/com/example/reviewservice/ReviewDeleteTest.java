package com.example.reviewservice;

import com.example.reviewservice.dto.ReviewResponseDTO;
import com.example.reviewservice.mapper.ReviewMapper;
import com.example.reviewservice.model.Review;
import com.example.reviewservice.order.OrderClient;
import com.example.reviewservice.service.ReviewService;
import com.example.reviewservice.user.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceDeleteTest {

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    @DisplayName("관리자면_삭제성공")
    void 관리자면_삭제성공() {
        // given
        Review sample = Review.builder()
                .userUid(7)
                .build();
        when(reviewMapper.findByUid(1)).thenReturn(sample);
        when(reviewMapper.deleteByUid(1)).thenReturn(1);

        CustomUserDetails admin = mock(CustomUserDetails.class);
        when(admin.getUid()).thenReturn(999); // 실제 작성자(7)와 다르지만, ROLE_ADMIN 이면 통과
        doReturn(Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        ))
                .when(admin).getAuthorities();

        // when
        ReviewResponseDTO dto = reviewService.deleteReview(1, admin);

        // then
        assertTrue(dto.isSuccess());
        assertEquals("리뷰가 성공적으로 삭제되었습니다.", dto.getMessage());
    }

    @Test
    @DisplayName("권한없으면_삭제실패")
    void 권한없으면_삭제실패() {
        // given
        Review sample = Review.builder()
                .userUid(8)
                .build();
        when(reviewMapper.findByUid(2)).thenReturn(sample);

        CustomUserDetails user = mock(CustomUserDetails.class);
        when(user.getUid()).thenReturn(9); // 작성자(8)와 다름
        doReturn(Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER")
        ))
                .when(user).getAuthorities();

        // when
        ReviewResponseDTO dto = reviewService.deleteReview(2, user);

        // then
        assertFalse(dto.isSuccess());
        assertEquals("삭제 권한이 없습니다.", dto.getMessage());
    }

    @Test
    @DisplayName("리뷰없으면_삭제실패")
    void 리뷰없으면_삭제실패() {
        // given
        when(reviewMapper.findByUid(3)).thenReturn(null);

        CustomUserDetails anyUser = mock(CustomUserDetails.class);

        // when
        ReviewResponseDTO dto = reviewService.deleteReview(3, anyUser);

        // then
        assertFalse(dto.isSuccess());
        assertEquals("삭제할 리뷰를 찾을 수 없습니다.", dto.getMessage());
    }
}
