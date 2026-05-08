package com.skkil.sync.like.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.like.enums.LikeTargetType;
import com.skkil.sync.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(
    name = "likes",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_likes_user_target",
            columnNames = {"user_id", "target_type", "target_id"}))
@Getter
public class Like extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(name = "target_type", nullable = false, length = 50)
  private LikeTargetType targetType;

  @Column(name = "target_id", nullable = false)
  private Long targetId;

  protected Like() {}

  @Builder
  public Like(User user, LikeTargetType targetType, Long targetId) {
    this.user = user;
    this.targetType = targetType;
    this.targetId = targetId;
  }
}
