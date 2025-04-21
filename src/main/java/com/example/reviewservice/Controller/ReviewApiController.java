package com.example.reviewservice.Controller;

import com.example.reviewservice.dto.ReviewRequestDTO;
import com.example.reviewservice.dto.ReviewResponseDTO;
import com.example.reviewservice.model.Review;
import com.example.reviewservice.service.ReviewService;
import com.example.reviewservice.type.ReviewStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewApiController {

    private final ReviewService reviewService;

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
}
