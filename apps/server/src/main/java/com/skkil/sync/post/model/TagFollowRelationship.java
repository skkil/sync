package com.skkil.sync.post.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.user.model.User;
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
    name = "tag_follow_relationships",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_tag_follow_relationships_follower_tag",
          columnNames = {"follower_id", "tag_id"})
    })
@Getter
public class TagFollowRelationship extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "follower_id", nullable = false)
  private User follower;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tag_id", nullable = false)
  private Tag tag;

  protected TagFollowRelationship() {}

  @Builder
  public TagFollowRelationship(User follower, Tag tag) {
    this.follower = follower;
    this.tag = tag;
  }
}
