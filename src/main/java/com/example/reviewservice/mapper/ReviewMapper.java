package com.example.reviewservice.mapper;

import com.example.reviewservice.model.Review;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReviewMapper {
    void insertReview(Review review);

    default Review save(Review review) {
        insertReview(review); // insertReview를 호출하면 useGeneratedKeys 옵션에 의해 review.uid가 자동 채워짐
        return review;
    }

    List<Review> findAllReviews();
    Review findByUid(int uid);
    List<Review> findByUserUid(Integer userUid);
    int deleteByUid(int uid);
}
