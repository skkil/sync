package com.skkil.sync.review.model;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.model.Lab;
import com.skkil.sync.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "lab_reviews")
@Getter
public class LabReview extends Review {

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "review_details", columnDefinition = "jsonb")
  private LabReviewDetails reviewDetails;

  protected LabReview() {}

  @Builder
  public LabReview(User reviewer, Lab provider, String textReview, LabReviewDetails reviewDetails) {
    super(ProviderType.LAB, reviewer, provider, textReview);
    this.reviewDetails = reviewDetails;
  }

  public static class LabReviewDetails implements ReviewDetails {
    public Double totalScore; // 총점
    public Double professorPersonality; // 교수 인성
    public Double labAtmosphere; // 랩 분위기
    public Double workLifeBalance; // 워라밸
    public Double compensation; // 인건비
  }

  @Override
  public void setReviewDetails(ReviewDetails reviewDetails) {
    if (reviewDetails instanceof LabReviewDetails details) {
      this.reviewDetails = details;
    } else {
      throw new IllegalArgumentException("Invalid review details type for LabReview");
    }
  }
}
