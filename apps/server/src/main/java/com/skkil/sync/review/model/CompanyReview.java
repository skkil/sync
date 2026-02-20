package com.skkil.sync.review.model;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.model.Company;
import com.skkil.sync.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "company_reviews")
@Getter
public class CompanyReview extends Review {

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "review_details", columnDefinition = "jsonb")
  private CompanyReviewDetails reviewDetails;

  protected CompanyReview() {}

  @Builder
  public CompanyReview(
      User reviewer, Company provider, String textReview, CompanyReviewDetails reviewDetails) {
    super(ProviderType.COMPANY, reviewer, provider, textReview);
    this.reviewDetails = reviewDetails;
  }

  public static class CompanyReviewDetails implements ReviewDetails {
    public Double totalScore; // 총점
    public Double workCulture; // 회사 문화
    public Double salary; // 연봉
    public Double growthOpportunity; // 성장 기회
    public Double workLifeBalance; // 워라밸
  }

  @Override
  public void setReviewDetails(ReviewDetails reviewDetails) {
    if (reviewDetails instanceof CompanyReviewDetails details) {
      this.reviewDetails = details;
    } else {
      throw new IllegalArgumentException("Invalid review details type for CompanyReview");
    }
  }
}
