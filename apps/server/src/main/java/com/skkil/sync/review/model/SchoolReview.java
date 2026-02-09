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
  public SchoolReview(
      User reviewer, School provider, String textReview, SchoolReviewDetails reviewDetails) {
    super(ProviderType.SCHOOL, reviewer, provider, textReview);
    this.reviewDetails = reviewDetails;
  }

  public static class SchoolReviewDetails {
    public Integer totalScore; // 총점
    public Integer academicQuality; // 학문 수준
    public Integer campusFacilities; // 캠퍼스 시설
    public Integer studentLife; // 학생 생활
    public Integer valueForMoney; // 가성비
  }
}
