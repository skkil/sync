package com.skkil.sync.review.service;

import com.skkil.sync.provider.model.School;
import com.skkil.sync.review.dto.request.CreateReviewRequest;
import com.skkil.sync.review.mapper.ReviewMapper;
import com.skkil.sync.review.model.SchoolReview;
import com.skkil.sync.review.repository.ReviewRepository;
import com.skkil.sync.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final ReviewMapper reviewMapper;

  public ReviewService(ReviewRepository reviewRepository, ReviewMapper reviewMapper) {
    this.reviewRepository = reviewRepository;
    this.reviewMapper = reviewMapper;
  }

  @Transactional
  public void createReview(Long userId, Long providerId, CreateReviewRequest request) {
    User reviewer = new User(userId);

    var review =
        switch (request.type()) {
          case SCHOOL ->
              SchoolReview.builder()
                  .reviewer(reviewer)
                  .provider(new School(providerId))
                  .textReview(request.review())
                  .build();
          default -> throw new IllegalArgumentException("Unknown provider type: " + request.type());
        };

    var details = reviewMapper.toReviewDetails(request);
    review.setReviewDetails(details);

    log.debug("Creating review for userId={}, providerId={}", userId, providerId);
    reviewRepository.save(review);
  }
}
