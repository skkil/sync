package com.skkil.sync.user.model;

import com.skkil.sync.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(
    name = "user_follow_relationships",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_user_follow_relationships_follower_followee",
          columnNames = {"follower_id", "followee_id"})
    })
@Getter
public class UserFollowRelationship extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "follower_id", nullable = false)
  private User follower;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "followee_id", nullable = false)
  private User followee;

  protected UserFollowRelationship() {}

  @Builder
  public UserFollowRelationship(User follower, User followee) {
    this.follower = follower;
    this.followee = followee;
  }
}
