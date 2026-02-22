package com.skkil.sync.review.model;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.model.School;
import com.skkil.sync.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "school_reviews")
@Getter
public class SchoolReview extends Review {

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "review_details", columnDefinition = "jsonb")
  private SchoolReviewDetails reviewDetails;

  protected SchoolReview() {}

  @Builder
  public SchoolReview(User reviewer, School provider, String textReview) {
    super(ProviderType.SCHOOL, reviewer, provider, textReview);
  }

  public static class SchoolReviewDetails implements ReviewDetails {
    public Double academicQuality; // 학문 수준
    public Double campusFacilities; // 캠퍼스 시설
    public Double studentLife; // 학생 생활
    public Double valueForMoney; // 가성비
  }

  @Override
  public void setReviewDetails(ReviewDetails reviewDetails) {
    if (reviewDetails instanceof SchoolReviewDetails details) {
      this.reviewDetails = details;
    } else {
      throw new IllegalArgumentException("Invalid review details type for SchoolReview");
    }
  }
}
