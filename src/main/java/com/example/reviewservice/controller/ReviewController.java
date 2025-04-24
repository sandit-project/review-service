package com.example.reviewservice.controller;

import com.example.reviewservice.dto.ReviewDetailResponseDTO;
import com.example.reviewservice.dto.ReviewRequestDTO;
import com.example.reviewservice.dto.ReviewResponseDTO;
import com.example.reviewservice.model.Review;
import com.example.reviewservice.service.ReviewService;
import com.example.reviewservice.type.ReviewStatus;
import com.example.reviewservice.user.CustomUserDetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public List<ReviewDetailResponseDTO> findAllReviews() {
        log.info("findAllReviews");
        return reviewService.findAllReviews();
    }

    @GetMapping("/{uid}")
    public ReviewDetailResponseDTO getReviewByUid(@PathVariable int uid) throws NoSuchFieldException {
        log.info("get review by uid: {}", uid);
        return reviewService.getReviewByUid(uid);
    }

    @GetMapping("/user/{userUid}")
    public List<ReviewDetailResponseDTO> getReviewsByUserUid(@PathVariable Integer userUid) throws NoSuchFieldException {
        log.info("get reviews by user uid: {}", userUid);
        return reviewService.getReviewsByUserUid(userUid);
    }

    @PostMapping
    public ReviewResponseDTO writeReview(@RequestBody ReviewRequestDTO reviewRequestDTO){
        log.info("Review request DTO: " + reviewRequestDTO);
        return reviewService.writeReview(Review.builder()
                .userUid(reviewRequestDTO.getUserUid())
                .socialUid(reviewRequestDTO.getSocialUid())
                .orderUid(reviewRequestDTO.getOrderUid())
                .rate(reviewRequestDTO.getRate())
                .title(reviewRequestDTO.getTitle())
                .content(reviewRequestDTO.getContent())
                .status(ReviewStatus.ACTIVE)
                .build());
    }

    @DeleteMapping("/{uid}")
    public ReviewResponseDTO deleteReview(
            @PathVariable int uid,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("delete review by uid: {}, user: {}", uid, userDetails.getUid());
        return reviewService.deleteReview(uid, userDetails);
    }

}
