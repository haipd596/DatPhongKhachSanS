package com.cntt.rentalmanagement.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cntt.rentalmanagement.domain.models.Review;
import com.cntt.rentalmanagement.domain.models.User;
import com.cntt.rentalmanagement.domain.payload.request.ReviewRequest;
import com.cntt.rentalmanagement.domain.payload.response.ReviewResponse;
import com.cntt.rentalmanagement.exception.ApiException;
import com.cntt.rentalmanagement.repository.ReviewRepository;
import com.cntt.rentalmanagement.repository.UserRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReviewResponse create(String email, ReviewRequest request) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ApiException("Khong tim thay user"));
        Review review = new Review(user, request.rating(), request.comment().trim());
        reviewRepository.save(review);
        return toResponse(review);
    }

    public List<ReviewResponse> latest() {
        return reviewRepository.findTop20ByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
    }

    public double avgRating() {
        return reviewRepository.avgRating().orElse(0.0);
    }

    private ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
            review.getId(),
            review.getUser().getFullName(),
            review.getRating(),
            review.getComment(),
            review.getCreatedAt()
        );
    }
}
