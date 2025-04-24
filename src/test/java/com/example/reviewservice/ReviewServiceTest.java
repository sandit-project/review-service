package com.example.reviewservice;

import com.example.reviewservice.dto.ReviewDetailResponseDTO;
import com.example.reviewservice.dto.ReviewResponseDTO;
import com.example.reviewservice.exception.OrderNotFoundException;
import com.example.reviewservice.exception.OrderServiceUnavailableException;
import com.example.reviewservice.mapper.ReviewMapper;
import com.example.reviewservice.model.Review;
import com.example.reviewservice.order.OrderClient;
import com.example.reviewservice.service.ReviewService;
import com.example.reviewservice.user.CustomUserDetails;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    ReviewMapper reviewMapper;
    @Mock
    private CustomUserDetails userDetails;
    @Mock
    OrderClient orderClient;
    @InjectMocks
    ReviewService reviewService;

    // --- writeReview 테스트 ---

    @Test
    @DisplayName("주문이_존재하면_리뷰가_성공적으로_작성된다")
    void 주문이_존재하면_리뷰가_성공적으로_작성된다() {
        // given
        Review r = Review.builder()
                .userUid(1)
                .orderUid(42)
                .rate(BigDecimal.valueOf(5.0))
                .title("굿")
                .content("최고에요")
                .status(null)
                .build();
        doNothing().when(orderClient).getOrderByUid(42);
        when(reviewMapper.save(r)).thenReturn(r);

        // when
        ReviewResponseDTO dto = reviewService.writeReview(r);

        // then
        assertTrue(dto.isSuccess());
        assertEquals("리뷰가 작성되었습니다.", dto.getMessage());
        verify(orderClient).getOrderByUid(42);
        verify(reviewMapper).save(r);
    }

    @Test
    @DisplayName("주문이_없으면_OrderNotFoundException_발생")
    void 주문이_없으면_OrderNotFoundException_발생() {
        // given
        Review r = Review.builder().orderUid(99).build();
        doThrow(FeignException.NotFound.class)
                .when(orderClient).getOrderByUid(99);

        // when & then
        OrderNotFoundException ex = assertThrows(
                OrderNotFoundException.class,
                () -> reviewService.writeReview(r)
        );
        assertTrue(ex.getMessage().contains("orderUid=99"));
        verify(orderClient).getOrderByUid(99);
        verifyNoMoreInteractions(reviewMapper);
    }

    @Test
    @DisplayName("주문서비스_장애시_OrderServiceUnavailableException_발생")
    void 주문서비스_장애시_OrderServiceUnavailableException_발생() {
        // given
        Review r = Review.builder().orderUid(77).build();
        doThrow(FeignException.class)
                .when(orderClient).getOrderByUid(77);

        // when & then
        assertThrows(
                OrderServiceUnavailableException.class,
                () -> reviewService.writeReview(r)
        );
        verify(orderClient).getOrderByUid(77);
        verifyNoMoreInteractions(reviewMapper);
    }

    @Test
    @DisplayName("무결성_제약위반시_OrderNotFoundException_발생")
    void 무결성_제약위반시_OrderNotFoundException_발생() {
        // given
        Review r = Review.builder().orderUid(123).build();
        doNothing().when(orderClient).getOrderByUid(123);
        doThrow(DataIntegrityViolationException.class)
                .when(reviewMapper).save(r);

        // when & then
        assertThrows(
                OrderNotFoundException.class,
                () -> reviewService.writeReview(r)
        );
        verify(orderClient).getOrderByUid(123);
        verify(reviewMapper).save(r);
    }

    // --- findAllReviews 테스트 ---

    @Test
    @DisplayName("전체_리뷰_조회_성공")
    void 전체_리뷰_조회_성공() {
        // given
        Review sample = Review.builder()
                .uid(10)
                .userUid(2)
                .orderUid(99)
                .rate(BigDecimal.valueOf(4.5))
                .title("테스트")
                .content("내용")
                .status(null)
                .createdDate(LocalDateTime.of(2025,4,24,12,0))
                .version(0)
                .build();
        when(reviewMapper.findAllReviews())
                .thenReturn(List.of(sample));

        // when
        List<ReviewDetailResponseDTO> list = reviewService.findAllReviews();

        // then
        assertEquals(1, list.size());
        ReviewDetailResponseDTO dto = list.get(0);
        assertEquals(10, dto.getUid());
        assertEquals(2, dto.getUserUid());
        assertEquals(99, dto.getOrderUid());
        assertEquals("테스트", dto.getTitle());
    }

    // --- getReviewByUid 테스트 ---

    @Test
    @DisplayName("UID로_리뷰조회_성공")
    void UID로_리뷰조회_성공() throws Exception {
        // given
        Review sample = Review.builder()
                .uid(5)
                .userUid(3)
                .orderUid(55)
                .rate(BigDecimal.valueOf(3.0))
                .title("OK")
                .content("그냥그래요")
                .status(null)
                .createdDate(LocalDateTime.now())
                .version(1)
                .build();
        when(reviewMapper.findByUid(5)).thenReturn(sample);

        // when
        ReviewDetailResponseDTO dto = reviewService.getReviewByUid(5);

        // then
        assertEquals(5, dto.getUid());
        assertEquals("OK", dto.getTitle());
    }

    @Test
    @DisplayName("존재하지않는_UID로_조회시_예외발생")
    void 존재하지않는_UID로_조회시_예외발생() {
        // given
        when(reviewMapper.findByUid(1234)).thenReturn(null);

        // when & then
        assertThrows(
                NoSuchFieldException.class,
                () -> reviewService.getReviewByUid(1234)
        );
    }
}
