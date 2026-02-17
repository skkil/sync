package com.skkil.sync.review.mapper;

import com.skkil.sync.review.dto.request.CreateReviewRequest;
import com.skkil.sync.review.dto.request.CreateSchoolReviewRequest;
import com.skkil.sync.review.model.ReviewDetails;
import com.skkil.sync.review.model.SchoolReview.SchoolReviewDetails;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

  default ReviewDetails toReviewDetails(CreateReviewRequest details) {
    return switch (details.type()) {
      case SCHOOL -> toSchoolReviewDetails((CreateSchoolReviewRequest) details);
      default -> throw new IllegalArgumentException("Unknown provider type: " + details.type());
    };
  }

  SchoolReviewDetails toSchoolReviewDetails(CreateSchoolReviewRequest details);
}
