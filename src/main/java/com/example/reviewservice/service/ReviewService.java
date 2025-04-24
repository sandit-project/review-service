package com.example.reviewservice.service;

import com.example.reviewservice.dto.ReviewDetailResponseDTO;
import com.example.reviewservice.dto.ReviewResponseDTO;
import com.example.reviewservice.exception.OrderNotFoundException;
import com.example.reviewservice.exception.OrderServiceUnavailableException;
import com.example.reviewservice.mapper.ReviewMapper;
import com.example.reviewservice.model.Review;
import com.example.reviewservice.order.OrderClient;
import com.example.reviewservice.user.CustomUserDetails;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewMapper reviewMapper;
    private final OrderClient orderClient;

    public List<ReviewDetailResponseDTO> findAllReviews() {
        List<Review> reviews = reviewMapper.findAllReviews();
        return reviews.stream()
                .map(this::mapToDetailDTO)      // Review -> ReviewDetailResponseDTO
                .collect(Collectors.toList());
    }

    public ReviewResponseDTO writeReview(Review review) {
        // 1) Feign 으로 미리 주문 조회
        try {
            orderClient.getOrderByUid(review.getOrderUid());
        } catch (FeignException.NotFound e) {
            log.error("리뷰 작성 실패 - 주문 없음: orderUid={}", review.getOrderUid());
            throw new OrderNotFoundException(review.getOrderUid());
        } catch (FeignException e) {
            log.error("리뷰 작성 실패 - 주문 서비스 연결 실패: {}", e.getMessage());
            // 주문 서비스 자체가 죽어 있으면, 무조건 예외로 처리
            throw new OrderServiceUnavailableException();
        }

        // 2) insert 를 시도 + 무결성 제약 오류를 잡기
        try {
            reviewMapper.save(review);
        } catch (DataIntegrityViolationException ex) {
            // (a) DB constraint 로 튄 경우도 "주문 없음" 으로 간주
            log.error("리뷰 저장 실패 - FK 무결성 제약 위반, orderUid={}", review.getOrderUid(), ex);
            throw new OrderNotFoundException(review.getOrderUid());
        }

        return ReviewResponseDTO.builder()
                .isSuccess(true)
                .message("리뷰가 작성되었습니다.")
                .build();
    }

    public ReviewDetailResponseDTO getReviewByUid(int uid) throws NoSuchFieldException {
        Review review = reviewMapper.findByUid(uid);
        if (review == null){
            throw new NoSuchFieldException("해당 UID의 리뷰가 존재하지 않습니다. UID: " + uid);
        }
        return mapToDetailDTO(review);
    }

    public List<ReviewDetailResponseDTO> getReviewsByUserUid(Integer userId){
        List<Review> reviews = reviewMapper.findByUserUid(userId);
            return reviews.stream()
                .map(this::mapToDetailDTO)
                .collect(Collectors.toList());
    }

    //변환 메서드
    private ReviewDetailResponseDTO mapToDetailDTO(Review review) {
        return ReviewDetailResponseDTO.builder()
                .uid(review.getUid())
                .userUid(review.getUserUid())
                .socialUid(review.getSocialUid())
                .orderUid(review.getOrderUid())
                .rate(review.getRate())
                .title(review.getTitle())
                .content(review.getContent())
                .status(review.getStatus())
                .createdDate(review.getCreatedDate())
                .version(review.getVersion())
                .build();
    }

    public ReviewResponseDTO deleteReview(int uid, CustomUserDetails userDetails) {
        // 1) 리뷰 존재 여부
        Review review = reviewMapper.findByUid(uid);
        if (review == null) {
            return ReviewResponseDTO.builder()
                    .isSuccess(false)
                    .message("삭제할 리뷰를 찾을 수 없습니다.")
                    .build();
        }

        // 2) ADMIN 권한 여부 체크
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // 3) ADMIN 아니면 본인 작성 리뷰인지 확인
        if (!isAdmin && !review.getUserUid().equals(userDetails.getUid())) {
            return ReviewResponseDTO.builder()
                    .isSuccess(false)
                    .message("삭제 권한이 없습니다.")
                    .build();
        }

        // 4) 소프트 삭제 수행
        int rows = reviewMapper.deleteByUid(uid);
        if (rows == 1) {
            return ReviewResponseDTO.builder()
                    .isSuccess(true)
                    .message("리뷰가 성공적으로 삭제되었습니다.")
                    .build();
        } else {
            return ReviewResponseDTO.builder()
                    .isSuccess(false)
                    .message("리뷰 삭제에 실패했습니다.")
                    .build();
        }
    }
}
