package com.skkil.sync.review.repository;

import com.skkil.sync.review.model.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
  List<Review> findByProviderId(Long providerId);

  List<Review> findByReviewerId(Long reviewerId);

  List<Review> findByProviderIdAndReviewerId(Long providerId, Long reviewerId);
}
