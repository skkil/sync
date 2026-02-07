package com.skkil.sync.review.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.provider.model.Provider;
import com.skkil.sync.review.constant.ReviewType;
import com.skkil.sync.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reviews")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
public abstract class Review extends BaseEntity {

  @Enumerated(EnumType.STRING)
  @Column(name = "review_type", length = 50, nullable = false)
  private ReviewType type; // LAB, SCHOOL, COMPANY 중 하나

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reviewer_id", nullable = false)
  @Setter
  protected User reviewer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "provider_id", nullable = false)
  @Setter // 리뷰 대상의 id
  protected Provider provider;

  @Column(name = "text_review", columnDefinition = "TEXT")
  @Setter
  protected String textReview;

  protected Review() {}

  protected Review(ReviewType type, User reviewer, Provider provider, String textReview) {
    this.type = type;
    this.reviewer = reviewer;
    this.provider = provider;
    this.textReview = textReview;
  }
}
